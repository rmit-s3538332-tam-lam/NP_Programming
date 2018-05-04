import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;


public class SinglePlayerServer extends SocketAgent {
    public static void main(String[] args)throws IOException {
        ServerSocket serverSocket = null;
        int x;
        try{
            
            serverSocket = new ServerSocket(PORT_NUMBER);
            Socket sSocket = serverSocket.accept();
            String message = "Please enter integer from 3 to 8:";            
            sendMessage(sSocket, message);
            x = getXFromClient(sSocket);
        }

        finally{
            serverSocket.close();
        }
    }

    private static int getXFromClient(Socket s){
        int x  = 0;
        while(true){
            try{
                String message = "Please enter integer from 3 to 8:";
                sendMessage(s, message);
                // System.out.println("Please enter an integer between 3 - 8:");
                InputStream inStream = s.getInputStream();
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
    
     //server
     private static int getCorrectPosition(int[] secretCode,int[] guessCode){
        int correctPosition  = 0;
        if(isMatch(secretCode, guessCode)){
            return secretCode.length;
        }else{
            int i = 0;
            while(i<secretCode.length && i<guessCode.length){
                if(secretCode[i] == guessCode[i]){
                    correctPosition++;
                }
                i++;
            }
        }
        return correctPosition;
    }
    //server
    private static int getTotalMatchPosition(int[]secretCode,  int[] guessCode){
        int[] largeTempArray = null;
        int[] smallTempArray = null;
        int matchPosition = 0;
        if (secretCode.length > guessCode.length){
            largeTempArray = secretCode;
            smallTempArray  = guessCode;
        } else{
            largeTempArray = guessCode;
            smallTempArray = secretCode;
        }
        for(int i = 0; i<largeTempArray.length ; i++){
            int number = largeTempArray[i];
            boolean contains = IntStream.of(smallTempArray).anyMatch(x -> x == number);
            if (contains){
                matchPosition +=1;
            }
        }
        return matchPosition;
    }
    //server
    private static int getIncorrectPosition(int[] secretCode, int[] guessCode){
        int totalMatchPosition = getTotalMatchPosition(secretCode, guessCode);
        int correctPosition = getCorrectPosition(secretCode, guessCode);
        int incorrectPosition = totalMatchPosition - correctPosition;
        return incorrectPosition;
    }
    //server
    private static boolean isMatch(int[] secretCode, int[] guessCode){
        if(Arrays.equals(secretCode, guessCode)) {
            System.out.println("Matched!!");
            return true;
        }
        return false;
    }
    //server
    private static String getFinalMessage(boolean match, int attemptCount, int[] secretcode){
        
        String winOrLose = match? "You won!" : "You lose...";
        String message = "============================================\n"+
         winOrLose + "\nNumber of Attempt: "+
         attemptCount +  "\nSecret code: " + Arrays.toString(secretcode) +
         "\n============================================";
        return message;
    }
       //generate unqiue combo secret code
    private static int[] generateSecretCode(int size){
        ArrayList<Integer> list = new ArrayList<>(11);
        for (int i = 0; i < 10; i++){
            list.add(i);
        }
        int[] secretCode = new int[size];
        for (int count = 0; count < size; count++){
            secretCode[count] = list.remove((int)(Math.random() * list.size()));
        }
        return secretCode;
    }

}