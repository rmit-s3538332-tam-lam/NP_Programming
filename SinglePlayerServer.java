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
        System.out.println(Arrays.toString(secretCode));
 
        
    }
    public static int getX(){
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
