import javax.json.*;

import java.io.*;
import java.util.ArrayList;

public class Map {

	String path, name, desc;
	double width, height;
	ArrayList<Double> walls;

	Map(){
		this.width = 100;
		this.height = 100;
		this.walls = new ArrayList<>();
	}

	Map(String path) {
		this.load(path);
	}

	public void load(String path){
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
	}

	public void store(String path){
		// JsonObject obj = new JsonObject();
		// obj.put("name", this.name);
		// obj.put("desc", this.desc);
		// obj.put("width", new Double(this.width));
		// obj.put("height", new Double(this.height));
		// JsonArray arr = new JsonArray();
		// obj.put("walls", new Double(1000.21));
		// obj.put("is_vip", new Boolean(true));
		this.path = path;

		JsonArrayBuilder arr = Json.createArrayBuilder();
		for (int i = 0; i < this.walls.size()/4; i++) {
			JsonArrayBuilder segment = Json.createArrayBuilder();
			segment.add(this.walls.get(4*i+0));
			segment.add(this.walls.get(4*i+1));
			segment.add(this.walls.get(4*i+2));
			segment.add(this.walls.get(4*i+3));
		
			arr.add(segment.build());
		}

		// Map<String, Object> properties = new HashMap<String, Object>(1);
		// properties.put(JsonGenerator.PRETTY_PRINTING, true);
		// JsonGeneratorFactory jgf = Json.createGeneratorFactory(properties);
		// JsonGenerator jg = jgf.createGenerator(System.out);

		JsonObject obj = Json.createObjectBuilder()
			.add("name", this.name)
			.add("desc", this.desc)
			.add("width", this.width)
			.add("height", this.height)
			.add("walls", arr.build())
			.build();

		System.out.println(obj);
		
		try {
			JsonWriter writer = Json.createWriter(new FileWriter(path));
			writer.writeObject(obj);
			writer.close();
		} catch(IOException e){
			System.out.println("couldn't write file: " + path);
		}


	}

	public String getName(){ return name; }
	public String getDescription(){ return desc; }
	public double getWidth(){ return width; }
	public double getHeight(){ return height; }
	public ArrayList<Double> getWalls(){ return walls; }

	public void setName(String name){ this.name = name; }
	public void setDescription(String desc){ this.desc = desc; }
	public void setWidth(double width){ this.width = width; }
	public void setHeight(double height){ this.height = height; }
	public void addWall(double x1, double y1, double x2, double y2){ 
		this.walls.add(x1);
		this.walls.add(y1);
		this.walls.add(x2);
		this.walls.add(y2);
	}

	@Override
	public String toString(){
		String r = "";
		r += "name = " + name + "\n";
		r += "desc = " + desc + "\n";
		r += "width = " + width + "\n";
		r += "height = " + height + "\n";
		r += "walls = [\n";
		for (int i = 0; i < walls.size()/4; i++) {
			r += "    [";
			r += walls.get(4*i+0) + ", ";
			r += walls.get(4*i+1) + ",  ";
			r += walls.get(4*i+2) + ", ";
			r += walls.get(4*i+3);
			r += "]\n";
		}
		r += "]";
		return r;
	}

	public static void main(String[] args) {
		Map m = new Map("test.json");
		System.out.println(m);

		Map newMap = new Map();
		newMap.setName("writeTest");
		newMap.setDescription("something something ...");
		newMap.addWall(0, 0, 100, 100);
		newMap.addWall(0, 0, 100, 0);
		newMap.addWall(100, 0, 100, 100);

		newMap.store("new.json");



	}
}
