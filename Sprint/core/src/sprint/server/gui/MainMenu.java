package sprint.server.gui;

import sprint.server.logic.State;
import sprint.server.logic.StateMachine;

import com.badlogic.gdx.Gdx;
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
	private Sprite logo;
	private final StateMachine stateMachine;
	
	public MainMenu(StateMachine stateMachine){
		create();		
		this.stateMachine = stateMachine;
	}
	
	public void draw(){
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
		logo = new Sprite(new Texture("SuperSprintlogo.png"));
		
		startServer.setSize(280.0f, 40.0f);
		startServer.setPosition(Gdx.graphics.getWidth()/2.0f - startServer.getWidth()/2.0f, Gdx.graphics.getHeight()-250.0f);
		startServer.addListener(new ClickListener(-1){
			@Override
			public void clicked(InputEvent event , float x, float y){
				MainMenu.this.stateMachine.getServer().launchServer();
				MainMenu.this.stateMachine.setState(new LobbyMenu(MainMenu.this.stateMachine.getLobby(), MainMenu.this.stateMachine));
			}
		});
		
		
		exit.setSize(280.0f, 40.0f);
		exit.setPosition(Gdx.graphics.getWidth()/2.0f - exit.getWidth()/2.0f, startServer.getY() - 100.0f);
		exit.addListener(new ClickListener(-1){
			@Override
			public void clicked(InputEvent event , float x, float y){
				Gdx.app.exit();
			}
		});
		
		logo.setSize(440.0f, 100.0f);
		logo.setPosition(Gdx.graphics.getWidth()/2.0f - logo.getWidth()/2.0f, Gdx.graphics.getHeight() - 30.0f - logo.getHeight());
		
		mainMenu.addActor(startServer);
		mainMenu.addActor(exit);
		
		Gdx.input.setInputProcessor(mainMenu);
		
	}

	@Override
	public void resize(int width, int height) {
		mainMenu.getViewport().update(width, height, true);
	}
	
}
