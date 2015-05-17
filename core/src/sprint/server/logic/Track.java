package sprint.server.logic;

import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Track {
	
	private HashSet<Body> bodies;
	private ArrayList<BodyFixture> segments;
	private World world;
	
	private static final float BOUNDARY_RESTITUTION = 1.0f;
	
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
}
