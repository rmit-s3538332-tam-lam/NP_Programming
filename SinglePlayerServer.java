import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;


public class SinglePlayerServer extends SocketAgent {
    public static void main(String[] args)throws IOException {
        ServerSocket serverSocket = null;
        int x;
        int[] secretCode;
        try{
            
            serverSocket = new ServerSocket(PORT_NUMBER);
            Socket s = serverSocket.accept();
            x = getXFromClient(s);
            secretCode = generateSecretCode(x);
            System.out.println("Generated secret code: " + Arrays.toString(secretCode));
            sendMessage(s, START_GUESSING_MESSAGE);
            play(s,secretCode);
        }
        finally{
            serverSocket.close();
        }
    }
    private static void play(Socket s,int[] secretCode){
       
        Boolean match = false;
        int attemptCount = 1;
       
        while(attemptCount<10 && !match){
            int correctPosition = 0;
            int incorrectPosition = 0;
            String  guessCodeString = readMessage(s);
            int[] guessCode = convertStringToIntArray(guessCodeString);
            System.out.println("Guess secrete code: " + Arrays.toString(guessCode));

            if(isMatch(secretCode, guessCode)){
                match =true;
                break;
            }
            correctPosition = getCorrectPosition(secretCode, guessCode);
            incorrectPosition = getIncorrectPosition(secretCode, guessCode);
            String hintMessage = "Correct Position: "+ correctPosition  +  
            "         Incorrect Position: "+ incorrectPosition;
            sendMessage(s, hintMessage);    
            // if(attemptCount  == 9 ){
            //     sendWinOrLoseMessage(s, match, attemptCount, secretCode);
            // }
            attemptCount +=1;
        }
        sendWinOrLoseMessage(s, match, attemptCount, secretCode);

        
    }
    private static void  sendWinOrLoseMessage(Socket s, Boolean match, int attemptCount, int[] secretCode){
        String message =  match? WIN_MESSAGE : LOSE_MESSAGE;
        message += " Secret code: "+ Arrays.toString(secretCode) + "     Attempt count: "+ attemptCount;
        System.out.println(message);
        sendMessage(s, message);
    }
    private static int getXFromClient(Socket s){
        int x  = 0;
        while(true){
            try{
                System.out.println("Getting X from client...");
                String message = "Please enter an integer from 3 to 8:";
                sendMessage(s, message);
                InputStream inStream = s.getInputStream();
                BufferedReader  reader =  new BufferedReader(new InputStreamReader(inStream));
                String line = null;
                if((line = reader.readLine())!=null){
                    //if input is x quit program
                    quittingOnX(line);
                    if(isNumeric(line)){
                        if(convertStringToInt(line) >= 3  && convertStringToInt(line)<=8){
                            x = convertStringToInt(line);
                            System.out.println("Selected X: "+  x);
                            sendMessage(s, X_ACCEPTED_MESSAGE);
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
    private static int getIncorrectPosition(int[] secretCode, int[] guessCode){
        int totalMatchPosition = getTotalMatchPosition(secretCode, guessCode);
        int correctPosition = getCorrectPosition(secretCode, guessCode);
        int incorrectPosition = totalMatchPosition - correctPosition;
        return incorrectPosition;
    }
    private static boolean isMatch(int[] secretCode, int[] guessCode){
        if(Arrays.equals(secretCode, guessCode)) {
            System.out.println("Matched!!");
            return true;
        }
        return false;
    }
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