import java.util.*;

public class Battleship implements BattleshipGenerator{
    private final int SIZE = 10;
    public String[][] field = new String[SIZE][SIZE];
    private final List<Integer> shipsSizes = new ArrayList<>(List.of(4, 3, 3, 2, 2, 2, 1, 1, 1, 1));
    public List<Ship> ships = new ArrayList<>();

    public Battleship(){
        this.generateMap();
    }

    public Battleship(Boolean emptyBoard){
        this.filedInitialization();
    }
    @Override
    public String generateMap() {
        return placeShips();
    }

    private void filedInitialization(){
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                field[i][j] = ".";
            }
        }
    }

    public void printField(){
        System.out.println(" ");
        for (int i = 0; i < 10; i++) {
            System.out.print((char)(i+65)+" ");
        }
        System.out.println();
        int i=0;
        for(String[] rowF : field){
            for(String columnF : rowF){
                System.out.print(columnF+ " ");
            }
            System.out.println(i++);
        }
        System.out.println();
    }

    public String placeShips(){
        filedInitialization();
        Random random = new Random();
        for (int shipSizes : shipsSizes) {
            boolean created = false;
            while (!created) {
                int row = random.nextInt(10);
                int column = random.nextInt(10);
                if (Objects.equals(field[row][column], "#") || Objects.equals(field[row][column], "-")){
                    created = false;
                } else {
                    Ship ship = new Ship(shipSizes);
                    ship.setShip(new Coordinates(row, column),field);
                    if (ship.isShipCreated) {
                        createShipFromCoordinates(ship.coordinatesList);
                        markAroundTheShip(ship);
                        ships.add(ship);
                        created = true;
                    }
                }
            }
        }
        replaceToWater();
        String battleshipField = stringArrayToString();

        return battleshipField;
    }

    private void markAroundTheShip(Ship ship){
        for(Coordinates coordinates : ship.coordinatesList){
            int r = coordinates.getX();
            int c = coordinates.getY();

            if(ship.isValid(r-1,c,field)) field[r-1][c] = "-"; //gora
            if(ship.isValid(r+1,c,field)) field[r+1][c] = "-"; //dol
            if(ship.isValid(r,c+1,field)) field[r][c+1] = "-"; //prawo
            if(ship.isValid(r,c-1,field)) field[r][c-1] = "-"; //lewo
            if(ship.isValid(r-1,c-1,field)) field[r-1][c-1] = "-"; //lewo gora skos
            if(ship.isValid(r-1,c+1,field)) field[r-1][c+1] = "-"; //prawo gora skos
            if(ship.isValid(r+1,c-1,field)) field[r+1][c-1] = "-"; //lewo dol skos
            if(ship.isValid(r+1,c+1,field)) field[r+1][c+1] = "-"; //prawo dol skos
        }
    }

    private void createShipFromCoordinates(List<Coordinates> coordinates){
        for(Coordinates c : coordinates){
            field[c.getX()][c.getY()] = "#";
        }
    }

    private void replaceToWater(){
        for(int i=0; i<SIZE; i++){
            for(int j=0; j<SIZE; j++){
                if(Objects.equals(field[i][j], "-")){
                    field[i][j] = ".";
                }
            }
        }
    }

    private String stringArrayToString(){
        StringBuilder result = new StringBuilder();
        for(String[] r : field){
            for(String c : r){
                result.append(c);
            }
        }
        return result.toString();
    }

}

class Coordinates{
    public int x;
    public int y;

    public Coordinates (int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Coordinates(" +
                x +
                ", " + y +
                ')';
    }
}

class Ship{
    public final int SIZE = 10;
    public int shipSize;
    List<Coordinates> coordinatesList = new ArrayList<>();
    boolean isShipCreated;

    public void setShip(Coordinates startCoordinates, String[][] field){
        String[][] tempField =  Arrays.stream(field).map(String[]::clone).toArray(String[][]::new);
        LinkedList<Coordinates> path = new LinkedList<>();
        path.push(startCoordinates);
        coordinatesList.add(startCoordinates);
        int i = 1;
        while (path.size() > 0 && i < shipSize){
            int x = path.peek().x;
            int y = path.peek().y;
            tempField[x][y] = "#";

            if(isValid(x,y-1,tempField)){ //lewo
                tempField[x][y-1] = "#";
                path.push(new Coordinates(x,y-1));
                coordinatesList.add(new Coordinates(x,y-1));
                i++;
                continue;
            }
            if(isValid(x+1,y,tempField)){ //dol
                tempField[x+1][y] = "#";
                path.push(new Coordinates(x+1,y));
                coordinatesList.add(new Coordinates(x+1,y));
                i++;
                continue;
            }
            if(isValid(x-1,y,tempField)){ //gora
                tempField[x-1][y] = "#";
                path.push(new Coordinates(x-1,y));
                coordinatesList.add(new Coordinates(x-1,y));
                i++;
                continue;
            }
            if(isValid(x,y+1,tempField)){ //peawo
                tempField[x][y+1] = "#";
                path.push(new Coordinates(x,y+1));
                coordinatesList.add(new Coordinates(x,y+1));
                i++;
                continue;
            }
            path.pop();
        }
        isShipCreated = i == shipSize;
    }

    boolean isValid(int x, int y, String[][] fieldT){
        return y >= 0 && y < SIZE && x >= 0 && x < SIZE
                && !Objects.equals(fieldT[x][y], "-")
                && !Objects.equals(fieldT[x][y], "#");
    }

    public Ship(int size) {
        this.shipSize = size;
    }
}


