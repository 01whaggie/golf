import javax.json.*;

import java.io.FileReader;
import java.util.ArrayList;

public class Map {

	String path, name, desc;

	double width, height;
	ArrayList<Double> walls;

	Map(String path) {
		this.path = path;
		try {
			JsonReader jsonReader = Json.createReader(new FileReader(path));
			JsonObject obj = jsonReader.readObject();

			this.name = obj.getJsonString("name").getString();
			this.desc = obj.getJsonString("desc").getString();
			this.width = obj.getJsonNumber("width").doubleValue();
			this.height = obj.getJsonNumber("height").doubleValue();

			JsonArray arr = obj.getJsonArray("walls");
			this.walls = new ArrayList<Double>();
			for(int i = 0; i < arr.size(); i++){
				JsonArray segment = arr.getJsonArray(i);
				if(segment.size() != 4){
					throw new Exception("Invalid wall format");
				}
				for(int j = 0; j < segment.size(); j++){
					double d = segment.getJsonNumber(j).doubleValue();
					walls.add(d);
				}
			}
			jsonReader.close();
		} catch(Exception e){
			System.out.println("something went wrong while reading map: " + path);
			e.printStackTrace();
		}
		// System.out.println(path);
		// System.out.println(name);
		// System.out.println(desc);
		// System.out.println(width);
		// System.out.println(height);
		// System.out.println(walls);
	}

	public String getName(){ return name; }
	public String getDescription(){ return desc; }
	public double getWidth(){ return width; }
	public double getHeight(){ return height; }
	public ArrayList<Double> getWalls(){ return walls; }



	public static void main(String[] args) {
		Map m = new Map("test.json");
	}
}
