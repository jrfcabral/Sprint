package sprint.server.logic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

import sprint.server.gui.LobbyMenu;
import sprint.server.gui.ScoreMenu;
import sprint.server.net.Lobby;
import sprint.server.net.PlayerControls;
import utils.CameraManager;
import utils.Settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Race implements ContactListener, State{
	private static final int OIL_MAGNITUDE = 1000;
	private static final int OIL_DURATION = 10000;
	private static final int OIL_SIZE = 2;
	private static final int DIRECTION = 1;
	private static final int LAP_NUMBER = 3;
	private Texture trackTex;

	private SpriteBatch batch;
	private World world;
	private Track track;
	private OrthographicCamera camera;
	private Box2DDebugRenderer debugRenderer;
	private CameraManager camManager;
	private ArrayList<Car> cars;
	private boolean ended;
	private final StateMachine stateMachine;
	private Lobby lobby;
	private ArrayList<Vector2> oilPoints;	
	private LinkedList<String> positions;
		
	public Race(StateMachine stateMachine, Lobby lobby){
		this.lobby = lobby;
		world  = new World(new Vector2(0,0), true);
		world.setContactListener(this);
		batch = new SpriteBatch();
		track = new Track(world);

		trackTex = new Texture("Track01.png");
		
		//track.addSegment(0, 0, -50, 50);
		//track.addSegment(0, 200, -50, 50);
		/*track.addCurveLR(new Vector2(0, 0), new Vector2(0, 350), new Vector2(125, 125), 0, 20);
		track.addCurveUD(new Vector2(0, 0), new Vector2(350, 0), new Vector2(125, 125), 0, 20);

		track.addSegment(0, 0, 350, 0);
		track.addSegment(350, 0, 350, 350);
		track.addSegment(350, 350, 0, 350);
		track.addSegment(0, 350, 0, 0);
		track.addSegment(40, 40, 160, 40);
		track.addSegment(160, 40, 160, 160);
		track.addSegment(160, 160, 40, 160);
		track.addSegment(40, 160, 40, 40);
		track.addFinishLine(120, 180, 125, 200);
		track.apply();*/
		track.addSegment(-175, -175, 175, -175);
		track.addSegment(-175, 175, 175, 175);
		track.addSegment(-175, -175, -175, 175);
		track.addSegment(175, -175, 175, 175);
		track.addCurveLR(new Vector2(-150, -175), new Vector2(-175, -125), new Vector2(-175, -125), 0, 50);
		track.addCurveLR(new Vector2(-150, 175), new Vector2(-175, 125), new Vector2(-175, 125), 0, 50);
		track.addCurveLR(new Vector2(150, -175), new Vector2(175, -125), new Vector2(175, -125), 0, 50);
		track.addCurveLR(new Vector2(150, 175), new Vector2(175, 125), new Vector2(175, 125), 0, 50);
		
		track.addSegment(-88, -88, 88, -88);
		track.addSegment(-88, 88, 88, 88);
		track.addSegment(-100, -55, -100, 55);
		track.addSegment(100, -55, 100, 55);
		track.addCurveLR(new Vector2(-85, -88), new Vector2(-88, -85), new Vector2(-100, -50), 0, 50);
		track.addCurveLR(new Vector2(-85, 88), new Vector2(-88, 85), new Vector2(-100, 50), 0, 50);
		track.addCurveLR(new Vector2(85, -88), new Vector2(88, -85), new Vector2(100, -50), 0, 50);
		track.addCurveLR(new Vector2(85, 88), new Vector2(88, 85), new Vector2(100, 50), 0, 50);
		
		track.addFinishLine(0, 132, 10, 175);
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Settings.VIEWPORT_WIDTH, Settings.VIEWPORT_HEIGHT);
		camera.position.set(0, 0, 0);
		camera.update();
		oilPoints = new ArrayList<Vector2>();
		positions = new LinkedList<String>();
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
				batch.draw(trackTex, -202, -226, 400, 459);
				car.getSprite().draw(batch);
				batch.end();
				car.update();
				if (!car.getAlive())
					it.remove();
			}
							
			
			//debugRenderer.render(world, camera.combined);		
			
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
	}
	
	
	

	public Car getCar(){
		return cars.get(0);
		
	}
	
	public World getWorld(){
		return world;
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
		
		if ( (a != null && a.toString().equals("oil")) || (b != null && b.toString().equals("oil"))){			
			if (a instanceof Car)
				car = (Car) a;
			else if (b instanceof Car)
				car = (Car) b;
			else 
				return;
			car.applyOil();		
		}
		
		
		if(a!= null && a.toString().equals("finish"))			
			if(b != null && b instanceof Car)
				car =(Car) b;
			else
				return;
		else if (b != null && b.toString().equals("finish"))
			if(a != null && a instanceof Car)
				car = (Car) a;
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
			if(car.getLaps() >= LAP_NUMBER){
				oneEnded = true;
				if (!car.isDone())
				positions.add(car.getColor());
				car.setDone(true);
				car.dispose();				
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
		if (ended)
			this.stateMachine.setState(new ScoreMenu(this.positions, this.lobby, this.stateMachine));
		if (oilPoints.size() < 100){
			Random rand = new Random();
			this.oilPoints.add(this.cars.get(rand.nextInt(this.cars.size())).getPosition());			
		}
		Random rand = new Random();
		if (rand.nextInt(10000) < 20){
			Vector2 pos = this.oilPoints.get(rand.nextInt(this.oilPoints.size()));
			createOil(pos);
		}
	}

	private void createOil(Vector2 pos) {
		BodyDef oild = new BodyDef();
		oild.position.set(pos);
		oild.type = BodyType.StaticBody;
		final Body oil = this.world.createBody(oild);
		oil.setUserData(new String("oil"));
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(OIL_SIZE, OIL_SIZE);
		FixtureDef fdef = new FixtureDef();
		fdef.isSensor = true;
		fdef.restitution = 0f;
		fdef.shape = shape;		
		oil.createFixture(fdef);
		Thread t = new Thread(){
			@Override
			public void run(){
				try {Thread.sleep(OIL_DURATION);} catch (InterruptedException e) {}
				Race.this.world.destroyBody(oil);
			}
		};
		t.start();
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
