package sprint.tests;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
	private int passed;
	private int failed;
	
	/*public int passed;
	public int failed;
	public Car car;
	public Race race;
	
	public Tests(Race race){
		passed = 0;
		failed = 0;
		this.race = race;
		car = race.getCar();
	}
	
	public void run(){
		if(testAccelerate()){
			passed++;
		}
		else{
			failed++;
		}
		
		if(testBrake()){
			passed++;
		}
		else{
			failed++;
		}
		
		if(testTurnLeft()){
			passed++;
		}
		else{
			failed++;
		}
		
		if(testTurnRight()){
			passed++;
		}
		else{
			failed++;
		}
		
		System.out.print("Passed: " + passed + "\nFailed: " + failed + "\n");
	}
	
	private boolean testAccelerate(){
		car.setVelocity(0);
		car.update(false, false, Car.SteerDirection.SteerNone);
		
		for(int i = 0; i < 10; i++){
			car.update(true, false, Car.SteerDirection.SteerNone);
			race.getWorld().step(1/60f, 6, 2);
		}
		if(car.getVelocity() <= 0){
			System.out.println("Accelerate test failed.");
			return false;
		}
		else{
			return true;
		}
		
	}
	
	private boolean testBrake(){
		car.setVelocity(0f);
		car.update(false, false, Car.SteerDirection.SteerNone);
		
		car.setVelocity(30);
		car.update(false, false, Car.SteerDirection.SteerNone);
		
		for(int i = 0; i < 10; i++){
			car.update(false, true, Car.SteerDirection.SteerNone);
			race.getWorld().step(1/60f, 6, 2);
		}
		
		if(car.getVelocity() >= 30){
			System.out.println("Brake test failed");
			return false;
		}
		
		return true;
	}
	
	private boolean testTurnLeft(){
		car.setVelocity(0f);
		car.update(false, false, Car.SteerDirection.SteerNone);
		
		car.setVelocity(1.0f);
		car.update(false, false, Car.SteerDirection.SteerNone);
		
		float oldAng = car.getAngle();
		
		for(int i = 0; i < 10; i++){
			car.update(false, false, Car.SteerDirection.SteerLeft);
			race.getWorld().step(1/60f, 6, 2);
		}
		
		if(car.getAngle() >= oldAng){
			System.out.println("Turn Left test failed.");
			return false;
		}
		else return true;
	}
	
	private boolean testTurnRight(){
		car.setVelocity(0f);
		car.update(false, false, Car.SteerDirection.SteerNone);
		
		car.setVelocity(1.0f);
		car.update(false, false, Car.SteerDirection.SteerNone);
		
		float oldAng = car.getAngle();
		
		for(int i = 0; i < 10; i++){
			car.update(false, false, Car.SteerDirection.SteerRight);
			race.getWorld().step(1/60f, 6, 2);
		}
		
		if(car.getAngle() <= oldAng){
			return false;
		}
		else return true;
	}*/

	public Tests(StateMachine machine){
		world = new World(new Vector2(0, 0), true);
		batch = new SpriteBatch();
		stateMachine = machine;
		world.setContactListener(this);
		
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Settings.VIEWPORT_WIDTH, Settings.VIEWPORT_HEIGHT);
		camera.position.set(0, 0, 0);
		camera.update();
		testCar = new Car(world, 0, 0);
		
		track = new Track(world);
		track.addSegment(25, -50, 25, 50);
		
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
		batch.begin();
		testCar.getSprite().draw(batch);
		batch.end();
		testStat.draw();
		testCar.update(throttle, brake, steer);
		debugRenderer.render(world, camera.combined);
		//world.step(1/60f, 6, 2);
		
	}
	
	
	public void runTests(){
		if(testAccelerate())
			passed++;
		else
			failed++;
	}
	
	public boolean testAccelerate(){
		testCar.setVelocity(0);
		testCar.update(false, false, Car.SteerDirection.SteerNone);
		
		for(int i = 0; i < 10; i++){
			testCar.update(true, false, Car.SteerDirection.SteerNone);
			world.step(1/60f, 6, 2);
		}
		if(testCar.getVelocity() <= 0){
			System.out.println("Accelerate test failed.");
			return false;
		}
		else{
			return true;
		}
	}
	
	
	
	
	
	
	
	

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
		
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
