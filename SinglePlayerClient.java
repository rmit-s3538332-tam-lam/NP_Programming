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
        guessing(s);

    }

   //keep sending X to server until a valid X is accepted by Server
   public static void sendingXtoServer(Socket s)throws IOException{
        while(true){
            String message = readMessage(s);
            if(message.equals(X_ACCEPTED_MESSAGE))break;
            String xString = getXFromConsole();
            sendMessage(s, xString);
            }
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
 
    //making, sending guesses and getting responses from server until winning or lose
    public static void guessing(Socket s) {
        while(true){
            String guess = getGuessCodeString();
            sendMessage(s, guess);
            String message = null;
            if((message = readMessage(s))!=null){
                if (message.contains(WIN_MESSAGE)|| message.contains(LOSE_MESSAGE)){
                    break;
                }
            }
        }
    }

    //making sure server generate secret code before guessing
    public static void  waitToStartGuessing(Socket s){
        while(true){
            String message = null;
            if((message = readMessage(s))!=null){
                if(message.equals(START_GUESSING_MESSAGE)){
                    System.out.println("Start  guessing...");
                    break;
                }
            }
            
        }
    }

    //Getting  guess code from console's input
    private static String getGuessCodeString(){
        String guessCodeString = null;
        try{
            guessCodeString = null;
            InputStream inStream = System.in;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            String line = null;
            while(true){
                System.out.println("Please enter an unquie number guess code: ");
                if((line = reader.readLine())!=null){
                    if(isNumeric(line) && isUnquie(line)){
                        guessCodeString = line;
                        System.out.println("Guess code an number entered: "+guessCodeString);
                        break;
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return guessCodeString;
    }
    
}