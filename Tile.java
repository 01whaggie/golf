import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Michael on 14.03.2016.
 */
public class Tile {

    ArrayList<Double> walls = new ArrayList<>();
    int id;

    Tile(String name) {

    }

    ArrayList<Double> getWalls() {
        return walls;
    }

}
