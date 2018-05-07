import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class MServer extends SocketAgent {
    private static ArrayList<String> playerNames = new ArrayList<String>();
    private static int x;
    private static int[] secretCode;

    public static void main(String[] args) throws IOException {
        System.out.println("Server is running...");
        ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
        BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
        QuitThread quitThread = new QuitThread();
        quitThread.start();
        String line;
        try {
            while (true) {
                new ClientThread(serverSocket.accept()).start();
            }
        } finally {
            serverSocket.close();
        }

    }

    public static class ClientThread extends Thread {
        private String playerName;
        private BufferedReader in;
        private PrintWriter out;
        private Socket s;
        private int x;

        public ClientThread(Socket s) {

            this.s = s;
        }

        @Override
        public void run() {
            try {
                System.out.println("New client is connected...");
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                out = new PrintWriter(s.getOutputStream(), true);

                // getting player name and add it to playerQueue
                playerName = getPlayerNameFromClient(s);
                addPlayerToQueue(playerName);

                waitToJoinGame();
                System.out.println("Player: " + playerName + " joined game");

                gettingX();
                System.out.println("X accepted: " + x);

                // while 
                //if firts player --> generate secret code
                // if secret code is not empty --> out.println(...)
                //break;
                //client : wait until server generate secret code
                
                while (true) {
                    //First player's server thread generate secrete code
                    if(playerName.equals(playerNames.get(0))){
                        secretCode =  generateSecretCode(x);
                        System.out.println("Secret code is successfully generated");
                        System.out.println("Generated secret code: " + Arrays.toString(secretCode));
                        out.println(SECRET_CODE_GENERATED);
                        return;
                    }
                    //other player wait until code is generated
                    if(secretCode != null||secretCode.length!= 0){
                        System.out.println("Secret code is successfully generated");
                        out.println(SECRET_CODE_GENERATED);
                    }
                    try{
                        Thread.sleep(SLEEP_MILLISECOND);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (playerName != null) {
                    playerNames.remove(playerName);
                }
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        // if client is the first player, get X from client
        private void gettingX() throws IOException {
            while (true) {
                System.out.println("Getting X");
                if (playerName == playerNames.get(0)) {
                    System.out.println("Getting X from first player: " + playerName + " ....");
                    out.println(SUBMIT_X);
                    String line = in.readLine();
                    if (line != null) {
                        if (isNumeric(line)) {
                            if (convertStringToInt(line) >= 3 && convertStringToInt(line) <= 8) {
                                setX(convertStringToInt(line));
                                System.out.println("Selected X: " + x);
                                out.println(X_ACCEPTED);
                                return;
                            }
                        }
                    }
                }
                if (x != 0) {
                    out.println(X_ACCEPTED);
                }

                try {
                    Thread.sleep(SLEEP_MILLISECOND);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    ;
                }
            }
        }

        private synchronized void setX(int val) {
            x = val;
        }

        private void waitToJoinGame() {
            while (true) {
                int playerCount = getPlayerCount();
                for (int i = 0; i < playerCount; i++) {
                    String name = playerNames.get(i);
                    if (playerName.equals(name)) {
                        System.out.println("Player is going joinning game: " + playerName);
                        out.println(JOIN_GAME);
                        return;
                    }
                }
                out.println(WAIT_FOR_GAME);
                System.out.println("Waiting...");
                try {
                    Thread.sleep(SLEEP_MILLISECOND);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // get player count in a single game
        public int getPlayerCount() {
            int playerCount = 0;
            playerCount = playerNames.size() < PLAYER_COUNT ? playerNames.size() : PLAYER_COUNT;
            return playerCount;
        }

        public String getPlayerNameFromClient(Socket s) throws IOException {
            String playerName;
            while (true) {
                out.println(SUBMIT_NAME);
                System.out.println("Getting player  name...");
                playerName = in.readLine();
                if (playerName == null) {
                    return playerName;
                }
                if (!playerNames.contains(playerName)) {
                    break;
                }
            }
            out.println(NAME_ACCEPTED);
            return playerName;
        }

        public void addPlayerToQueue(String playerName) {
            synchronized (playerNames) {
                playerNames.add(playerName);
                if (playerNames.contains(playerName)) {
                }
            }
        }

        // generate unquie secret code
        private static int[] generateSecretCode(int size) {
            ArrayList<Integer> list = new ArrayList<>(11);
            for (int i = 0; i < 10; i++) {
                list.add(i);
            }
            int[] secretCode = new int[size];
            for (int count = 0; count < size; count++) {
                secretCode[count] = list.remove((int) (Math.random() * list.size()));
            }
            return secretCode;
        }

    }
}