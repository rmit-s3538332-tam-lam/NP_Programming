import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public  abstract class SocketAgent{
    final static String SERVER_ADDRESS = "127.0.0.1";
    final static int PORT_NUMBER = 1324;
    
    // Socket cSocket;
    public static void readMessage(Socket cSocket){
        try{
            BufferedReader inMessage = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
            String line  = null;
            if((line = inMessage.readLine())!=null){
                System.out.println("Server: "+ line);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void sendMessage(Socket s, String message){
        try{
            PrintWriter out = new PrintWriter(s.getOutputStream(),true);
            out.println(message);
            System.out.println("Message sent: "+ message);
        } catch (IOException e){
            e.printStackTrace();
        }
        
    }

     //////Utils////
     public static int[] convertStringToIntArray(String line){
        int[] intArray = new int[line.length()];
        for (int i = 0; i<line.length();i++){
            intArray[i] = line.charAt(i)-'0';
        }
        return intArray;  
    }
    public static void quittingOnX(String line){
        if(line.equalsIgnoreCase("x")) {
            System.out.println("Quitting...");
            System.exit(0);
        }
    }
    public static int convertStringToInt(String valueString) throws NumberFormatException {
        return Integer.parseInt(valueString);
    }
    public static boolean isNumeric(String str){
        try  
        {  
          int i = Integer.parseInt(str);  
        }  
        catch(NumberFormatException e)  
        {  
          return false;  
        }  
        return true; 
    } 

}