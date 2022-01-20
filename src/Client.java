import java.io.*;
import java.net.*;
import java.sql.SQLOutput;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Client client = new Client();
        client.startConnection("127.0.0.1",6666);
    }
///////////////////////////////////
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Battleship battleship;
    private Battleship enemyBattleship;
    private Scanner scanner;

    public Client(){
        battleship = new Battleship();
        enemyBattleship = new Battleship(true);
        scanner = new Scanner(System.in);
    }

    public void startConnection(String ip, int port){
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            startGame();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startGame() {
        try {
            System.out.println("Jeżeli jesteś gotowy napisz READY i wciśnij ENTER!");
            String outToServer = scanner.nextLine();
            out.println(outToServer);
            if (in.readLine().toUpperCase(Locale.ROOT).equals("READY")) {
                printGameRules();
                printBoards();
                System.out.println("Zaczynasz. Podaj współrzędne.");

                outToServer = scanner.nextLine();
                out.println("start;" + outToServer);
                continueGame();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void continueGame() {
        String inFromServer;
        String isTrafiony;
        int row = 0;
        int column = 0;
        try {
            while(true){
                inFromServer = in.readLine();
                System.out.println(inFromServer);
                String[] inFromServerA = inFromServer.split(";");

                //sprawdzam czy trafiłem przeciwnika
                if(inFromServerA[0].equals("trafiony"))
                    markField(row, column, "@", enemyBattleship);
                else
                    markField(row, column, "?", enemyBattleship);
                //spradzam czy przeciwnik mnie trafił
                if(checkIfHitted(inFromServerA[1])) {
                    isTrafiony = "trafiony";
                    markField(inFromServerA[1], "@", battleship);
                }
                else{
                    isTrafiony = "pudlo";
                    markField(inFromServerA[1], "?", battleship);
                }

                printBoards();
                //wspolrzedne do trafienia
                String outToServer = scanner.nextLine();
                out.println(isTrafiony + ";" +outToServer);
                row = outToServer.charAt(1)-48;
                column = outToServer.charAt(0)-65;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Boolean checkIfHitted(String inFromClientA) {
        int row = inFromClientA.charAt(1)-48;
        int column = inFromClientA.charAt(0)-65;
        return Objects.equals(battleship.field[row][column], "#")
                || Objects.equals(battleship.field[row][column], "@");
    }

    private void markField(String msgIn, String c, Battleship b) {
        int row, column;
        row = msgIn.charAt(1)-48;
        column = msgIn.charAt(0) - 65;
        b.field[row][column] = c;
    }

    private void markField(int row, int column, String c, Battleship b) {
        b.field[row][column] = c;
    }

    public void printBoards(){
        System.out.print("TWOJA PLANSZA:");
        battleship.printField();
        System.out.print("PLANSZA PRZECIWNIKA:");
        enemyBattleship.printField();
    }

    private void printGameRules(){
        String stringBuilder = "ZASADY GRY" +
                "\nKolumny numerowane są od A do J," +
                " wiersze numerowane są od 0 do 9\n" +
                "Na planszy umieszczone są: \n" +
                "#####- 1 cztero-masztowiec\n" +
                "###- 2 trzy-masztowce\n" +
                "##- 3 dwu-masztowce\n" +
                "#- 4 jedno-masztowece\n" +
                "współrzędne podajemy w formacie \"kolumna;wiersz\" np A0\n" +
                "Wygrywasz jeżeli uda Ci się trafić wszystkie statki przeciwnika\n";
        System.out.println(stringBuilder);
    }
}
