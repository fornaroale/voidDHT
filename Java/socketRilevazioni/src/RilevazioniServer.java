import java.net.*;
import java.util.ArrayList;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
 
public class RilevazioniServer extends Thread
{
// -------------------------- DICHIARAZIONI SERVER SOCKET
   private ServerSocket serverSocket;
     
   public RilevazioniServer(int port) throws IOException
   {
      serverSocket = new ServerSocket(port);//Apro la porta del server
      serverSocket.setSoTimeout(120000);//tempo dopo cui la porta del server va in time out(120sec)
   }
 
   public void run()
   {
        while(true)
        {
            try
            {
                System.out.println("Server: Aspettando client sulla porta " + serverSocket.getLocalPort() + "...");
                Socket server = serverSocket.accept();//Accetto la richiesta di connessione fatta dal client al server 
                System.out.println("Server: Connesso a " + server.getRemoteSocketAddress());

                //DataInputStream in = new DataInputStream(server.getInputStream());
                //System.out.println("Server riceve messaggio da Client: "+in.readUTF());
                //DataOutputStream out = new DataOutputStream(server.getOutputStream());
                //out.writeUTF("Certo! Ecco la lista.");

                InputStream inFromClient = server.getInputStream();//Apro lo stream in input con il client
                DataInputStream in = new DataInputStream(inFromClient);//Apro lo stream per ricevere dati dal client
                ObjectInputStream objectIn = new ObjectInputStream(inFromClient);//Apro lo stream per ricevere oggetti dal client
                ArrayList<Rilevazione> rilevazioni = (ArrayList<Rilevazione>) objectIn.readObject();//ricevo l'arraylist delle rilevazioni
                for(int i=0; i<rilevazioni.size(); i++)//la scorro con un ciclo for
                {
                    scriviRilevazioneSuFile(rilevazioni.get(i).getTemp() + "," + rilevazioni.get(i).getHum());//infine la stampo su file
                }

                server.close();
            }catch(SocketTimeoutException s) {
                System.out.println("Server: Socket timed out!");
                break;
            }catch(IOException e) {
                e.printStackTrace();
                break;
            }catch(Exception e) {
                e.printStackTrace();
                System.out.println(e);
            }
        }
    }
    
  /**
		 Questo metodo prende la rilevazione ricevuta e la scrive su file
		 @param La rilevazione che deve essere scritta sul file
	*/
    public void scriviRilevazioneSuFile(String rilevazione){
        try(//Creazione dello stream per la scrittura su file
            FileWriter fw = new FileWriter("rilevazioni.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(rilevazione);//scrivo su file la rilevazione
        } catch (IOException e) {}
    }
   
    public static void main(String [] args)
    {
        int port = Integer.parseInt("6066");//Porta che devo utilizzare 
        try
        {
            Thread t = new RilevazioniServer(port);//creo un nuovo thread
            t.start();//faccio partire il thread
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}
