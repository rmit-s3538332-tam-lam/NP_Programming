
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class MClient extends SocketAgent {
    private BufferedReader in;
    private PrintWriter out;

    public static void main(String[] args) throws UnknownHostException, IOException {
        MClient client = new MClient();
        client.run();
    }

    public void run() throws IOException {
        Socket s = new Socket(SERVER_ADDRESS, PORT_NUMBER);
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(s.getOutputStream(), true);
        sendingPlayerName();
        waitToJoinGame();
        
        // geting x if first player
        while(true){
            String line = in.readLine();
            if(line.equals(SUBMIT_X)){
                String x = getXFromConsole();
                System.out.println("Getting X main loop..");
                out.println(getXFromConsole());
            }
            if(line.equals(X_ACCEPTED)){
                System.out.println("Server accepted X");
                break;
            }
        }

    }

    private void waitToJoinGame() throws IOException {
        while (true) {
            String line = in.readLine();
            if (line.equals(JOIN_GAME)) {
                System.out.println("Joinning game... ");
                break;
            }
        }
        System.out.println("Play game...");
    }

    private void sendingPlayerName()throws IOException{
        while (true) {
            String line = in.readLine();
            if (line.equals(SUBMIT_NAME)) {
                System.out.println("getNameFromConsole()");
                out.println(getNameFromConsole());
            }
            if (line.equals(NAME_ACCEPTED)) {
                System.out.println("Name accepted");
                break;
            }
        }
    }

    private String getNameFromConsole() throws IOException {
        System.out.println("Please enter a unquie player name:");
        String playerName = null;
        InputStream in = System.in;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        while (true) {
            String line = reader.readLine();
            if (line != null && line.trim().length() > 0) {
                playerName = line;
                System.out.println("Name: " + playerName);
                break;
            }
        }
        return playerName;
    }

    public static String getXFromConsole() throws IOException{
        System.out.println("You are the first player. \nPlease enter a X integer from 3-8:");
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
}