import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        Server server = new Server();
        server.start(6666);
    }
    ////////////////////////////////////
    private Socket clientSocket;
    private ServerSocket serverSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Battleship battleship;
    private Battleship enemyBattleship;
    private Scanner scanner;

    public Server(){
        battleship = new Battleship();
        enemyBattleship = new Battleship(true);
        scanner = new Scanner(System.in);
    }

    public void start(int port){
        try{
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            if(in.readLine().equals("READY")){
                System.out.println("Przeciwnik jest gotowy, zeby zaczac napisz READY i wcisnij ENTER!");
                String outToClient = scanner.nextLine();
                out.println(outToClient.toUpperCase(Locale.ROOT));

                printGameRules();
                printBoards();
                String[] inFromClient = in.readLine().split(";");
                if(inFromClient[0].equals("start")){
                    contiuneGame(inFromClient[1], false);
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void contiuneGame(String coordinates, Boolean b) {
        String inFromClient = "pudlo;"+coordinates;
        String isTrafiony;
        int row = 0;
        int column = 0;
        try {
            while(true){
                //inFromClient = pudlo;A1
                if(b) inFromClient = in.readLine();
                if(!b) b = true;
                System.out.println(inFromClient);

                String[] inFromClientA = inFromClient.split(";");
                //sprawdzam czy trafiłem przeciwnika
                if(inFromClientA[0].equals("trafiony"))
                    markField(row, column, "@", enemyBattleship);
                else
                    markField(row, column, "?", enemyBattleship);
                //spradzam czy przeciwnik mnie trafił
                if(checkIfHitted(inFromClientA[1])) {
                    isTrafiony = "trafiony";
                    markField(inFromClientA[1], "@", battleship);
                }
                else{
                    isTrafiony = "pudlo";
                    markField(inFromClientA[1], "?", battleship);
                }
                printBoards();
                //wspolrzedne do trafienia
                String outToClient = scanner.nextLine();
                out.println(isTrafiony + ";" + outToClient);
                row = outToClient.charAt(1)-48;
                column = outToClient.charAt(0)-65;
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
