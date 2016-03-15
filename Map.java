import java.util.ArrayList;

/**
 * Created by Michael on 14.03.2016.
 */
public class Map {

    int sizeX, sizeY;
    int[][] mapArray;

    public static ArrayList<Tile> defaultTiles = new ArrayList<>();

    static void loadTiles(String string) {
        for (int i = 0; i < 10; i ++) {
            //do shit
        }
    }

    Map(int x, int y) {
        sizeX = x;
        sizeY = y;

        mapArray = new int[x][y];
    }





}
