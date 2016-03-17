import java.util.ArrayList;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.TextInputListener;
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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

import java.io.*;
import java.net.*;

public class GameScreen implements Screen, InputProcessor {
	private Game game;

	private OrthographicCamera cam;
	private Vector2 lastLeftMousePos = new Vector2(-1, -1);
	private Vector2 lastRightMousePos = new Vector2(-1, -1);
	private boolean draggingLeft = false;
	private boolean draggingRight = false;
	private static float VIEWPORT_HEIGHT = 100;
	private float rotationSpeed;

	private ShapeRenderer shapeRenderer;

	private Map map;
	private GolfBall ball;
	private Vector3D prevPosition;
	private Vector3D newPosition;
	private String ipAddress;

	// private String textIn;

	
	public GameScreen(Game game) {
		this.game = game;

		// Load Map
		String path = "test.json";
		map = new Map(path);
		// ========
		Vector3D startingPos = map.getStartPosition(); //new Vector3D(30, 30, 0);

		prevPosition = new Vector3D(0, 0, 0);
		newPosition = new Vector3D(40, 40, 0);
		Vector3D velocity = new Vector3D(0, 0, 0);

		double radius = 0.5;
		ball = new GolfBall(startingPos, velocity, radius, 1, this.map);


		rotationSpeed = 0.5f;

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		// Constructs a new OrthographicCamera, using the given viewport width and height
		// Height is multiplied by aspect ratio.
		cam = new OrthographicCamera(VIEWPORT_HEIGHT*(w/h), VIEWPORT_HEIGHT);

		// cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
		cam.position.set((float)map.getWidth()/2f, (float)map.getHeight()/2f, 0);
		cam.update();

		shapeRenderer = new ShapeRenderer();

		Gdx.input.setInputProcessor(this);


		ipAddress = readIPAddress();
		createClientThread();

		// TextInputListener textListener = new TextInputListener(){
		// 	public void input (String text) {
		// 		textIn = text;
		// 		System.out.println(textIn);
		// 	}
		// 	public void canceled () {}
		// };
		// Gdx.input.getTextInput(textListener, "Dialog Title", "", "filename");

	}

	private void updateCamera(){
		// cam.zoom = MathUtils.clamp(cam.zoom, 0.1f, 100/cam.viewportWidth*2);
		if(cam.zoom < 0.1f){
			cam.zoom = 0.1f;
		}

		float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
		float effectiveViewportHeight = cam.viewportHeight * cam.zoom;

		// cam.position.x = MathUtils.clamp(cam.position.x, effectiveViewportWidth / 2f, 100 - effectiveViewportWidth / 2f);
		// cam.position.y = MathUtils.clamp(cam.position.y, effectiveViewportHeight / 2f, 100 - effectiveViewportHeight / 2f);
		cam.update();
	}

	@Override
	public void render(float delta) {

		// this.ball.update(1f/60f);
		this.ball.update(delta);

		cam.update();

		// Gdx.gl.glClearColor(0.4f, 0.4f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		shapeRenderer.setProjectionMatrix(cam.combined);

		shapeRenderer.begin(ShapeType.Line);
		// border
		shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1);
		shapeRenderer.rect(0, 0, (float)map.getWidth(), (float)map.getHeight());
		// grid
		float sep = 2.5f;
		shapeRenderer.setColor(0.05f, 0.05f, 0.05f, 1);
		for (float x = sep; x <= map.getWidth()-sep; x+=sep) {
			shapeRenderer.line(x, 0, x, (float)map.getHeight());
		}
		for (float y = sep; y <= map.getHeight()-sep; y+=sep) {
			shapeRenderer.line(0, y, (float)map.getWidth(), y);
		}
		sep = 5f;
		shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1);
		for (float x = sep; x <= map.getWidth()-sep; x+=sep) {
			shapeRenderer.line(x, 0, x, (float)map.getHeight());
		}
		for (float y = sep; y <= map.getHeight()-sep; y+=sep) {
			shapeRenderer.line(0, y, (float)map.getWidth(), y);
		}
		
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
		Vector3D ballpos = ball.getPosition();
		// hole
		shapeRenderer.setColor(0.2f, 0.6f, 0.2f, 1);
		Vector3D holepos = map.getHolePosition();
		double radius = map.getHoleRadius();
		shapeRenderer.circle((float)holepos.x, (float)holepos.y, (float)radius, 20);
		// line between mouse and ball when dragging
		if ((draggingLeft && lastLeftMousePos.x != -1 && lastLeftMousePos.y != -1)){
			shapeRenderer.setColor(1, 1, 0, 1);
			Vector3 mouseInWorld = cam.unproject(new Vector3(lastLeftMousePos, 0));
			Vector3 line = mouseInWorld.cpy();
			line.sub((float)ballpos.x, (float)ballpos.y, 0f);
			line.limit((float)ball.MAX_KICK_SPEED/5f);
			shapeRenderer.line((float)ballpos.x, (float)ballpos.y, (float)ballpos.x + line.x/*mouseInWorld.x*/, (float)ballpos.y + line.y/*mouseInWorld.y*/);
		}
		shapeRenderer.end();
		shapeRenderer.begin(ShapeType.Filled);
		// ball
		shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1);
		shapeRenderer.circle((float)ballpos.x, (float)ballpos.y, (float)ball.getRadius(), 20);
		shapeRenderer.end();
	}

	@Override
	public void resize(int width, int height) {
		cam.viewportWidth = VIEWPORT_HEIGHT * width/height;
		cam.viewportHeight = VIEWPORT_HEIGHT;
		cam.update();
	}
	
	// INPUT
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

	public boolean keyTyped(char character){
		return false;
	}

	public boolean keyUp(int key){
		return false;
	}

	public boolean mouseMoved(int screenX, int screenY){
		return false;
	}

	public boolean scrolled(int amount){
		cam.zoom += 0.05*amount;
		updateCamera();

		return false;
	}

	public boolean touchDown(int screenX, int screenY, int pointer, int button){
		if(button == 0){
			draggingLeft = true;
		}
		if(button == 1){
			draggingRight = true;
		}
		return false;
	}

	public boolean touchDragged(int screenX, int screenY, int pointer){
		if(draggingRight){
			if(lastRightMousePos.x != -1 && lastRightMousePos.y != -1){
				Vector3 screenpos = cam.unproject(new Vector3(screenX, screenY, 0));
				Vector3 mousepos = cam.unproject(new Vector3(lastRightMousePos, 0));
				float dx = mousepos.x - screenpos.x;
				float dy = mousepos.y - screenpos.y;

				cam.translate(dx, dy, 0);
			}
			lastRightMousePos.set(screenX, screenY);
		}
		if(draggingLeft){
			lastLeftMousePos.set(screenX, screenY);
		}
		return false;
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button){
		if(button == 0){
			// 'hit' the ball
			Vector3 mouseInWorld = cam.unproject(new Vector3(screenX, screenY, 0));
			Vector3D dx = new Vector3D(mouseInWorld.x, mouseInWorld.y, 0);
			dx.sub(ball.getPosition());
			dx.mult(-1);
			// multiply to scale velocity
			dx.mult(5);
			ball.kick(dx);

			draggingLeft = false;
			lastLeftMousePos.set(-1, -1);
		}
		if(button == 1){
			draggingRight = false;
			lastRightMousePos.set(-1, -1);

		}
		return false;
	}


	public boolean hitBall(float deltaX, float deltaY){
			// 'hit' the ball
			Vector3D dx = new Vector3D(deltaX, deltaY, 0);
			dx.sub(prevPosition);
			dx.mult(-1);
			// multiply to scale velocity
			dx.mult(3);
			ball.addVelocity(dx);
		return false;
	}

	private String readIPAddress() {

		String result = "127.0.0.1";

		try {
			BufferedReader br = new BufferedReader(new FileReader("ip.ini"));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			result = sb.toString();
		}
		catch (Exception ex) {
		}

		return result;
	}



	private void createClientThread() {

		Thread thread = new Thread() {
			public void run() {
				String requestToServer;
				String coordsFromServer;

				while (true) {
					try {
						Socket clientSocket = new Socket(ipAddress, 7544);
						DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
						BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
						requestToServer = "getCoords";
						outToServer.writeBytes(requestToServer + '\n');
						coordsFromServer = inFromServer.readLine();
						String[] coords = coordsFromServer.split(";");

						hitBall(Float.parseFloat(coords[0]), Float.parseFloat(coords[1]));
						clientSocket.close();
					} catch (Exception ex) {
						System.out.println(ex.getMessage());
					}
				}
			}
		};

		thread.start();
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

	@Override
	public void hide(){
	}

	@Override
	public void show(){
	}
}
