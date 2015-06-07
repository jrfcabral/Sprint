package sprint.tests;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import sprint.server.gui.MainMenu;
import sprint.server.logic.Car;
import sprint.server.logic.Race;
import sprint.server.logic.State;
import sprint.server.logic.StateMachine;
import sprint.server.logic.Track;
import sprint.server.net.PlayerControls;
import utils.Settings;


public class Tests implements State, ContactListener{
	private SpriteBatch batch;
	private World world;
	private Track track;
	private Car testCar;
	private OrthographicCamera camera;
	private Box2DDebugRenderer debugRenderer;
	private final StateMachine stateMachine;
	private Stage testStat;
	private Skin testSkin;
	private TextArea testStatArea;
	private TextButton back;
	
	private boolean throttle;
	private boolean brake;
	private Car.SteerDirection steer;
	private boolean ended;
	private final int LAP_NUMBER = 3;
	private int passed;
	private int failed;
	

	private Texture trackTex;
	
	public Tests(StateMachine machine){
		world = new World(new Vector2(0, 0), true);
		batch = new SpriteBatch();
		stateMachine = machine;
		world.setContactListener(this);
		
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Settings.VIEWPORT_WIDTH, Settings.VIEWPORT_HEIGHT);
		camera.position.set(0, 0, 0);
		camera.update();
		testCar = new Car(world, 0, 0, "Red");
		
		track = new Track(world);
		track.addSegment(25, -50, 25, 50);
		track.addFinishLine(-25, -25, -35, 25);
		
		
		
		throttle = false;
		brake = false;
		steer = Car.SteerDirection.SteerNone;
		
		testStat = new Stage();
		testSkin = new Skin(Gdx.files.internal("data/uiskin.json"));
		
		passed = 0;
		failed = 0;
		String passded = "Passed: " + Integer.toString(passed);
		String failded = "Failed: " + Integer.toString(failed);
		
		testStatArea = new TextArea(passded + "\n" + failded, testSkin);
		testStatArea.setSize(Gdx.graphics.getWidth()*0.3f, Gdx.graphics.getHeight()*0.1f);
		testStatArea.setPosition(0,  100);
		
		back = new TextButton("Back to Main", testSkin);
		back.setSize(Gdx.graphics.getWidth()*0.3f, Gdx.graphics.getHeight()*0.05f);
		back.setPosition(0, 50);
		back.addListener(new ClickListener(-1){
			public void clicked(InputEvent event, float x, float y){
				Tests.this.stateMachine.setState(new MainMenu(stateMachine));
			}
		});
		ended = false;
		
		testStat.addActor(testStatArea);
		testStat.addActor(back);
		
		Gdx.input.setInputProcessor(testStat);
		
		runTests();
	}	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void create() {
	}

	@Override
	public void draw() {
		String passded = "Passed: " + Integer.toString(passed);
		String failded = "Failed: " + Integer.toString(failed);
		testStatArea.setText(passded + "\n" + failded);
		
		batch.setProjectionMatrix(camera.combined);
		debugRenderer.render(world, camera.combined);
		batch.begin();
	
		testCar.getSprite().draw(batch);
		batch.end();
		testStat.draw();
		testCar.update(throttle, brake, steer);
		debugRenderer.render(world, camera.combined);
		world.step(1/60f, 6, 2);
		
	}
	
	
	public void runTests(){	
		testAccelerate();
		testBrake();
		testTurnRight();
		testTurnLeft();
		testCollisionWall();
		testCollisionFinish();
		testMultipleCars();
	}
	
	public boolean testAccelerate(){
		testCar.setVelocity(0f);
		testCar.update(false, false, Car.SteerDirection.SteerNone);
		
		for(int i = 0; i < 10; i++){
			testCar.update(true, false, Car.SteerDirection.SteerNone);
			world.step(1/60f, 6, 2);
		}
		if(testCar.getVelocity() <= 0){
			System.out.println("Accelerate test failed.");
			failed++;
			return false;
		}
		else{
			passed++;
			return true;
		}
	}
	
	
	
	private boolean testBrake(){
		testCar.setVelocity(0f);
		testCar.update(false, false, Car.SteerDirection.SteerNone);
		
		testCar.setVelocity(30);
		testCar.update(false, false, Car.SteerDirection.SteerNone);
		
		for(int i = 0; i < 10; i++){
			testCar.update(false, true, Car.SteerDirection.SteerNone);
			world.step(1/60f, 6, 2);
		}
		
		if(testCar.getVelocity() >= 30){
			System.out.println("Brake test failed");
			failed++;
			return false;
		}
		passed++;
		return true;
	}
	
	private boolean testTurnLeft(){
		testCar.setVelocity(0f);
		testCar.update(false, false, Car.SteerDirection.SteerNone);
		
		testCar.setVelocity(1.0f);
		testCar.update(false, false, Car.SteerDirection.SteerNone);
		
		float oldAng = testCar.getAngle();
		
		for(int i = 0; i < 10; i++){
			testCar.update(false, false, Car.SteerDirection.SteerLeft);
			world.step(1/60f, 6, 2);
		}
		
		if(testCar.getAngle() >= oldAng){
			System.out.println("Turn Left test failed.");
			failed++;
			return false;
		}
		else{
			passed++;
			return true;
		}
	}
	
	private boolean testTurnRight(){
		testCar.setVelocity(0f);
		testCar.update(false, false, Car.SteerDirection.SteerNone);
		
		testCar.setVelocity(1.0f);
		testCar.update(false, false, Car.SteerDirection.SteerNone);
		
		float oldAng = testCar.getAngle();
		
		for(int i = 0; i < 10; i++){
			testCar.update(false, false, Car.SteerDirection.SteerRight);
			world.step(1/60f, 6, 2);
		}
		
		if(testCar.getAngle() <= oldAng){
			failed++;
			return false;
		}
		else{
			passed++;
			return true;
		}
	}
	
	
	private boolean testCollisionWall(){
		testCar.setVelocity(0f);
		testCar.update(false, false, Car.SteerDirection.SteerNone);
		
		testCar.setVelocity(50.0f);
		testCar.update(false, false, Car.SteerDirection.SteerNone);
		world.step(1/60f, 6, 2);
		float oldDir = testCar.getLinearVelocity().x;
		if(oldDir > 0){
			oldDir = 1;
		}
		else{
			oldDir = 0;
		}
		
		
		for(int i = 0; i < 30; i++){
			testCar.update(false, false, Car.SteerDirection.SteerNone);
			world.step(1/60f, 6, 2);
		}
		
		float newDir =  testCar.getLinearVelocity().x;
		if(newDir > 0){
			newDir = 1;
		}
		else{
			newDir = 0;
		}
		
		
		if(oldDir == newDir){
			failed++;
			testCar.setVelocity(0f);
			return false;
		}
		else{
			passed++;
			testCar.setVelocity(0f);
			return true;
		}
	}
	
	public boolean testCollisionFinish(){
		testCar.setVelocity(-30f);
		testCar.setAngle(180); //In degrees
		int laps = testCar.getLaps();
		for(int i = 0; i < 200; i++){
			testCar.update(false, false, Car.SteerDirection.SteerNone);
			world.step(1/60f, 6, 2);
		}
		
		if(laps < testCar.getLaps()){
			failed++;
			testCar.setVelocity(0f);
			return false;
		}
		testCar.setVelocity(30f);
		testCar.setAngle(0);
		
		for(int i = 0; i < 10; i++){
			testCar.update(false, false, Car.SteerDirection.SteerNone);
			world.step(1/60f, 6, 2);
		}
		
		if(laps == testCar.getLaps()){
			passed++;
			testCar.setVelocity(0f);
			return true;
		}
		else{
			failed++;
			testCar.setVelocity(0f);
			return false;
		}
		
	}
	
	private boolean testMultipleCars(){
		Car testCar2 = new Car(world, -150, -150, "Blue");
		
		testCar2.setAngle(testCar.getAngle());
		testCar2.setAngle(90);
		testCar.update(false, false, Car.SteerDirection.SteerNone);
		testCar2.update(false, false, Car.SteerDirection.SteerNone);
		world.step(1/60f, 6, 2);
		
		if(testCar.getAngle() == testCar2.getAngle()){
			failed++;
			testCar2.dispose();
			return false;
		}
		
		testCar2.setVelocity(30f);
		testCar2.update(false, false, Car.SteerDirection.SteerNone);
		if(testCar.getVelocity() == testCar2.getVelocity()){
			failed++;
			testCar2.dispose();
			return false;
		}
		else{
			passed++;
			testCar2.dispose();
			return true;
		}
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beginContact(Contact contact) {
		Car car;
		Object a = contact.getFixtureA().getBody().getUserData();
		Object b = contact.getFixtureB().getBody().getUserData();
		if (a!=null)
		System.out.println(a.getClass().getName());
		if (b != null)
		System.out.println(b.getClass().getName());
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
		
		if (car.getLinearVelocity().x > 0){
			System.out.println("dei uma voltinha na minha lambreta");
			car.incrementLap();
		}
		else{
			System.out.println("desdei uma voltinha na minha lambreta");
			car.decrementLap();
		}
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
	
	
}
