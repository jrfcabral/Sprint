package sprint.server.logic;

import sprint.server.net.PlayerControls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.sun.corba.se.impl.orbutil.concurrent.Mutex;



/**
 * Represents a car inside the game logic.
 */
public class Car implements Disposable{
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
	
	public Car(Race race, PlayerControls controls)
	{	
		this.world = race.getWorld();
		BodyDef def = new BodyDef();
		def.type = BodyDef.BodyType.DynamicBody;		
		def.position.set(new Vector2(20,150));
		body = world.createBody(def);
		body.setUserData(this);
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(4.5f, 2.5f);
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
		carSprite.setSize(10, 5);
		carSprite.setOrigin(carSprite.getWidth()/2.0f,  carSprite.getHeight()/2.0f);
		
		
		
	}
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
			if(this.getVelocity() < 100f)
				body.applyForce(new Vector2(650,0).rotate((float) Math.toDegrees(body.getAngle())), body.getWorldCenter(), true);
			if(this.getVelocity() < 50f)
				body.applyForce(new Vector2(650,0).rotate((float) Math.toDegrees(body.getAngle())), body.getWorldCenter(), true);
		}

		if (!throttle && getVelocity() <0.5f)
			body.setLinearVelocity(new Vector2(0,0));
		
		linearizeVelocity(0.55f);
		//System.out.println(this.getVelocity());
		//System.out.println(getAngle());
		
		carSprite.setPosition(body.getPosition().x - (carSprite.getWidth()/2.0f), body.getPosition().y - (carSprite.getHeight()/2.0f));
		carSprite.setRotation((float) ((float) body.getAngle()*180f/Math.PI));

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
	
	public void incrementLap(){
		laps++;
	}
	public void decrementLap(){
		if(laps >0)
			laps--;
	}
	
	public int getLaps(){
		return this.laps;
	}
	
	public Sprite getSprite(){
		return this.carSprite;
	}
	
	public void setVelocity(float vel){
		body.setLinearVelocity(vel*((float)Math.cos(getAngle())), vel*((float)Math.sin(getAngle())));
	}
	@Override
	public void dispose() {	
		this.world.destroyBody(this.body);		
	}
	
	public boolean getAlive(){
		return this.playerControls.isActive();
	}
	
	public String getIdentifier(){
		return this.playerControls.getId();
	}
	
}
