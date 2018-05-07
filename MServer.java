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
import java.util.stream.IntStream;

public class MServer extends SocketAgent {
    private static ArrayList<String> playerNames = new ArrayList<String>();
    private static int x = 0;
    private static boolean isXValid = false;

    private static int[] secretCode;

    // Strong attempt count for player (1-3) in current game
    private static int[] attemptCounts = new int[PLAYER_COUNT];

    // Array storing players status in current game
    // false: player still playing
    // true: player has finished the game
    private static boolean[] isPlayersFinish = new boolean[PLAYER_COUNT];

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
        private boolean replay;

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

                gettingSecretCode();
                System.out.println(playerName + ": Generated secret code: " + Arrays.toString(secretCode));

                play();

                udpateIsPlayersFinish(playerName, true);
                System.out.println(playerName + ":has finished the game");
                waitForOtherPlayerToFinish();
                System.out.println("Everyone has finish their game");
                out.println(EVERYONE_FINISHED);
                announceRanking();
                replayPromting();
                System.out.println("Replay: " + replay);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (playerName != null) {
                    // playerNames.remove(playerName);
                }
                // attemptCounts = new int[PLAYER_COUNT];
                // isPlayersFinish = new boolean[PLAYER_COUNT];
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        public void replayPromting() throws IOException {
            System.out.println("Prompting client to replay:");
            out.println(REPLAY_PROMPT);
            String line;
            while (true) {
                line = in.readLine();
                if (line != null) {
                    if (line.equals(QUIT)) {
                        System.out.println(playerName+ " : choose to replay");
                        replay = false;
                        return;
                    }
                    if (line.equals(REPLAY)) {
                        replay = true;
                        System.out.println(playerName+ " : choose to replay");
                        return;
                    }
                }
            }
        }

        public void play() throws IOException {
            Boolean match = false;
            boolean isForfeited = false;
            int attemptCount;
            for (attemptCount = 1; attemptCount <= 10; attemptCount++) {
                if (!match) {
                    int correctPosition = 0;
                    int incorrectPosition = 0;
                    out.println(SUBMIT_GUESS);
                    String guessCodeString = in.readLine();
                    if (guessCodeString.equals(FORFEIT)) {
                        System.out.println(playerName + ": forfeit game");
                        isForfeited = true;
                        System.out.println("Forfeit:" + isForfeited);
                        updateAttemptCount(playerName, FORFEIT_ATTEMPT_COUNT);
                        break;
                    }
                    int[] guessCode = convertStringToIntArray(guessCodeString);
                    System.out.println("Guess secrete code: " + Arrays.toString(guessCode));

                    if (isMatch(secretCode, guessCode)) {
                        match = true;
                        updateAttemptCount(playerName, attemptCount);
                        break;
                    }
                    correctPosition = getCorrectPosition(secretCode, guessCode);
                    incorrectPosition = getIncorrectPosition(secretCode, guessCode);
                    String hintMessage = "Correct Position: " + correctPosition + "         Incorrect Position: "
                            + incorrectPosition;
                    out.println(HINT_MESSAGE);
                    out.println(hintMessage);
                    updateAttemptCount(playerName, attemptCount);
                }
            }
            // last iteration
            if (attemptCount == 11 && !isForfeited) {
                System.out.println("LOSe 10 update");
                attemptCount -= 1;
                updateAttemptCount(playerName, attemptCount);
            }
            int playerIndex = getPlayerIndex(playerName);
            out.println(FINISH_PLAYING);
            sendWinOrLoseMessage(s, match, attemptCounts[playerIndex], secretCode);
        }

        public void waitForOtherPlayerToFinish() {
            System.out.println(playerName + ": waiting for other players to finish game...");
            while (true) {
                try {
                    Thread.sleep(SLEEP_MILLISECOND);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isAllPlayerFinishPlaying()) {
                    System.out.println(playerName + ": somebody hasn't  finish with  their game ...");
                    return;
                }

            }
        }

        public String announceRanking() {
            int[] sortedAttemptCounts = attemptCounts;
            String[] sortedPlayerNames = new String[PLAYER_COUNT];
            Arrays.sort(sortedAttemptCounts);
            if(sortedAttemptCounts.length ==1){
                sortedPlayerNames[0] =  playerNames.get(0);
            }else{
                for (int i = 0; i < sortedAttemptCounts.length; i++) {
                    sortedPlayerNames[i] = playerNames.get(i);
                }
            }
            
            System.out.println(Arrays.toString(sortedAttemptCounts));
            System.out.println(Arrays.toString(sortedPlayerNames));
            out.println(FINAL_RANKING);
            out.println("====================================");
            out.println("Ranking:");
            String message = "====================================\n" + "Ranking: \n";
            for (int i = 0; i < PLAYER_COUNT; i++) {
                String name = sortedPlayerNames[i];
                int count = sortedAttemptCounts[i];
                String line = (i + 1) + ". " + name + ", " + count + " attempts";
                out.println(line);
                message += line + "\n";
            }
            out.println("====================================");
            message += "====================================\n";
            System.out.println(message);
            return "";
        }

        public int getIndexFromAttemptCount(int count) {
            int playerIndex = 0;
            for (int i = 0; i < attemptCounts.length; i++) {
                if (attemptCounts[i] == count) {
                    playerIndex = i;
                }
            }
            return playerIndex;
        }

        // send a indivisual summary message after player finish game
        private void sendWinOrLoseMessage(Socket s, Boolean match, int attemptCount, int[] secretCode) {
            String message;
            if (match) {
                message = WIN_MESSAGE + " Secret code: " + Arrays.toString(secretCode) + "     Attempt count: "
                        + attemptCount;
            } else {
                message = LOSE_MESSAGE + " Secret code: " + Arrays.toString(secretCode) + "     Attempt count: "
                        + (attemptCount);
            }
            System.out.println(message);
            out.println(message);
        }

        // check if all player in a current game have finished playing
        private boolean isAllPlayerFinishPlaying() {
            int playerCount = getPlayerCount();
            for (int i = 0; i < playerCount; i++) {
                if (isPlayersFinish[i] == false) {
                    return false;
                }
            }
            return true;
        }

        // Update global attemptCount for current player in game base on name
        private void updateAttemptCount(String playerName, int attemptCount) {
            synchronized (attemptCounts) {
                int playerIndex = getPlayerIndex(playerName);
                attemptCounts[playerIndex] = attemptCount;
            }
        }

        // Update status of current player in game
        private void udpateIsPlayersFinish(String playerName, boolean boo) {
            synchronized (isPlayersFinish) {
                int playerIndex = getPlayerIndex(playerName);
                isPlayersFinish[playerIndex] = boo;
            }
        }
        // a result message to send after game is concluded

        private void gettingSecretCode() {
            while (true) {
                // First player's server thread generate secrete code
                if (playerName.equals(playerNames.get(0))) {
                    secretCode = generateSecretCode(x);
                    System.out.println("Secret code is successfully generated");
                    out.println(SECRET_CODE_GENERATED);
                    return;
                }
                // other player wait until code is generated
                if (secretCode != null && secretCode.length != 0) {
                    System.out.println("Secret code is successfully generated");
                    out.println(SECRET_CODE_GENERATED);
                    return;
                }
                try {
                    Thread.sleep(SLEEP_MILLISECOND);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        // if client is the first player, get X from client
        private void gettingX() throws IOException {
            while (true) {
                System.out.println(playerName + ": getting X");
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
                                isXValid = true;
                                return;
                            }
                        }
                    }
                }
                if (isXValid) {
                    System.out.println(playerName + ": Accept x");
                    out.println(X_ACCEPTED);
                    return;
                }

                try {
                    Thread.sleep(SLEEP_MILLISECOND);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    ;
                }
            }
        }

        private void setX(int val) {
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
                System.out.println("Player: " + playerName + " is waiting for game...");
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

        // get player index in a single
        private int getPlayerIndex(String playerName) {
            int index = 0;
            int playerCount = getPlayerCount();
            for (int i = 0; i < playerCount; i++) {
                String name = playerNames.get(i);
                if (playerName.equals(name)) {
                    index = i;
                }
            }
            return index;
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

        // calcualte correct position
        private static int getCorrectPosition(int[] secretCode, int[] guessCode) {
            int correctPosition = 0;
            if (isMatch(secretCode, guessCode)) {
                return secretCode.length;
            } else {
                int i = 0;
                while (i < secretCode.length && i < guessCode.length) {
                    if (secretCode[i] == guessCode[i]) {
                        correctPosition++;
                    }
                    i++;
                }
            }
            return correctPosition;
        }

        // Total match position is sum of correct and incorrect positions
        private static int getTotalMatchPosition(int[] secretCode, int[] guessCode) {
            int[] largeTempArray = null;
            int[] smallTempArray = null;
            int matchPosition = 0;
            if (secretCode.length > guessCode.length) {
                largeTempArray = secretCode;
                smallTempArray = guessCode;
            } else {
                largeTempArray = guessCode;
                smallTempArray = secretCode;
            }
            for (int i = 0; i < largeTempArray.length; i++) {
                int number = largeTempArray[i];
                boolean contains = IntStream.of(smallTempArray).anyMatch(x -> x == number);
                if (contains) {
                    matchPosition += 1;
                }
            }
            return matchPosition;
        }

        private static int getIncorrectPosition(int[] secretCode, int[] guessCode) {
            int totalMatchPosition = getTotalMatchPosition(secretCode, guessCode);
            int correctPosition = getCorrectPosition(secretCode, guessCode);
            int incorrectPosition = totalMatchPosition - correctPosition;
            return incorrectPosition;
        }

        // are 2 code completely match( all are correct positions)
        private static boolean isMatch(int[] secretCode, int[] guessCode) {
            if (Arrays.equals(secretCode, guessCode)) {
                System.out.println("Matched!!");
                return true;
            }
            return false;
        }

    }
}