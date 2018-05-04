import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SinglePlayerServer extends SocketAgent {
    static final int PORT_NUMBER = 1324;
    public static void main(String[] args)throws IOException {
        ServerSocket serverSocket = null;
        try{
            serverSocket = new ServerSocket(PORT_NUMBER);
            Socket sSocket = serverSocket.accept();
            String message = "Please enter integer from 3 to 8:";            
            sendMessage(sSocket, message);
            
        }

        finally{
            serverSocket.close();
        }
    }

    // private static int getX(Socket s){
    //     int x  = 0;
    //     while(true){
    //         try{
    //             String message = "Please enter integer from 3 to 8:";
    //             sendMessage(s, message);
    //             // System.out.println("Please enter an integer between 3 - 8:");
    //             InputStream inStream = s.getInputStream();
    //             BufferedReader  reader =  new BufferedReader(new InputStreamReader(inStream));
    //             String line = null;
    //             if((line = reader.readLine())!=null){
    //                 //if input is x quit program
    //                 quittingOnX(line);
    //                 if(isNumeric(line)){
    //                     if(convertStringToInt(line) >= 3  && convertStringToInt(line)<=8){
    //                         x = convertStringToInt(line);
    //                         System.out.println("Selected x: "+  x);
    //                         break;
    //                     }
    //                 }
    //             }
    //         } catch (IOException  e){
    //             e.printStackTrace();
    //         }
    //     }
    //     return x;
    // }
    public static void sendMessage(Socket s, String message){
        try{
            PrintWriter out = new PrintWriter(s.getOutputStream(),true);
            out.println(message);
            System.out.println("Message sent: "+ message);
        } catch (IOException e){
            e.printStackTrace();
        }
        
    }
}