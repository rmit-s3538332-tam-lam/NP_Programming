import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SinglePlayerClient extends SocketAgent{

    public static void main(String[] args) throws UnknownHostException,IOException{
        Socket s = new Socket(SERVER_ADDRESS,PORT_NUMBER);
        sendingXtoServer(s);
        waitToStartGuessing(s);


    }
    public static String getXFromConsole() throws IOException{
        String xString = null;
        InputStream in = System.in;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        while(true){
            if((line = reader.readLine())!= null){
                xString = line;
                break;
            }
        }
        return xString;
    }
    public static void sendingXtoServer(Socket s)throws IOException{
        while(true){
            String message = readMessage(s);
            if(message.equals(X_ACCEPTED_MESSAGE))break;
            String xString = getXFromConsole();
            sendMessage(s, xString);
        }
    }
    public static void guessing(Socket s) {
        
    }
    public static boolean  waitToStartGuessing(Socket s){
        Boolean startGuessing = false;
        while(true){
            String message = readMessage(s);
            if(message.equals(START_GUESSING_MESSAGE)) {
                startGuessing = true;
                break;
            }
        }
        return startGuessing;
    }
}