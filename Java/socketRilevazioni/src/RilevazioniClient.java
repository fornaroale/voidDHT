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
	private static final String PORT_NAMES[] = { //scrivo il nome della porta che devo usare
            "COM3", // Windows
	};
	/**
        * Il BufferedReader che servirà come InputStreamReader
	* converte i bytes in caratteri
	* facendo i dati a display codepage independent
	*/
	private BufferedReader input;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;

        private ArrayList<Rilevazione> ril = new ArrayList<Rilevazione>();
        private Rilevazione temp = new Rilevazione();
        private int numRil = 0;	
    // -------------------------- DICHIARAZIONI PER COMUNICAZIONE SERIALE
    
    public void initialize() {             
        //Inizializzo vettore
        Rilevazione emptyRil = new Rilevazione();
        for(int i=0; i<100; i++){
            ril.add(emptyRil);
        }

        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in PORT_NAMES.
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
                // open serial port, and use class name for the appName.
                serialPort = (SerialPort) portId.open(this.getClass().getName(),
                                TIME_OUT);

                // set port parameters
                serialPort.setSerialPortParams(DATA_RATE,
                                SerialPort.DATABITS_8,
                                SerialPort.STOPBITS_1,
                                SerialPort.PARITY_NONE);

                // open the streams
                input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
                output = serialPort.getOutputStream();

                // add event listeners
                serialPort.addEventListener(this);
                serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
                System.err.println(e.toString());
        }
    }

    /**
     * This should be called when you stop using the port.
     * This will prevent port locking on platforms like Linux.
     */
    public synchronized void close() {
            if (serialPort != null) {
                    serialPort.removeEventListener();
                    serialPort.close();
            }
    }

    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                prendiValori();
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }

    public static void main(String[] args) throws Exception {
            RilevazioniClient main = new RilevazioniClient();
            main.initialize();
            Thread t=new Thread() {
                    public void run() {
                            //the following line will keep this app alive for 1000 seconds,
                            //waiting for events to occur and responding to them (printing incoming messages to console).
                            try {Thread.sleep(1000);} catch (InterruptedException ie) {}
                    }
            };
            t.start();
            System.out.println("Started");
    }

    public void prendiValori() throws IOException {
        String inputLine=input.readLine();
        String[] rivString = inputLine.split(",");
        temp.setData(Float.parseFloat(rivString[0]), Float.parseFloat(rivString[1]));

        if(numRil<=99) {
            System.out.println(temp);
            ril.set(numRil,temp);
        } else {
            //se ho 100 valori spedisco l'array al Server:
            inviaDati(ril);
            //.. e resetto il contatore:
            System.out.println("CENTO!");
            numRil=0;
            ril.set(numRil,temp);
        }
        numRil++;

        // PER CONTROLLO:
        //System.out.print("Posizione " + numRil + ": ");
        //Rilevazione tempRilDue = new Rilevazione();
        //tempRilDue = ril.get(numRil-1);
        //System.out.println(tempRilDue.getTemp() + " - " + tempRilDue.getHum());
    }

    public void inviaDati(ArrayList<Rilevazione> rilevazioni)
    {
        String serverName = "localhost";
        int port = Integer.parseInt("6066");
        try{
            System.out.println("Client: Mi sto connettendo a " + serverName + " sulla porta " + port);
            Socket client = new Socket(serverName, port);
            System.out.println("Client: Connesso al Server " + client.getRemoteSocketAddress());

            //OutputStream outToServer = client.getOutputStream();
            //DataOutputStream out = new DataOutputStream(outToServer);
            //out.writeUTF("Ciao Server, puoi darmi una lista di rilevazioni?" + client.getLocalSocketAddress());

            ObjectOutputStream objectOut = new ObjectOutputStream(client.getOutputStream());
            objectOut.writeObject(rilevazioni);

            client.close();
        }catch(Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }
}