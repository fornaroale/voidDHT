import java.net.*;
import java.util.ArrayList;
import java.io.*;
import java.util.Enumeration;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import static jdk.nashorn.tools.ShellFunctions.input;
 


public class RilevazioniClient implements SerialPortEventListener {
    // -------------------------- DICHIARAZIONI PER COMUNICAZIONE SERIALE    
        SerialPort serialPort;//dichiara oggetto serial port
	private static final String PORT_NAMES[] = { //porta che uso per interfacciarmi con arduino
            "COM3", // nome della porta
	};
	// Dichiaro il BufferedReader che servirà come InputStreamReader e mi converte i bytes in caratteri
	private BufferedReader input;
	//Dichiaro l'outputstream
	private OutputStream output;
	// Millisecondi da aspettare per l'apertura della porta
	private static final int TIME_OUT = 2000;
	//Bit che la porta COM passa al secondo
	private static final int DATA_RATE = 9600;

	// -------------------------- DICHIARAZIONI PER LA MEMORIZZAZIONE DELLE RILEVAZIONI
	//ArrayList di rilevazioni che poi andrò ad inviare
        private ArrayList<Rilevazione> ril = new ArrayList<Rilevazione>();
	//Variabile temporanea del tipo rilevazione dove metterò i miei dati prima di metterli nell' ArrayList 
        private Rilevazione temp = new Rilevazione();
	//Contatore che mi serve a capire quando la mia ArrayList è piena
        private int numRil = 0;	
	
   // -------------------------- METODI DELLA CLASSE CLIENT
   /**
	Metodo che serve ad inizializzare tutti i parametri per la comunicazione con la porta seriale 
	e anche inizializzare gli oggetti per la memorizzazione
   */
    public void initialize() {             
        //Inizializzo l'arraylist di rilevazioni
        Rilevazione emptyRil = new Rilevazione();
        for(int i=0; i<100; i++){
            ril.add(emptyRil);
        }

        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

      
        while (portEnum.hasMoreElements()) {
                CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
                for (String portName : PORT_NAMES) {
                        if (currPortId.getName().equals(portName)) {
                                portId = currPortId;
                                break;
                        }
                }
        }
        if (portId == null) {
                System.out.println("Could not find COM port.");
                return;
        }

        try {
                 // apro la mia porta seriale
                serialPort = (SerialPort) portId.open(this.getClass().getName(),
                                TIME_OUT);

                // setto i parametri della porta seriale
                serialPort.setSerialPortParams(DATA_RATE,
                                SerialPort.DATABITS_8,
                                SerialPort.STOPBITS_1,
                                SerialPort.PARITY_NONE);

                // apro gli stream
                input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
                output = serialPort.getOutputStream();

                // aggiungo gli EventListener (cioè coloro che ascoltano la porta seriale e aspettano un evento)
                serialPort.addEventListener(this);
                serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
                System.err.println(e.toString());
        }
    }

 	/**
		Metodo che richiamo quando non devo più usare la porta seriale e quindi la chiudo
	*/
    public synchronized void close() {
            if (serialPort != null) {
                    serialPort.removeEventListener();
                    serialPort.close();
            }
    }

  	/**
		Metodo che verrà richiamato ad ogni evento della serial port che prende i dati e me li inserisce in un arraylist
		@param passo l'evento che si è verificato sulla porta
   	 */
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                prendiValori();
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
    }

    public static void main(String[] args) throws Exception {
            RilevazioniClient main = new RilevazioniClient();
            main.initialize();
            Thread t=new Thread() { //creo il thread
                    public void run() {
			    //la linea seguente mantiene in vita la applicazione per 1000 millisecondi aspettando l'evento 
                            try {Thread.sleep(1000);} catch (InterruptedException ie) {}
                    }
            };
            t.start();//faccio partire il thread
            System.out.println("Started");
    }
	
	/**
		Questo metodo prende i valori della serile li splitta e li mette in una arraylist
		poi quando arrivano a 100 li invia al server
	*/
    public void prendiValori() throws IOException {
        String inputLine=input.readLine();//creo una stringa temporanea dove metto i dati che mi arrivano dalla seriale
        String[] rivString = inputLine.split(",");//splitto la mia stringa
        temp.setData(Float.parseFloat(rivString[0]), Float.parseFloat(rivString[1]));//la metto nel mio oggetto rilevazione temporaneo

        if(numRil<=99) {//se ho inserito nel vettore meno di 100 valori continuo ad inserire 
            System.out.println(temp);
            ril.set(numRil,temp);//metto nel mio arraylist di rilevazioni il mio oggetto temporaneo
        } else {
            //se ho 100 valori spedisco l'array al Server:
            inviaDati(ril);
            //.. e resetto il contatore:
            System.out.println("CENTO!");
            numRil=0;
            ril.set(numRil,temp);
        }
        numRil++;//incremento il contatore

        // PER CONTROLLO:
        //System.out.print("Posizione " + numRil + ": ");
        //Rilevazione tempRilDue = new Rilevazione();
        //tempRilDue = ril.get(numRil-1);
        //System.out.println(tempRilDue.getTemp() + " - " + tempRilDue.getHum());
    }
	
	/**
		Questo metodo viene richiamato ogni 100 rilevazioni e prende l'arraylist
		e la invia tramite le socket al server
		@param l'arraylist delle rilevazioni che deve inviare
	*/
    public void inviaDati(ArrayList<Rilevazione> rilevazioni)
    {
        String serverName = "localhost";//nome del server
        int port = Integer.parseInt("6066");//porta utilizzata
        try{
            System.out.println("Client: Mi sto connettendo a " + serverName + " sulla porta " + port);
            Socket client = new Socket(serverName, port);//creo una socket passandogli il nome del server e la porta
            System.out.println("Client: Connesso al Server " + client.getRemoteSocketAddress());

            //OutputStream outToServer = client.getOutputStream();
            //DataOutputStream out = new DataOutputStream(outToServer);
            //out.writeUTF("Ciao Server, puoi darmi una lista di rilevazioni?" + client.getLocalSocketAddress());
	
	//creo uno stream che mi permetterà di inviare dati al server con cui sono connesso tramite la socket:	
            ObjectOutputStream objectOut = new ObjectOutputStream(client.getOutputStream());
            objectOut.writeObject(rilevazioni);//mando il mio arraylist di rilevazioni al server

            client.close();// chiudo la socket
        }catch(Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }
}
