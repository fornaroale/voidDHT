import java.net.*;
import java.util.ArrayList;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
 
public class RilevazioniServer extends Thread
{
   private ServerSocket serverSocket;
     
   public RilevazioniServer(int port) throws IOException
   {
      serverSocket = new ServerSocket(port);
      serverSocket.setSoTimeout(120000);//dopo 120 secondi la porta del Server va in time out
   }
 
   public void run()
   {
        while(true)
        {
            try
            {
                System.out.println("Server: Aspettando client sulla porta " + serverSocket.getLocalPort() + "...");
                Socket server = serverSocket.accept();
                System.out.println("Server: Connesso a " + server.getRemoteSocketAddress());

                //DataInputStream in = new DataInputStream(server.getInputStream());
                //System.out.println("Server riceve messaggio da Client: "+in.readUTF());
                //DataOutputStream out = new DataOutputStream(server.getOutputStream());
                //out.writeUTF("Certo! Ecco la lista.");

                InputStream inFromClient = server.getInputStream();
                DataInputStream in = new DataInputStream(inFromClient);

                ObjectInputStream objectIn = new ObjectInputStream(inFromClient);
                ArrayList<Rilevazione> rilevazioni = (ArrayList<Rilevazione>) objectIn.readObject();
                for(int i=0; i<rilevazioni.size(); i++)
                {
                    scriviRilevazioneSuFile(rilevazioni.get(i).getTemp() + "," + rilevazioni.get(i).getHum());
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
    
    public void scriviRilevazioneSuFile(String rilevazione){
        try(FileWriter fw = new FileWriter("rilevazioni.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(rilevazione);
        } catch (IOException e) {}
    }
   
    public static void main(String [] args)
    {
        int port = Integer.parseInt("6066");
        try
        {
            Thread t = new RilevazioniServer(port);
            t.start();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}