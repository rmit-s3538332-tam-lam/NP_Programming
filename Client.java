import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
public class Client extends SocketAgent {
    static private BufferedReader in;
    static private PrintWriter out;
    static private String localPlayerName;
    public static void main(String[] args) throws UnknownHostException, IOException{
        Thread quitThread = new QuitThread();
        quitThread.start();
        Socket s = new Socket(SERVER_ADDRESS,PORT_NUMBER);
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(s.getOutputStream(),true);
        sendNameToServer(s);
        waitToJoinGame(s);
      
    }  
    private static void sendNameToServer(Socket s) throws IOException{
        while(true){
            String line = in.readLine(); 
            if(line!=null){
                if(line.equals(GET_NAME)){
                    System.out.println("Getting name");
                    System.out.println("Please enter a unquie player name: " );

                    String playerName = getNameFromConsole();
                    out.println(playerName);
                }
                if(line.equals(NAME_ACCEPTED)){
                    System.out.println("Name accepted");
                    localPlayerName = line;
                    break;
                }
            }
        
        }
    }
    private static void waitToJoinGame(Socket s) throws IOException{
        System.out.println("Waiting to join game...");
        while(true){
            String message = in.readLine();
            if(message.contains(JOIN_GAME)){
                System.out.println("Joinning game...");
                break;
            }
        }

    }
    private static String getNameFromConsole() throws IOException{
        String playerName = null;
        InputStream in = System.in;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        while(true){
            if((line = reader.readLine())!= null){
                playerName = line;
                System.out.println("getNameFromConsole: playerName : "+ playerName);
                break;
            }
        }
        return playerName;
    }
}