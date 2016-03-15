import java.util.Scanner;
import java.util.ArrayList;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class GameWindow implements ApplicationListener {
	private FPSLogger fps;
	private OrthographicCamera cam;
	private static float VIEWPORT_WIDTH = 100;
	private ShapeRenderer shapeRenderer;
	private float rotationSpeed;

	private Map map;
	private GolfBall ball;


	@Override
	public void create() {
		// Load Map
		String path = "test.json";
		map = new Map(path);
		// ========
		Vector startingPos = new Vector(30, 30, 0);
		Vector velocity = new Vector(20, 10, 0);
		double radius = 1;
		ball = new GolfBall(startingPos, velocity, radius, 1, this.map);


		fps = new FPSLogger();

		rotationSpeed = 0.5f;

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		// Constructs a new OrthographicCamera, using the given viewport width and height
		// Height is multiplied by aspect ratio.
		cam = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_WIDTH * (h / w));

		cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
		cam.update();

		shapeRenderer = new ShapeRenderer();

		InputProcessor myInputProcessor = new InputAdapter(){
			public boolean scrolled(int amount){
				cam.zoom += 0.02*amount;
				updateCamera();

				return false;
			}
			public boolean keyDown(int key){
				if (key == Input.Keys.LEFT || key == Input.Keys.A) {
					cam.translate(-3, 0, 0);
				}
				if (key == Input.Keys.RIGHT || key == Input.Keys.D) {
					cam.translate(3, 0, 0);
				}
				if (key == Input.Keys.DOWN || key == Input.Keys.S) {
					cam.translate(0, -3, 0);
				}
				if (key == Input.Keys.UP || key == Input.Keys.W) {
					cam.translate(0, 3, 0);
				}
				if (key == Input.Keys.Q) {
					cam.rotate(-rotationSpeed, 0, 0, 1);
				}
				if (key == Input.Keys.E) {
					cam.rotate(rotationSpeed, 0, 0, 1);
				}

				updateCamera();

				return false;
			}
		};
		Gdx.input.setInputProcessor(myInputProcessor);
	}

	public void updateCamera(){
		cam.zoom = MathUtils.clamp(cam.zoom, 0.1f, 100/cam.viewportWidth*2);

		float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
		float effectiveViewportHeight = cam.viewportHeight * cam.zoom;

		// cam.position.x = MathUtils.clamp(cam.position.x, effectiveViewportWidth / 2f, 100 - effectiveViewportWidth / 2f);
		// cam.position.y = MathUtils.clamp(cam.position.y, effectiveViewportHeight / 2f, 100 - effectiveViewportHeight / 2f);
		cam.update();
	}

	@Override
	public void render() {


		this.ball.update(1f/60f);



		cam.update();

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		shapeRenderer.setProjectionMatrix(cam.combined);

		shapeRenderer.begin(ShapeType.Line);
		// walls
		shapeRenderer.setColor(1, 1, 1, 1);
		ArrayList<Double> walls = map.getWalls();
		for (int i = 0; i < walls.size()/4; i++) {
			double x1 = walls.get(4*i+0);
			double y1 = walls.get(4*i+1);
			double x2 = walls.get(4*i+2);
			double y2 = walls.get(4*i+3);
			shapeRenderer.line((float)x1, (float)y1, (float)x2, (float)y2);
		}
		// border
		shapeRenderer.rect(0, 0, (float)map.getWidth(), (float)map.getHeight());
		// ball
		shapeRenderer.setColor(1, 0, 0, 1);
		Vector pos = ball.getPosition();
		shapeRenderer.circle((float)pos.getX(), (float)pos.getY(), (float)ball.getRadius(), 20);

		shapeRenderer.end();

		// shapeRenderer.begin(ShapeType.Filled);
		// shapeRenderer.setColor(0, 1, 0, 1);
		// shapeRenderer.rect(x, y, width, height);
		// shapeRenderer.circle(x, y, radius);
		// shapeRenderer.end();

		fps.log();

	}

	@Override
	public void resize(int width, int height) {
		cam.viewportWidth = VIEWPORT_WIDTH;
		cam.viewportHeight = VIEWPORT_WIDTH * height/width;
		cam.update();
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void pause() {
	}

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 600;
		config.x = -1; //center
		config.y = -1; // center
		config.title = "Lots of fancy boxes!";
		config.samples = 4;

		new LwjglApplication(new GameWindow(), config);
	}
}
