package sprint.server.logic;

import java.util.ArrayList;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
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
		def.position.set(new Vector2(0,0));
		body = world.createBody(def);
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(1.5f, 1f);
		fdef.shape = shape;
		fdef.density = 1f;
		fdef.restitution = 0f;
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
		
		switch(dir){
		case SteerLeft:
			if (body.getAngularVelocity() > -3.5)
			body.setAngularVelocity(body.getAngularVelocity()-0.1f);
			break;
		case SteerRight:
			if (body.getAngularVelocity() < 3.5)
				body.setAngularVelocity(body.getAngularVelocity()+0.1f);
			
			break;
		default:
			if (body.getAngularVelocity() > 0)
				body.setAngularVelocity((float) Math.max(body.getAngularVelocity()-0.1, 0));
			else if (body.getAngularVelocity() < 0)
				body.setAngularVelocity((float) Math.min(body.getAngularVelocity()+0.1f, 0));
			break;
		}
		if (body.getLinearVelocity().isZero())
			body.setAngularVelocity(0);

		if(this.getVelocity() > 0.2f)
			body.applyForce(new Vector2(getVelocity()*1f,0).rotate((float) Math.toDegrees(body.getAngle())+180f), body.getWorldCenter(), true);
		if (brake){			
			body.applyForce(new Vector2(100,0).rotate((float) Math.toDegrees(body.getAngle())+180f), body.getWorldCenter(), true);

		}
			
		else if (throttle){
			body.applyForce(new Vector2(50,0).rotate((float) Math.toDegrees(body.getAngle())), body.getWorldCenter(), true);
			
		}

		if (!throttle && getVelocity() <0.5f)
			body.setLinearVelocity(new Vector2(0,0));
		
		linearizeVelocity(0.85f);

		
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
		
		
		System.out.println("in"+body.getLinearVelocity());
		Vector2 forwardDir = new Vector2(1,0);
		forwardDir.rotate((float) Math.toDegrees(body.getAngle()));

		Vector2 sideDir = new Vector2(1,0);
		sideDir.rotate((float)Math.toDegrees(body.getAngle())+90f);
		Vector2 currVelocity = body.getLinearVelocity().cpy();
		float dotprod = forwardDir.x*currVelocity.x+forwardDir.y*currVelocity.y;
		float sideprod = (sideDir.x*currVelocity.x+sideDir.y*currVelocity.y)*factor;
		System.out.println(forwardDir.x*currVelocity.x+forwardDir.y*currVelocity.y);
		body.setLinearVelocity(forwardDir.x*dotprod+sideprod*sideDir.x, forwardDir.y*dotprod+sideprod*sideDir.y);

		System.out.println("out"+body.getLinearVelocity());
				
	}
	
	public Sprite getSprite(){
		return this.carSprite;
	}
}
