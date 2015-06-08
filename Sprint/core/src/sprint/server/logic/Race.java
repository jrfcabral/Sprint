package sprint.server.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.Semaphore;

import sprint.server.gui.ScoreMenu;
import sprint.server.net.Lobby;
import sprint.server.net.PlayerControls;
import sun.awt.Mutex;
import utils.CameraManager;
import utils.Settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
	private static final int OIL_DURATION = 10000;
	private static final int OIL_SIZE = 4;
	private static final int DIRECTION = 1;
	private static final int LAP_NUMBER = 3;
	private Texture trackTex;

	private SpriteBatch batch;
	private World world;
	private Track track;
	private OrthographicCamera camera;	
	private CameraManager camManager;
	private ArrayList<Car> cars;
	private List<Sprite> oilSprites;
	private boolean ended;
	private final StateMachine stateMachine;
	private Lobby lobby;
	private ArrayList<Vector2> oilPoints;	
	private LinkedList<String> positions;
	private LinkedList<Body> deleteQueueBody;
	private LinkedList<Sprite> deleteQueueSprite;
	private Box2DDebugRenderer debugRenderer;
	private Semaphore sem;
		
	public Race(StateMachine stateMachine, Lobby lobby){
		sem = new Semaphore(1, false);
		this.lobby = lobby;
		this.deleteQueueBody = new LinkedList<Body>();
		deleteQueueSprite = new LinkedList<Sprite>();
		world  = new World(new Vector2(0,0), true);
		world.setContinuousPhysics(true);
		world.setContactListener(this);
		batch = new SpriteBatch();
		track = new Track(world);
		Random rand = new Random();
		int trackNum = rand.nextInt(2);
		if(trackNum == 0){
			trackTex = new Texture("track01.png");
			track.addSegment(-350, -310, 350, -310);
			track.addSegment(-350, 300, 350, 300);
			track.addSegment(-350, -350, -350, 350);
			track.addSegment(352, -350, 352, 350);
			track.addCurveLR(new Vector2(-300, -310), new Vector2(-350, -200), new Vector2(-350, -200), 0, 50);
			track.addCurveLR(new Vector2(-300, 300), new Vector2(-350, 200), new Vector2(-350, 200), 0, 50);
			track.addCurveLR(new Vector2(300, -310), new Vector2(350, -230), new Vector2(350, -230), 0, 50);
			track.addCurveLR(new Vector2(300, 310), new Vector2(355, 200), new Vector2(355, 200), 0, 50);
			
			track.addSegment(-170, -158, 176, -158);
			track.addSegment(-170, 146, 176, 146);
			track.addSegment(-195, -110, -195, 100);
			track.addSegment(200, -110, 200, 100);
			track.addCurveLR(new Vector2(-165, -158), new Vector2(-195, -110), new Vector2(-195, -110), 0, 50);
			track.addCurveLR(new Vector2(-165, 148), new Vector2(-195, 100), new Vector2(-195, 100), 0, 50);
			track.addCurveLR(new Vector2(170, -158), new Vector2(200, -100), new Vector2(200, -110), 0, 50);
			track.addCurveLR(new Vector2(170, 148), new Vector2(200, 100), new Vector2(200, 100), 0, 50);
			
			track.addFinishLine(5, 223, 8,148);
		}
		else{
			trackTex = new Texture("track02.png");
			track.addSegment(-350, -310, 350, -310);
			track.addSegment(-350, 300, 350, 300);
			track.addSegment(-350, -350, -350, 350);
			track.addSegment(352, -350, 352, 350);
			track.addCurveLR(new Vector2(-300, -310), new Vector2(-350, -200), new Vector2(-350, -200), 0, 50);
			track.addCurveLR(new Vector2(-300, 300), new Vector2(-350, 200), new Vector2(-350, 200), 0, 50);
			track.addCurveLR(new Vector2(300, -310), new Vector2(350, -230), new Vector2(350, -230), 0, 50);
			track.addCurveLR(new Vector2(300, 310), new Vector2(355, 200), new Vector2(355, 200), 0, 50);
			
			track.addSegment(-170, 146, 176, 146);
			
			track.addCurveLR(new Vector2(-350, -87), new Vector2(-350, 76), new Vector2(-200, -5), 0, 50);
			track.addCurveLR(new Vector2(-150, -87), new Vector2(-150, 76), new Vector2(0, -5), 0, 50);
			track.addCurveLR(new Vector2(-150, 76), new Vector2(-170, 146), new Vector2(-210, 111), 0, 50);
			track.addSegment(176, 146, -50, -87);
			track.addSegment(352, 146, 50, -87);
			track.addSegment(352, 116, 50, -117);
			track.addCurveLR(new Vector2(50, -117), new Vector2(50, -87), new Vector2(30, -112), 0, 50);
			track.addSegment(222, -106, 50, -200);
			track.addCurveLR(new Vector2(50, -200), new Vector2(-50, -87), new Vector2(-100, -184), 0, 50);
			track.addSegment(-100, -210, 220, -210);
			track.addCurveLR(new Vector2(222, -106),  new Vector2(220, -210),  new Vector2(251, -163),  0,  50);
			track.addCurveLR(new Vector2(-100, -210), new Vector2(-150, -87), new Vector2(-205, -163),  0,  50);
			
			track.addFinishLine(5, 223, 8,148);
		}
		
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Settings.VIEWPORT_WIDTH, Settings.VIEWPORT_HEIGHT);
		camera.position.set(0, 0, 0);
		camera.update();
		oilPoints = new ArrayList<Vector2>();
		positions = new LinkedList<String>();
		camManager = new CameraManager();
		cars = new ArrayList<Car>();
		oilSprites = Collections.synchronizedList(new ArrayList<Sprite>());
		
		ended = false;
		this.stateMachine = stateMachine;
	}
	
	/**
	 * Clears the screen, then redraws the sprite of each element in its current position and with the correct orientation
	 */
	public void draw(){
			camManager.update(Gdx.graphics.getDeltaTime());
			camManager.applyTo(camera);	
			batch.setProjectionMatrix(camera.combined);
			
			ListIterator<Car> it = cars.listIterator();
			ListIterator<Sprite> itt= oilSprites.listIterator();
			
			batch.begin();
			batch.draw(trackTex, -400, -400, 800, 800);
			batch.end();
			try {
				sem.acquire();
				
				while(itt.hasNext()){
					Sprite oil = itt.next();
					batch.begin();
					oil.draw(batch);
					batch.end();
				}
				while(it.hasNext()){
					Car car = it.next();
					batch.begin();
					car.getSprite().draw(batch);
					batch.end();
					car.update();
					if (!car.getAlive())
						it.remove();					
				}
				
			} catch (InterruptedException e) {
				
			}	
			

			finally{
				sem.release();
			}		
			
			handleInput(Gdx.graphics.getDeltaTime());
			
			world.step(1/60f, 6, 2);
			
			

			checkEnd();
		}
		
		
	/**
	 * Fetches five players from the lobby, creates cars for them and assigns each car to the player controls
	 * @param identifiers
	 */
	public void startGame(LinkedList<String> identifiers) {
		String[] colors = new String[]{"Red", "Blue", "Green", "Pink", "Orange"};
		int i = identifiers.size()-1;
		int x = 20, y = 180;
		for (String id : identifiers){
			PlayerControls controls = new PlayerControls(id, stateMachine.getServer(), colors[i--]);
			Car car = new Car(this, controls, x, y);
			y += 20;
			addCar(car);
		}		
	}


	/** 
	 * Handles the server controls for commanding the camera
	 * @param deltaTime the timestep of the simulation
	 */
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
	
	
	
	
	
	/**
	 * Returns the Box2d world where the simluation is running
	 * @return the Box2d world
	 */
	public World getWorld(){
		return world;
	}	
	
	/**
	 * 
	 * @param car the car to be removed from the simulation
	 */
	 public void removeCar(Car car){
		 this.cars.remove(car);
	 }

	 /**
	  * 
	  * @param car the car to be added to the simulation
	  */
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
			car.incrementLap();
		}
		else{			
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
		ListIterator<Car> it = cars.listIterator();
		while(it.hasNext()){
			Car car = it.next();
			if(car.getLaps() >= LAP_NUMBER){
				oneEnded = true;
				if (!car.isDone())
					positions.add(car.getColor());
				car.setDone(true);
				try {
					sem.acquire();
					cars.remove(car);
					world.destroyBody(car.getBody());
					it = cars.listIterator();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				finally{
					sem.release();
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
			Thread t = new Thread(){
				@Override
				public void run(){
					try {Thread.sleep(30000);} catch (InterruptedException e) {}
					ended = true;
				}
			};
			t.start();
		}
	}

	public boolean getEnded() {
		return ended;
	}

	/**
	 * Checks for the end of the race, and ocasionally causes slippery oil to appear on the field. 
	 */
	@Override
	public void update() { 
		checkEnd();
		if (ended)
			this.stateMachine.setState(new ScoreMenu(this.positions, this.lobby, this.stateMachine));
		Random randy = new Random();
		if (oilPoints.size() < 100 && randy.nextInt(10000) < 20){
			Car car = this.cars.get(randy.nextInt(this.cars.size()));
			Vector2 pos = car.getPosition();
			pos.x -= 60*Math.cos(car.getAngle());
			pos.y -= 60*Math.sin(car.getAngle());
			this.oilPoints.add(pos);
						
		}
		if (randy.nextInt(10000) < 20 && oilPoints.size() > 1){
			Vector2 pos = this.oilPoints.get(randy.nextInt(this.oilPoints.size()));
			createOil(pos);
		}
		
		for(Body body: deleteQueueBody)
		{
			this.world.destroyBody(body);
			deleteQueueBody.removeFirstOccurrence(body);
			
		}
		ListIterator<Sprite> it = deleteQueueSprite.listIterator();
		while(it.hasNext()){
			Sprite spr = it.next();
			try {
				sem.acquire();
				oilSprites.remove(spr);
				deleteQueueSprite.removeFirstOccurrence(spr);
				it = deleteQueueSprite.listIterator();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
				sem.release();
			}
		}
	}
	/**
	 * Creates an oil splatter at the target position, and generates a thread to remove it.
	 * @param pos the position where the oil should be generated
	 */
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
		final Sprite blotch = new Sprite(new Texture("oil.png"));
		
		blotch.setOriginCenter();
		blotch.setSize(OIL_SIZE*2,  OIL_SIZE*2);
		blotch.setPosition(pos.x - (blotch.getWidth()/2.0f),  pos.y - (blotch.getHeight()/2.0f));
		blotch.setRotation((float)(oild.angle*Math.PI/180f));
		try {
			sem.acquire();
			oilSprites.add(blotch);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally{
			sem.release();
		}
			
		
		
		Thread t = new Thread(){
			@Override
			public void run(){
				try {Thread.sleep(OIL_DURATION);} catch (InterruptedException e) {}
				Race.this.deleteQueueBody.add(oil);
				Race.this.deleteQueueSprite.add(blotch);	
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
