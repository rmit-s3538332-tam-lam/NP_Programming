import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class SinglePlayerServer {
    static final int PORT_NUMBER = 1324;
    public static void main(String[] args) {
        int x;
        // ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
        // Socket s = serverSocket.accept();
        // System.out.println("Please enter an integer between 3 - 8:");
        x = getX();
        int[] secretCode = generateSecretCode(x);
        System.out.println("Generated secrete code: " + Arrays.toString(secretCode));
        String guessCodeString = getGuessCodeString();


 
        
    }

    //use on client side to get x --> send x to server
    private static int getX(){
        int x  = 0;
        while(true){
            try{
                System.out.println("Please enter an integer between 3 - 8:");
                InputStream inStream = System.in;
                BufferedReader  reader =  new BufferedReader(new InputStreamReader(inStream));
                String line = null;
                if((line = reader.readLine())!=null){
                    //if input is x quit program
                    quittingOnX(line);
                    if(isNumeric(line)){
                        if(convertStringToInt(line) >= 3  && convertStringToInt(line)<=8){
                            x = convertStringToInt(line);
                            System.out.println("Selected x: "+  x);
                            break;
                        }
                    }
                }
            } catch (IOException  e){
                e.printStackTrace();
            }
        }
        return x;
    }
    //generate unqiue combo secret code
    private static int[] generateSecretCode(int size){
        ArrayList<Integer> list = new ArrayList<>(11);
        for (int i = 0; i <= 10; i++){
            list.add(i);
        }
        int[] secretCode = new int[size];
        for (int count = 0; count < size; count++){
            secretCode[count] = list.remove((int)(Math.random() * list.size()));
        }
        return secretCode;
    }
    private static String getGuessCodeString(){
        String guessCodeString = null;
        try{
            guessCodeString = null;
            InputStream inStream = System.in;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            String line = null;
            while(true){
                System.out.println("Please enter guess code: ");
                if((line = reader.readLine())!=null){
                    if(isNumeric(line)){
                        guessCodeString = line;
                        System.out.println("Guess code entered: "+guessCodeString);
                        break;
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return guessCodeString;
    }
    
  
    //////Utils////
    public static int[] convertStringToIntArray(String line){
        int[] intArray = new int[line.length()];
        for (int i = 0; i<line.length();i++){
            intArray[i] = line.charAt(i)-'0';
        }
        return intArray;  
    }
    private static void quittingOnX(String line){
        if(line.equalsIgnoreCase("x")) {
            System.out.println("Quitting...");
            System.exit(0);
        }
    }
    private static int convertStringToInt(String valueString) throws NumberFormatException {
        return Integer.parseInt(valueString);
    }
    private static boolean isNumeric(String str){
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