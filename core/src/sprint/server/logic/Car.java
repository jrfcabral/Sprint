package sprint.server.logic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;



/**
 * Represents a car inside the game logic.
 */
public class Car{
	public static enum SteerDirection {
		SteerLeft, SteerRight, SteerNone;
	}
	

	private Body body;
	Sprite carSprite;
	private float angle;
	private float maxSteer;
	private float topSpeed;
	private float maxForce;
	
	public float getVelocity(){
		return body.getLinearVelocity().len();
	}
	/**
	 * @return the angle
	 */
	public float getAngle() {
		return angle;
	}

	/**
	 * @param angle the angle to set
	 */
	public void setAngle(float angle) {
		this.angle = angle;
	}
	
	public Car(World world)
	{	
		BodyDef def = new BodyDef();
		def.type = BodyDef.BodyType.DynamicBody;		
		def.position.set(new Vector2(200,200));
		body = world.createBody(def);
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(4.5f, 2.5f);
		fdef.shape = shape;
		fdef.density = 1f;
		fdef.restitution = 0.1f;
		fdef.friction = 1f;
		body.createFixture(fdef);
		shape.dispose();
		carSprite = new Sprite(new Texture("MLGCar.png"));
		carSprite.setSize(10, 5);
		carSprite.setOrigin(carSprite.getWidth()/2.0f,  carSprite.getHeight()/2.0f);

		
		maxSteer = (float) (Math.PI/120);
		angle = (float) (3*Math.PI);
	}
	
	/**
	 * Updates car position
	 * @param throttle whether the car has been accelerated on this time step
	 * @param dir direction of the steering
	 */
	public void update (boolean throttle,boolean brake, SteerDirection dir)
	{
		//rotate car according to input
		switch(dir){
		case SteerLeft:
			if (body.getAngularVelocity() > -2.5)
			body.setAngularVelocity(body.getAngularVelocity()-0.1f);
			break;
		case SteerRight:
			if (body.getAngularVelocity() < 2.5)
				body.setAngularVelocity(body.getAngularVelocity()+0.1f);
			
			break;
		//if no input is given, "slowly" reduce rotational speed
		default:
			if (body.getAngularVelocity() > 0)
				body.setAngularVelocity((float) Math.max(body.getAngularVelocity()-0.2, 0));
			else if (body.getAngularVelocity() < 0)
				body.setAngularVelocity((float) Math.min(body.getAngularVelocity()+0.2f, 0));
			break;
		}
		//can't rotate a resting vehicle
		if (body.getLinearVelocity().isZero())
			body.setAngularVelocity(0);
		
		//apply some simulated friction
		if(this.getVelocity() > 0.2f)
			body.applyForce(new Vector2(getVelocity()*1f,0).rotate((float) Math.toDegrees(body.getAngle())+180f), body.getWorldCenter(), true);
		
		//process braking
		if (brake){			
			body.applyForce(new Vector2(1050,0).rotate((float) Math.toDegrees(body.getAngle())+180f), body.getWorldCenter(), true);

		}
		
		//process acceleration
		else if (throttle){
			if(this.getVelocity() < 100f)
				body.applyForce(new Vector2(650,0).rotate((float) Math.toDegrees(body.getAngle())), body.getWorldCenter(), true);
			if(this.getVelocity() < 50f)
				body.applyForce(new Vector2(650,0).rotate((float) Math.toDegrees(body.getAngle())), body.getWorldCenter(), true);
		}
		
		//if the car is moving very slowly and not trying to accelerate, stop it - allows friction to bring the car to a halt
		if (!throttle && getVelocity() <0.5f)
			body.setLinearVelocity(new Vector2(0,0));
		

		//kill some of the sideways velocity of the car - simulates the effect wheels have on steering
		//prevents the car from completely gliding when turning
		linearizeVelocity(0.55f);

		

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
	
	public Sprite getSprite(){
		return this.carSprite;
	}
	
	public void setVelocity(float vel){
		body.setLinearVelocity(vel, 0);
	}
}
