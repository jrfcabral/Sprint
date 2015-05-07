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
		CircleShape shape = new CircleShape();
		shape.setRadius(10);
		fdef.shape = shape;
		fdef.density = 1f;
		fdef.restitution = 1f;
		fdef.friction = 1f;
		body.createFixture(fdef);
		shape.dispose();
		
		maxSteer = (float) (Math.PI/120);
		angle = (float) (3*Math.PI);
	}
	
	public void update (boolean throttle, SteerDirection dir)
	{
		switch(dir){
		case SteerLeft:		
			this.angle += maxSteer;
			break;
		case SteerRight:
			this.angle -= maxSteer;
			break;
		default:break;
		}
		angle = (float) (angle%(Math.PI*2));
		
		Vector2 force = new Vector2(1000,0);
		force = force.rotate((float) Math.toDegrees(angle));
		
		
		if (throttle){
			Vector2 currentDirection = new Vector2(1,0);
			currentDirection.rotate((float) Math.toDegrees(angle));
			
			
			body.setLinearVelocity(new Vector2(body.getLinearVelocity().x= body.getLinearVelocity().x*body.getLinearVelocity().dot(currentDirection),body.getLinearVelocity().y= body.getLinearVelocity().x*body.getLinearVelocity().dot(currentDirection)));
			
			body.applyForceToCenter(force, true);			
		}
		body.setTransform(body.getPosition(), angle);
		
	}
}
