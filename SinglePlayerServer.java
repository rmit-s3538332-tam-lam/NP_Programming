import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SinglePlayerServer {
    static final int PORT_NUMBER = 1324;
    public static void main(String[] args) {
        // ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
        // Socket s = serverSocket.accept();
        System.out.println("Please enter an integer between 3 - 8:");
        getX();
        
    }
    public static int getX(){
        int x  = 0;
        while(true){
            try{
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
                    }else{
                        System.out.println("Please enter an integer between 3 - 8:");
                    }
                  
                } else{
                    System.out.println("Please enter an integer between 3 - 8:");
                }
            } catch (IOException  e){
                e.printStackTrace();
            }
        }
        return x;
    }
    private static void quittingOnX(String line){
        if(line.equalsIgnoreCase("x")) System.exit(0);
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
