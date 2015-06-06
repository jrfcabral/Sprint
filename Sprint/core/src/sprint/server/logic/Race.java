package sprint.server.logic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import sprint.server.gui.LobbyMenu;
import sprint.server.logic.Game.GameState;
import sprint.server.net.Lobby;
import sprint.server.net.PlayerControls;
import sprint.tests.Tests;
import utils.CameraManager;
import utils.Settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

public class Race implements ContactListener, State{
	private static final int DIRECTION = 1;
	private static final int LAP_NUMBER = 3;
	private SpriteBatch batch;
	private World world;
	private Track track;
	private OrthographicCamera camera;
	private Box2DDebugRenderer debugRenderer;
	private CameraManager camManager;
	private ArrayList<Car> cars;
	private boolean testing;
	private boolean ended;
	private final StateMachine stateMachine;
	private Lobby lobby;
	
	public Race(StateMachine stateMachine, Lobby lobby){
		this.lobby = lobby;
		world  = new World(new Vector2(0,0), true);
		world.setContactListener(this);
		batch = new SpriteBatch();
		track = new Track(world);
		track.addSegment(0, 0, 200, 0);
		track.addSegment(200, 0, 200, 200);
		track.addSegment(200, 200, 0, 200);
		track.addSegment(0, 200, 0, 0);
		track.addSegment(40, 40, 160, 40);
		track.addSegment(160, 40, 160, 160);
		track.addSegment(160, 160, 40, 160);
		track.addSegment(40, 160, 40, 40);
		track.addFinishLine(120, 180, 125, 200);
		track.apply();
		
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Settings.VIEWPORT_WIDTH, Settings.VIEWPORT_HEIGHT);
		camera.position.set(0, 0, 0);
		camera.update();
		
		camManager = new CameraManager();
		cars = new ArrayList<Car>();
		ended = false;
		this.stateMachine = stateMachine;
	}
	
	public void draw(){
			camManager.update(Gdx.graphics.getDeltaTime());
			camManager.applyTo(camera);	
			batch.setProjectionMatrix(camera.combined);
			
			ListIterator<Car> it = cars.listIterator();
			while(it.hasNext()){
				Car car = it.next();
				batch.begin();
				car.getSprite().draw(batch);
				batch.end();
				car.update();
				if (!car.getAlive())
					it.remove();
			}
							
			
			debugRenderer.render(world, camera.combined);		
			
			handleInput(Gdx.graphics.getDeltaTime());
			
			world.step(1/60f, 6, 2);
			
			/*Isto tem q ser posto noutro sitio depois (maybe ?)*/
			checkEnd();
		}
		
		

	public void startGame(LinkedList<String> identifiers) {
		String[] colors = new String[]{"Red", "Blue", "Green", "Pink", "Orange"};
		int i = identifiers.size()-1;
		for (String id : identifiers){
			PlayerControls controls = new PlayerControls(id, stateMachine.getServer(), colors[i--]);
			Car car = new Car(this, controls);
			addCar(car);
		}		
	}


	public void handleInput(float deltaTime){
		
		
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
				camManager.setTarget(cars.get(0).getSprite());
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
	
	
	

	public Car getCar(){
		return cars.get(0);
		
	}
	
	public World getWorld(){
		return world;
	}
	
	public static void main(){
		Game game = new Game();		
	}
	
	 public void removeCar(Car car){
		 this.cars.remove(car);
	 }

	public void addCar(Car car) {
		this.cars.add(car);
		
	}

	@Override
	public void beginContact(Contact contact) {
		
		Car car;
		Object a = contact.getFixtureA().getBody().getUserData();
		Object b = contact.getFixtureB().getBody().getUserData();
		if(a!= null && a.toString().equals("finish"))			
			if(b != null && b instanceof Car)
				car =(Car) contact.getFixtureB().getBody().getUserData();
			else
				return;
		else if (b != null && b.toString().equals("finish"))
			if(a != null && a instanceof Car)
				car = (Car) contact.getFixtureB().getBody().getUserData();
			else				
				return;
			
				
		else
			return;
		
		if (car.getLinearVelocity().x*DIRECTION > 0){
			System.out.println("dei uma voltinha na minha lambreta");
			car.incrementLap();
		}
		else{
			System.out.println("desdei uma voltinha na minha lambreta");
			car.decrementLap();
		}
		
	}

	@Override
	public void endContact(Contact contact) {}
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {}
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}
	
	private void checkEnd(){
		boolean oneEnded = false;
		boolean allEnded = true;
		for(Car car: cars){
			if(car.getLaps() == LAP_NUMBER){
				if(!oneEnded){
					oneEnded = true;
				}
			}
			else{
				allEnded = false;
			}
		}
		if(allEnded){
			ended = true;
			return;
		}
		else if(oneEnded){
			/*run 30 sec timer to end game?*/
		}
	}

	public boolean getEnded() {
		return ended;
	}

	@Override
	public void update() {
		checkEnd();
		if (false)
			this.stateMachine.setState(new LobbyMenu(this.lobby, this.stateMachine));
		
	}

	@Override
	public void create() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}
		
}
