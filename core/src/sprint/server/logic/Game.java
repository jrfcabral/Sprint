package sprint.server.logic;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.graphics.OrthographicCamera;

import sprint.server.logic.*;
import utils.CameraManager;
import utils.Settings;


public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	World world;
	OrthographicCamera camera;
	Box2DDebugRenderer debugRenderer;
	Body body;
	Car car;
	CameraManager camManager;
	@Override
	public void create () {
		world  = new World(new Vector2(0,0), true);
		debugRenderer = new Box2DDebugRenderer();
		camera = new OrthographicCamera(Settings.VIEWPORT_WIDTH, Settings.VIEWPORT_HEIGHT);
		camera.position.set(0, 0, 0);
		camera.update();
		batch = new SpriteBatch();
		car = new Car(world);
		BodyDef def = new BodyDef();
		def.type = BodyDef.BodyType.StaticBody;
		def.position.set(new Vector2(-2,-2));
		Body body = world.createBody(def);
		FixtureDef fixd = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(2, 2);
		fixd.shape = shape;		
		fixd.density = 1;
		fixd.restitution = 1f;
		fixd.friction = 1f;
		body.createFixture(fixd);		
		shape.dispose();
		
		camManager = new CameraManager();
	}

	@Override
	public void render () {		
		camManager.update(Gdx.graphics.getDeltaTime());
		camManager.applyTo(camera);
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);		
		batch.begin();
		//	car.getSprite().draw(batch);
		batch.end();
		debugRenderer.render(world, camera.combined);		
		
		handleInput(Gdx.graphics.getDeltaTime());
		world.step(1/60f, 6, 2);
		
		
	}
	
	
	public void handleInput(float deltaTime){
		boolean throttle = Gdx.input.isKeyPressed(Input.Keys.W);
		boolean brake = Gdx.input.isKeyPressed(Input.Keys.S);
		if (Gdx.input.isKeyPressed(Input.Keys.A))
			car.update(throttle,brake, Car.SteerDirection.SteerLeft);
		else if (Gdx.input.isKeyPressed(Input.Keys.D))
			car.update(throttle,brake, Car.SteerDirection.SteerRight);
		else
			car.update(throttle,brake, Car.SteerDirection.SteerNone);
		
		//Camera controls
		//Movement
		if(Gdx.input.isKeyPressed(Keys.UP)){
			camManager.setPosition(camManager.getPosition().x, camManager.getPosition().y + Settings.CAMERA_MOVE_SPEED * deltaTime);
		}
		if(Gdx.input.isKeyPressed(Keys.DOWN)){
			camManager.setPosition(camManager.getPosition().x, camManager.getPosition().y - Settings.CAMERA_MOVE_SPEED * deltaTime);
		}
		if(Gdx.input.isKeyPressed(Keys.LEFT)){
			camManager.setPosition(camManager.getPosition().x - Settings.CAMERA_MOVE_SPEED * deltaTime, camManager.getPosition().y);
		}
		if(Gdx.input.isKeyPressed(Keys.RIGHT)){
			camManager.setPosition(camManager.getPosition().x + Settings.CAMERA_MOVE_SPEED * deltaTime, camManager.getPosition().y);
		}
		//Zoom
		if(Gdx.input.isKeyPressed(Keys.PLUS)){
			camManager.addZoom(Settings.CAMERA_ZOOM_SPEED);
		}
		if(Gdx.input.isKeyPressed(Keys.MINUS)){
			camManager.addZoom(-Settings.CAMERA_ZOOM_SPEED);
		}
		
		
	}
	
	public static void main(){
		Game game = new Game();		
	}
	
}
