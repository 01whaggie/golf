import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Launcher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 600;
		config.x = -1; //center
		config.y = -1; // center
		config.title = "Lots of fancy boxes!";
		config.samples = 4;

		new LwjglApplication(new OrthographicCameraExample(), config);
	}
}
