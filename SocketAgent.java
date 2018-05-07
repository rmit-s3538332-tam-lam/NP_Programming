import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.IntStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

//Storing common  and utility functions used by Clients and Servers
public  abstract class SocketAgent{
    final static int PLAYER_COUNT = 2;
    final static String SERVER_ADDRESS = "127.0.0.1";
    final static int PORT_NUMBER = 6073;
    final static String X_ACCEPTED_MESSAGE = "Server accepted X";
    final static String START_GUESSING_MESSAGE = "Please enter your guess";
    final static String WIN_MESSAGE = "You won!";
    final static String LOSE_MESSAGE  = "You lost!";
    final static String NAME_ACCEPTED = "NAME_ACCEPTED";
    final static String SUBMIT_NAME = "SUBMIT_NAME";
    final static String JOIN_GAME = "JOIN_GAME";
    final static String WAIT_FOR_GAME = "WAIT_FOR_GAME";
    final static int SLEEP_MILLISECOND = 3000;
    final static String SUBMIT_X = "SUBMIT_X";
    final static String X_ACCEPTED = "X_ACCEPTED";
    final static String SECRET_CODE_GENERATED = "SECRET_CODE_GENERATED";
    final static String HINT_MESSAGE = "HINT_MESSAGE";
    final static String SUBMIT_GUESS = "SUBMIT_GUESS";
    final static String FINISH_PLAYING = "FINISH_PLAYING";
    final static String GAME_ENDED = "GAME_ENDED";
    final static String FORFEIT = "f";
    final static int FORFEIT_ATTEMPT_COUNT = 11;

    //read and print a String message from socket
    public static String readMessage(Socket s){
        String message  = null;
        try{
            BufferedReader inMessage = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line  = null;
            if((line = inMessage.readLine())!=null){
                System.out.println(line);
                message = line;
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return message;
    }
    public static class QuitThread extends Thread{
        @Override
        public void run (){
            try{
                InputStream in =  System.in;
                BufferedReader reader = new  BufferedReader(new InputStreamReader(in));
                String  line = null;
                if ((line =  reader.readLine())!= null){
                    if (line.equals("q")){
                        System.exit(0);
                    }
                }
            } catch(IOException  e){
                e.printStackTrace();
            }   
        }
    }
    //send string message through socket
    public static void sendMessage(Socket s, String message){
        try{
            PrintWriter out = new PrintWriter(s.getOutputStream(),true);
            out.println(message);
            // System.out.println("Message sent: "+ message);
        } catch (IOException e){
            e.printStackTrace();
        }
        
    }
  

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

    //check if a code in string form contain unquie combo
    public static boolean isUnquie(String codeLine){
        int[] code = convertStringToIntArray(codeLine);
        Set<Integer> setUniqueNumbers = new LinkedHashSet<Integer>();
        for(int x : code) {
            setUniqueNumbers.add(x);
        }
        if(code.length > setUniqueNumbers.size()) return false;
        return true;
    }
    

}