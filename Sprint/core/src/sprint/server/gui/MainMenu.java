package sprint.server.gui;

import sprint.server.logic.State;
import sprint.server.logic.StateMachine;
import sprint.tests.Tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenu implements State {
	private Stage mainMenu;
	private Skin skin;
	private TextButton startServer;
	private TextButton exit;
	private TextButton tests;
	private Sprite logo;
	private final StateMachine stateMachine;
	
	public MainMenu(StateMachine stateMachine){
		create();		
		this.stateMachine = stateMachine;
	}
	
	public void draw(){
		Gdx.gl.glClearColor(0.66f, 0.66f, 0.66f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mainMenu.getBatch().begin();
		logo.draw(mainMenu.getBatch());
		mainMenu.getBatch().end();
		mainMenu.draw();
	}
	
	public boolean isStartServer(){
		return startServer.isPressed();
	}

	@Override
	public void update() {		
		if (exit.isPressed())
			Gdx.app.exit();
		else if (startServer.isPressed())
			stateMachine.setState(new MainMenu(stateMachine));
	}

	@Override
	public void create() {
	 	mainMenu = new Stage();
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		startServer = new TextButton("Start Server", skin);		
		exit = new TextButton("Exit", skin);
		tests = new TextButton("Tests", skin);
		logo = new Sprite(new Texture("SuperSprintlogo.png"));
		
		logo.setSize(Gdx.graphics.getWidth()*0.6f, Gdx.graphics.getHeight()*0.3f);
		logo.setPosition(Gdx.graphics.getWidth()/2.0f-logo.getWidth()/2.0f, Gdx.graphics.getHeight()-logo.getHeight());
		
		startServer.setSize(Gdx.graphics.getWidth()*0.45f, Gdx.graphics.getHeight()*0.08f);
		startServer.setPosition(Gdx.graphics.getWidth()/2.0f - startServer.getWidth()/2.0f, logo.getY() - 100f);
		startServer.addListener(new ClickListener(-1){
			@Override
			public void clicked(InputEvent event , float x, float y){
				MainMenu.this.stateMachine.getServer().launchServer();
				MainMenu.this.stateMachine.setState(new LobbyMenu(MainMenu.this.stateMachine.getLobby(), MainMenu.this.stateMachine));
			}
		});
		
		tests.setSize(Gdx.graphics.getWidth()*0.45f, Gdx.graphics.getHeight()*0.08f);
		tests.setPosition(Gdx.graphics.getWidth()/2.0f - tests.getWidth()/2.0f, startServer.getY() - 100.0f);
		tests.addListener(new ClickListener(-1){
			public void clicked(InputEvent event, float x, float y){
				MainMenu.this.stateMachine.setState(new Tests(stateMachine));
			}
		});
		exit.setSize(Gdx.graphics.getWidth()*0.45f, Gdx.graphics.getHeight()*0.08f);
		exit.setPosition(Gdx.graphics.getWidth()/2.0f - exit.getWidth()/2.0f, tests.getY() - 100.0f);
		exit.addListener(new ClickListener(-1){
			@Override
			public void clicked(InputEvent event , float x, float y){
				Gdx.app.exit();
			}
		});
		
		
		mainMenu.addActor(startServer);
		mainMenu.addActor(tests);
		mainMenu.addActor(exit);
		
		Gdx.input.setInputProcessor(mainMenu);
		
	}

	@Override
	public void resize(int width, int height) {
		mainMenu.getViewport().update(width, height, true);
	}
	
}
