package sprint.server.logic;

import java.util.ArrayList;

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
	private float angle;
	private float maxSteer;
	private float topSpeed;
	private float maxForce;
	
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
		def.position.set(new Vector2(100,100));
		body = world.createBody(def);
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(20, 10);
		fdef.shape = shape;
		fdef.density = 1f;
		fdef.restitution = 1f;
		fdef.friction = 1f;
		body.createFixture(fdef);
		shape.dispose();
		
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
		
		switch(dir){
		case SteerLeft:
			if (body.getAngularVelocity() < 2000)
			body.applyTorque(-5000, true);
			break;
		case SteerRight:
			if (body.getAngularVelocity() < 2000)
			body.applyTorque(5000, true);
			break;
		default:break;
		}
		
		if (brake){
			body.applyForceToCenter(new Vector2(-2000,0).rotate(body.getAngle()), true);
		}
			
		else if (throttle){
			body.applyForceToCenter(new Vector2(2000,0).rotate(body.getAngle()), true);
		}
		linearizeVelocity(0.5f);
	}
	
	/**
	 * Adjusts the current linear velocity so that it points in the direction of the current angle of the car. A factor of 0 means that all velocity not in the direction
	 * of the car will be killed, a 1 factor makes this call do nothing
	 * @param factor value between 0 and 1
	 */
	private void linearizeVelocity(float factor){
		if (factor < 0 || factor > 1)
			throw new IllegalArgumentException();
		
		System.out.println("old"+ body.getLinearVelocity());		
		Vector2 forwardDirection = new Vector2(1,0);		
		forwardDirection.rotate((float) Math.toDegrees(body.getAngle()));
		Vector2 rightDirection = forwardDirection.cpy();
		rightDirection.rotate(-90f);
		Vector2 forwardVelocity = forwardDirection.scl(body.getLinearVelocity().dot(forwardDirection));
		Vector2 rightVelocity = rightDirection.scl(body.getLinearVelocity().dot(rightDirection)*factor);
		body.setLinearVelocity(forwardVelocity.add(rightVelocity));
		System.out.println("new"+body.getLinearVelocity());
				
	}
}
