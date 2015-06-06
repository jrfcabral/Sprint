package sprint.server.logic;

import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Track{
	
	private HashSet<Body> bodies;
	private ArrayList<BodyFixture> segments;
	private World world;
	private Body finishLine;	
	
	private static final float BOUNDARY_RESTITUTION = 1.0f;
	private static final float SENSOR_RESTITUTION = 0f;
	
	private class BodyFixture{
		public BodyDef body;
		public FixtureDef fixture;
		public Shape shape;
	}
	
	public Track(World world){
		if (world == null)
			throw new IllegalArgumentException();
		this.world = world;
		bodies = new HashSet<Body>();
		segments = new ArrayList<BodyFixture>();
		
	}
	
	/**
	 * <p>Adds a line segment boundary to the world this track is associated with.</p>
	 * 
	 * <p>After adding a segment to a Track you must always apply it so that the Shape object is correctly disposed of!</p>
	 * @param x initial x coordinate
	 * @param y initial y coordinate
	 * @param xf final x coordinate
	 * @param yf final y coordinate
	 */
	public void addSegment(int x, int y, int xf, int yf){
		BodyDef bodydef = new BodyDef();
		bodydef.position.set(new Vector2(0,0));
		bodydef.type = BodyType.StaticBody;
		Body body = world.createBody(bodydef);
		FixtureDef fdef = new FixtureDef();
		fdef.restitution=BOUNDARY_RESTITUTION;
		fdef.density = 0.1f;
		
		EdgeShape shape = new EdgeShape();
		shape.set(new Vector2(x,y), new Vector2(xf,yf));
		fdef.shape = shape;
		body.createFixture(fdef);
		BodyFixture registry = new BodyFixture();
		registry.body=bodydef;
		registry.fixture = fdef;
		registry.shape = shape;
		segments.add(registry);
	}
	

	public void addFinishLine(int x, int y, int xf, int yf){
		BodyDef bodydef = new BodyDef();
		bodydef.position.set(new Vector2(x,y));
		bodydef.type = BodyType.StaticBody;
		Body body = world.createBody(bodydef);
		body.setUserData(new String("finish"));
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(xf-x, yf-y);
		fdef.restitution = SENSOR_RESTITUTION;
		fdef.isSensor = true;
		fdef.shape = shape;		
		body.createFixture(fdef);
	
		
		
	}
	
	public void addCurveLR(Vector2 p1, Vector2 p2, Vector2 p3, int num, int limiter){
		if(num >= limiter){
			return;
		}
		else{
			Vector2 p4 = new Vector2((p1.x + p3.x)/2.0f, (p1.y+p3.y)/2.0f);
			Vector2 p1a = new Vector2(p4.x, p1.y);
			Vector2 p4a = new Vector2((p4.x+p1a.x)/2.0f, (p4.y+p1a.y)/2.0f);
			
			Vector2 p5 = new Vector2((p2.x + p3.x)/2.0f, (p2.y+p3.y)/2.0f);
			Vector2 p2a = new Vector2(p5.x, p2.y);
			Vector2 p5a = new Vector2((p5.x+p2a.x)/2.0f, (p5.y+p2a.y)/2.0f);
			
			
			//Vector2 p6 = new Vector2((p4.x+p5.x)/2.0f, (p4.y+p5.y)/2.0f);
			this.addSegment((int)p1.x, (int)p1.y, (int)p4a.x, (int)p4a.y);
			this.addSegment((int)p2.x, (int)p2.y, (int)p5a.x, (int)p5a.y);
			addCurveLR(p4a, p5a, p3, ++num, limiter);
		}
	}
	
	public void addCurveUD(Vector2 p1, Vector2 p2, Vector2 p3, int num, int limiter){
		if(num >= limiter){
			return;
		}
		else{
			Vector2 p4 = new Vector2((p1.x + p3.x)/2.0f, (p1.y+p3.y)/2.0f);
			Vector2 p1a = new Vector2(p1.x, p4.y);
			Vector2 p4a = new Vector2((p4.x+p1a.x)/2.0f, (p4.y+p1a.y)/2.0f);
			
			Vector2 p5 = new Vector2((p2.x + p3.x)/2.0f, (p2.y+p3.y)/2.0f);
			Vector2 p2a = new Vector2(p2.x, p5.y);
			Vector2 p5a = new Vector2((p5.x+p2a.x)/2.0f, (p5.y+p2a.y)/2.0f);
			
			
			//Vector2 p6 = new Vector2((p4.x+p5.x)/2.0f, (p4.y+p5.y)/2.0f);
			this.addSegment((int)p1.x, (int)p1.y, (int)p4a.x, (int)p4a.y);
			this.addSegment((int)p2.x, (int)p2.y, (int)p5a.x, (int)p5a.y);
			addCurveLR(p4a, p5a, p3, ++num, limiter);
		}
	}
	
	/**
	 * Applies the current settings of this track to its world.
	 */
	public void apply(){
		if (world == null)
			throw new NullPointerException();
		for (BodyFixture registry : segments){
			Body body = world.createBody(registry.body);
			bodies.add(body);
			body.createFixture(registry.fixture);
			registry.shape.dispose();			
		}
	}
	/**
	 * Removes current track from the world
	 */
	public void destroy()
	{
		if (world == null)
			return;
		for(Body body: bodies){
			world.destroyBody(body);			
		}		
	}
	
	/**
	 * @return the finishLine
	 */
	public Body getFinishLine() {
		return finishLine;
	}


}
