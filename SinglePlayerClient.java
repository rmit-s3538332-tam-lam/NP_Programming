import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SinglePlayerClient extends SocketAgent{
    final static String SERVER_ADDRESS = "127.0.0.1";
    final static int PORT_ADDRESS = 1324;
    public static void main(String[] args) throws UnknownHostException,IOException{
        Socket cSocket = new Socket(SERVER_ADDRESS,PORT_ADDRESS);
        readMessage(cSocket);
        while(true){

        }
    
     

    }
    

}