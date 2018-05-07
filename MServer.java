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
    private static HashSet<String> playerNames = new HashSet<String>();

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
                String playerName = getPlayerNameFromClient(s);
                addPlayerToQueue(playerName);
                
                //

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
            System.out.println("Player  name:" + playerName);
            return playerName;
        }

        public void addPlayerToQueue(String playerName) {
            synchronized (playerNames) {
                playerNames.add(playerName);
            }
        }
    }
}