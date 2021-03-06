package sprint.server.logic;

import sprint.server.net.PlayerControls;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;



/**
 * Represents a car inside the game logic.
 */
public class Car {
	private static final int OIL_DURATION = 2000;
	public static enum SteerDirection {
		SteerLeft, SteerRight, SteerNone;
	}
	
	public static enum Color{
		Red{
			public Sprite getTex(){
				Sprite spr;
				spr = new Sprite(new Texture("MLGCar.png"));
				return spr;
			}
		}, 
		Blue{
			public Sprite getTex(){
				Sprite spr;
				spr = new Sprite(new Texture("MLGCarBlue.png"));
				return spr;
			}
		}, 
		Green{
			public Sprite getTex(){
				Sprite spr;
				spr = new Sprite(new Texture("MLGCarGreen.png"));
				return spr;
			}
		}, 
		Pink{
			public Sprite getTex(){
				Sprite spr;
				spr = new Sprite(new Texture("MLGCarPink.png"));
				return spr;
			}
		}, 
		Orange{
			public Sprite getTex(){
				Sprite spr;
				spr = new Sprite(new Texture("MLGCarOrange.png"));
				return spr;
			}
		};
		public abstract Sprite getTex();
	}
	
	
	private Body body;
	private Sprite carSprite;
	private PlayerControls playerControls;
	private World world;
	private int laps = 0;

	private float linearFactor = 0.55f;

	private boolean done;

	
	/**
	 * @return true if this car is no longer in the race, false otherwise
	 */
	public boolean isDone() {
		return done;
	}
	/**
	 * @param done true to remove car from the race
	 */
	public void setDone(boolean done) {
		this.done = done;
	}
	public Vector2 getLinearVelocity(){
		return body.getLinearVelocity();
	}
	public float getVelocity(){
		return body.getLinearVelocity().len();
	}
	/**
	 * @return the angle
	 */
	public float getAngle() {
		return body.getAngle();
	}
	
	public Car(World world, int x, int y, String color){ //Used for tests
		done = false;
		this.world = world;
		BodyDef def = new BodyDef();
		def.type = BodyDef.BodyType.DynamicBody;		
		def.position.set(new Vector2(x, y));
		body = world.createBody(def);
		body.setUserData(this);
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(9f, 5f);
		fdef.shape = shape;
		fdef.density = 1f;
		fdef.restitution = 0.1f;
		fdef.friction = 1f;		
		body.createFixture(fdef);
		body.setUserData(this);
		shape.dispose();
		carSprite = Color.valueOf(color).getTex();
		carSprite.setSize(20, 10);
		carSprite.setOriginCenter();
		
	}
	
	public Car(Race race, PlayerControls controls, int x, int y)
	{	
		this.world = race.getWorld();
		BodyDef def = new BodyDef();
		def.type = BodyDef.BodyType.DynamicBody;		
		def.position.set(new Vector2(x, y));
		body = world.createBody(def);
		body.setUserData(this);
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(9f, 5f);
		fdef.shape = shape;
		fdef.density = 1f;
		fdef.restitution = 0.1f;
		fdef.friction = 1f;		
		body.createFixture(fdef);
		body.setUserData(this);
		shape.dispose();
		playerControls = controls;
		Color color = Color.valueOf(controls.getColor());
		carSprite = color.getTex(); 
		carSprite.setSize(20, 10);
		//carSprite.setOrigin(carSprite.getWidth()/2.0f,  carSprite.getHeight()/2.0f);
		carSprite.setOriginCenter();
		
		
		
	}
	
	/**
	 * Checks the commands received via the internet and applies them to the car. Then simulates the physics of the car for the current timestep.
	 */
	public void update(){
		if(!this.playerControls.isActive()){
			this.dispose();
		}
			
		this.update(playerControls.getThrottle(), playerControls.getBrake(), playerControls.getSteer());
	}
	
	/**
	 * Updates car position
	 * @param throttle whether the car has been accelerated on this time step
	 * @param dir direction of the steering
	 */
	public void update (boolean throttle,boolean brake, SteerDirection dir)
	{
		
		switch(dir){
		case SteerLeft:
			if (body.getAngularVelocity() > -2.5)
			body.setAngularVelocity(body.getAngularVelocity()-0.1f);
			break;
		case SteerRight:
			if (body.getAngularVelocity() < 2.5)
				body.setAngularVelocity(body.getAngularVelocity()+0.1f);
			
			break;
		default:
			if (body.getAngularVelocity() > 0)
				body.setAngularVelocity((float) Math.max(body.getAngularVelocity()-0.2, 0));
			else if (body.getAngularVelocity() < 0)
				body.setAngularVelocity((float) Math.min(body.getAngularVelocity()+0.2f, 0));
			break;
		}
		if (body.getLinearVelocity().isZero())
			body.setAngularVelocity(0);

		if(this.getVelocity() > 0.2f)
			body.applyForce(new Vector2(getVelocity()*1f,0).rotate((float) Math.toDegrees(body.getAngle())+180f), body.getWorldCenter(), true);
		if (brake){			
			body.applyForce(new Vector2(1050,0).rotate((float) Math.toDegrees(body.getAngle())+180f), body.getWorldCenter(), true);

		}
			
		else if (throttle){
			if(this.getVelocity() < 300f)
				body.applyForce(new Vector2(1050,0).rotate((float) Math.toDegrees(body.getAngle())), body.getWorldCenter(), true);
			if(this.getVelocity() < 150f)
				body.applyForce(new Vector2(1550,0).rotate((float) Math.toDegrees(body.getAngle())), body.getWorldCenter(), true);
		}

		if (!throttle && getVelocity() <0.5f)
			body.setLinearVelocity(new Vector2(0,0));		
		
		linearizeVelocity(linearFactor);
		//System.out.println(this.getVelocity());
		//System.out.println(getAngle());
		
		carSprite.setPosition(body.getPosition().x - (carSprite.getWidth()/2.0f), body.getPosition().y - (carSprite.getHeight()/2.0f));
		carSprite.setRotation((float) ((float) body.getAngle()*180f/Math.PI));
		//carSprite.setScale(1.0f+(float)(Math.abs(Math.sin(getAngle()))*0.55f), 1.0f-(float)(Math.abs(Math.sin(getAngle()))*0.15f));

	}
	
	/**
	 * Adjusts the current linear velocity so that it points in the direction of the current angle of the car. A factor of 0 means that all velocity not in the direction
	 * of the car will be killed, a 1 factor makes this call do nothing
	 * @param factor value between 0 and 1
	 */
	private void linearizeVelocity(float factor){
		if (factor < 0 || factor > 1)
			throw new IllegalArgumentException();
		
		
		//System.out.println("in"+body.getLinearVelocity());
		Vector2 forwardDir = new Vector2(1,0);
		forwardDir.rotate((float) Math.toDegrees(body.getAngle()));

		Vector2 sideDir = new Vector2(1,0);
		sideDir.rotate((float)Math.toDegrees(body.getAngle())+90f);
		Vector2 currVelocity = body.getLinearVelocity().cpy();
		float dotprod = forwardDir.x*currVelocity.x+forwardDir.y*currVelocity.y;
		float sideprod = (sideDir.x*currVelocity.x+sideDir.y*currVelocity.y)*factor;
		//System.out.println(forwardDir.x*currVelocity.x+forwardDir.y*currVelocity.y);
		body.setLinearVelocity(forwardDir.x*dotprod+sideprod*sideDir.x, forwardDir.y*dotprod+sideprod*sideDir.y);
		
		//System.out.println("out"+body.getLinearVelocity());
				
	}
	
	/**
	 * Increments the number of laps this car has performed.
	 * Should be called when the car crosses the finish line in the correct direction
	 */
	public void incrementLap(){
		laps++;
	}
	/**
	 * Decrementes the number of laps this car has performed.
	 * Called when the car crosses the finish line in the wrong direction
	 */
	public void decrementLap(){
		if(laps >0)
			laps--;
	}
	
	/**
	 * 
	 * @return the laps this car has performed
	 */
	public int getLaps(){
		return this.laps;
	}
	
	/**
	 * 
	 * @return the sprite that represents the car
	 */
	public Sprite getSprite(){
		return this.carSprite;
	}
	/**
	 * Sets current linear velocity. Maintains the angle of the current movement.
	 * @param vel the new velocity of the car
	 */
	public void setVelocity(float vel){
		body.setLinearVelocity(vel*((float)Math.cos(getAngle())), vel*((float)Math.sin(getAngle())));
	}
	
	/**
	 * Disposes of the car in terms of its physical representation in the simulator
	 */
	public void dispose() {	
		this.world.destroyBody(this.body);		
	}
	
	/**
	 * 
	 * @return true if the player is still connected to the car, false otherwise
	 */
	public boolean getAlive(){
		return this.playerControls.isActive();
	}
	
	/**
	 * 
	 * @return the identifier of the device being used to control this car
	 */
	public String getIdentifier(){
		return this.playerControls.getId();
	}
	
	/**
	 * 
	 * @param angle the current angle of the car, in degrees
	 */
	public void setAngle(float angle){
		body.setTransform(0f,  0f, ((float) (angle*Math.PI/180f)));
	}
	/**
	 * 
	 * @return the vector representing the position of the car in the world.
	 */
	public Vector2 getPosition(){
		return this.body.getPosition();
	}
	
	/**
	 * Applies the effects of a oil slip to the car for the duration and then resets it to the normal status.
	 */
	public void applyOil(){
		this.linearFactor = 1f;
		new Thread(){
			public void run(){
				try {Thread.sleep(OIL_DURATION);} catch (InterruptedException e) {}
				Car.this.linearFactor = 0.55f;
			}
		}.start();
	}
	
	/**
	 * @return the color of the car
	 */
	public String getColor(){
		return this.playerControls.getColor();
	}
	
	public Body getBody(){
		return this.body;
	}
}
