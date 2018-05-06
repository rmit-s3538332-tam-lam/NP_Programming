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
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class MultiPlayerServer extends SocketAgent {
    public static PriorityQueue<String> playerQueue = new PriorityQueue<String>();

    public static void main(String[] args) throws IOException {
        System.out.println("Server is running...");
        ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);
        Thread registerThread = new RegisterThread(serverSocket);
        registerThread.start();
        Thread quitThread = new QuitThread();
        quitThread.start();
        Thread gameThread = new GameThread();
        gameThread.start();
       
    }

    public static class RegisterThread extends Thread {
        ServerSocket serverSocket;

        public RegisterThread(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            try {
                registerClientThread(serverSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void registerClientThread(ServerSocket serverSocket) throws IOException {
            try {
                while (true) {
                    new ServerThread(serverSocket.accept()).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                serverSocket.close();
            }
        }

    }

    private static class GameThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(10000);
                    dequePlayerToJoinGame();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        
        private static synchronized void  dequePlayerToJoinGame() {
            System.out.println("Selecting player for a new game...");
            int pollCount = getPopCount();
            if (playerQueue.size() == 0) {
                // System.out.println("No player waiting in queue");
                return;
            }
            for (int i = 0; i < pollCount; i++) {
                String playerName = playerQueue.poll();
                System.out.println("New player has joined game: " + playerName);

            }
        }

        private static int getPopCount() {
            int pollCount = 0;
            if (playerQueue.size() < GAME_SPACE) {
                pollCount = playerQueue.size();
            } else {
                pollCount = GAME_SPACE;
            }
            return pollCount;
        }
    }

    private static class ServerThread extends Thread {
        static private BufferedReader in;
        static private PrintWriter out;
        static private String localPlayerName;
        private Socket s;

        public ServerThread(Socket s) {
            this.s = s;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                out = new PrintWriter(s.getOutputStream(), true);
                System.out.println("New theard started");
                getPlayerName(s);
                waitToJoinGame(s);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private static void waitToJoinGame(Socket s) {
            while (true) {
                if (playerQueue != null) {
                    if (!playerQueue.contains(localPlayerName)) {
                        out.println(JOIN_GAME);
                        break;
                    }
                }

            }
        }

        private static synchronized String getPlayerName(Socket s) throws IOException {
            String playerName;
            System.out.println("Getting client's player name...");
            while (true) {
                out.println(GET_NAME);
                playerName = in.readLine();
                if (playerName != null) {
                    if (!playerQueue.contains(playerName)) {
                        localPlayerName = playerName;
                        playerQueue.offer(playerName);

                        out.println(NAME_ACCEPTED);
                        System.out.println("Player name registered: " + playerName);
                        break;
                    }
                }
            }
            return playerName;
        }
    }

}