package sprint.server.logic;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.graphics.OrthographicCamera;
import sprint.server.logic.*;
public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	World world;
	OrthographicCamera camera;
	Box2DDebugRenderer debugRenderer;
	Body body;
	Car car;
	@Override
	public void create () {
		
		world  = new World(new Vector2(0,0), true);
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(30,30);
		camera.setToOrtho(true);
	
	
		car = new Car(world);
	}

	@Override
	public void render () {		
		if (Gdx.input.isKeyPressed(Input.Keys.A))
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);		
		
		debugRenderer.render(world, camera.combined);		
		world.step(1/45f, 6, 2);
		boolean throttle = Gdx.input.isKeyPressed(Input.Keys.W);
		if (Gdx.input.isKeyPressed(Input.Keys.A))
			car.update(throttle, Car.SteerDirection.SteerLeft);
		else if (Gdx.input.isKeyPressed(Input.Keys.D))
			car.update(throttle, Car.SteerDirection.SteerRight);
		else
			car.update(throttle, Car.SteerDirection.SteerNone);
	}
	public static void main(){
		Game game = new Game();		
	}
	
}
