import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

import javax.swing.plaf.synth.SynthToolTipUI;

public class SinglePlayerServer {
    static final int PORT_NUMBER = 1324;
    public static void main(String[] args) {
        int attemptCount = 1;
        play(attemptCount);
        
    }
    private static void play(int attemptCount){
        //server
        Boolean match = false;
        int x = getX();
        int[] secretCode = generateSecretCode(x);
        System.out.println("Generated secrete code: " + Arrays.toString(secretCode));

        while((attemptCount <= 10) && (match!= true)){
            //server
            attemptCount+= 1;
            //client
            String guessCodeString = getGuessCodeString();
            //server
            int[] guessCode = convertStringToIntArray(guessCodeString);
            System.out.println("Guess secrete code: " + Arrays.toString(guessCode));
            if(isMatch(secretCode, guessCode)){
                match = true;
                break;
            }
            System.out.println("Correct position: "+ getCorrectPosition(secretCode, guessCode));
            System.out.println("Total match position: "+ getTotalMatchPosition(secretCode, guessCode));
        }
        if(match == true || attemptCount == 11){
            System.out.println(getFinalMessage(match ,attemptCount-1, secretCode));
        }
        
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
        for (int i = 0; i < 10; i++){
            list.add(i);
        }
        int[] secretCode = new int[size];
        for (int count = 0; count < size; count++){
            secretCode[count] = list.remove((int)(Math.random() * list.size()));
        }
        return secretCode;
    }
    //client
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
