package sprint.server.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.graphics.OrthographicCamera;

import sprint.server.logic.*;
import sprint.tests.Tests;
import utils.CameraManager;
import utils.Settings;


public class Game extends ApplicationAdapter {
	public static enum GameState{
		Main, Lobby, InGame
	}
	GameState state;
	SpriteBatch batch;
	Texture img;
	World world;
	OrthographicCamera camera;
	Box2DDebugRenderer debugRenderer;
	Body body;
	Car car;
	CameraManager camManager;
	MainMenu main;
	boolean testing;
	
	
	protected boolean throttle;
	protected boolean brake;
	@Override
	public void create () {
		state = GameState.Main;
		testing = false;
		
		world  = new World(new Vector2(0,0), true);
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Settings.VIEWPORT_WIDTH, Settings.VIEWPORT_HEIGHT);
		camera.position.set(0, 0, 0);
		camera.update();
		batch = new SpriteBatch();
		car = new Car(world);
		Track track = new Track(world);
		track.addSegment(0, 0, 200, 0);
		track.addSegment(200, 0, 200, 200);
		track.addSegment(200, 200, 0, 200);
		track.addSegment(0, 200, 0, 0);
		track.addSegment(40, 40, 160, 40);
		track.addSegment(160, 40, 160, 160);
		track.addSegment(160, 160, 40, 160);
		track.addSegment(40, 160, 40, 40);
		track.apply();
		
		main = new MainMenu();
		
		camManager = new CameraManager();
		
		
		new Thread(new Runnable(){
	        @Override
	        public void run() {
	        	System.out.println("Thread is running");
	            ServerSocketHints serverSocketHint = new ServerSocketHints();
	            // 0 means no timeout.  Probably not the greatest idea in production!
	            serverSocketHint.acceptTimeout = 0;
	           
	            ServerSocket serverSocket = Gdx.net.newServerSocket(null, 8888, serverSocketHint);
	            
	            // Loop forever
	            while(true){
	                // Create a socket
	                Socket socket = serverSocket.accept(null);
	                
	                // Read data from the socket into a BufferedReader
	                BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
	                
	                try {
	                	String command = buffer.readLine();
	                	System.out.println(command);
	                	if(command.equals("Accelerate"))
	                		throttle = true;
	                	else if(command.equals("Nop")){
	                		throttle = false;
	                		brake = false;
	                	}
	                	else if(command.equals("Travate"))
	                		brake = true;
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	        
	    }).start();
		
		
	}

	@Override
	public void render () {	
		if(state == GameState.Main){
			Gdx.gl.glClearColor(1, 1, 1, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			main.draw(batch);
			if(main.startServer.isPressed()){
				System.out.println("It's pressed");
				state = GameState.InGame;
			}
		}
		else if(state == GameState.Lobby){
			;
		}
		else if(state == GameState.InGame){
			drawGame(Gdx.graphics.getDeltaTime());
		}
	}
	
	public void drawGame(float deltaTime){
		if(!testing){
			camManager.update(Gdx.graphics.getDeltaTime());
			camManager.applyTo(camera);
			Gdx.gl.glClearColor(1, 1, 1, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			car.getSprite().draw(batch);
			batch.end();
			debugRenderer.render(world, camera.combined);		
			
			handleInput(Gdx.graphics.getDeltaTime());
			world.step(1/60f, 6, 2);
		}
	}
	
	
	public void handleInput(float deltaTime){
		if(!testing){
			throttle = Gdx.input.isKeyPressed(Keys.W);
		}
		if(!testing){
			brake = Gdx.input.isKeyPressed(Keys.S);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.D))
			car.update(throttle,brake, Car.SteerDirection.SteerLeft);
		else if (Gdx.input.isKeyPressed(Input.Keys.A))
			car.update(throttle,brake, Car.SteerDirection.SteerRight);
		else if (Gdx.input.isKeyPressed(Input.Keys.N))
			world = new World(new Vector2(0,0), true);
		else
			car.update(throttle,brake, Car.SteerDirection.SteerNone);
		
		//Camera controls
		//Movement
		if(Gdx.input.isKeyPressed(Keys.UP)){
			camManager.setPosition(camManager.getPosition().x, camManager.getPosition().y + Settings.CAMERA_MOVE_SPEED * deltaTime);
		}
		if(Gdx.input.isKeyPressed(Keys.DOWN)){
			camManager.setPosition(camManager.getPosition().x, camManager.getPosition().y - Settings.CAMERA_MOVE_SPEED * deltaTime);
		}
		if(Gdx.input.isKeyPressed(Keys.LEFT)){
			camManager.setPosition(camManager.getPosition().x - Settings.CAMERA_MOVE_SPEED * deltaTime, camManager.getPosition().y);
		}
		if(Gdx.input.isKeyPressed(Keys.RIGHT)){
			camManager.setPosition(camManager.getPosition().x + Settings.CAMERA_MOVE_SPEED * deltaTime, camManager.getPosition().y);
		}
		//Zoom
		if(Gdx.input.isKeyPressed(Keys.PAGE_UP)){
			camManager.addZoom(Settings.CAMERA_ZOOM_SPEED);
		}
		if(Gdx.input.isKeyPressed(Keys.PAGE_DOWN)){
			camManager.addZoom(-Settings.CAMERA_ZOOM_SPEED);
		}
		if(Gdx.input.isKeyPressed(Keys.SPACE)){
			if(camManager.hasTarget()){
				camManager.setTarget(null);
			}
			else{
				camManager.setTarget(car.getSprite());
			}
		}
		
		/*Engage testing sequence*/
		if(Gdx.input.isKeyPressed(Keys.T)){
			testing = true;
			Tests tests = new Tests(this);
			tests.run();
			testing = false;
		}
	}
	
	public boolean getThrottle(){
		return throttle;
	}
	
	public void setThrottle(boolean thrtle){
		throttle = thrtle;
	}
	
	public boolean getBrake(){
		return brake;
	}
	
	public void setBrake(boolean brk){
		brake = brk;
	}
	
	public Car getCar(){
		return car;
		
	}
	
	public static void main(){
		Game game = new Game();		
	}
	
}
