package sprint.server.logic;

import java.util.LinkedList;

import sprint.server.gui.LobbyMenu;
import sprint.server.gui.MainMenu;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;


public class Game extends ApplicationAdapter {
	public static enum GameState{
		Main, Lobby, InGame
	}
	
	
	GameState state;
	Race race;	
	StateMachine stateMachine;
	MainMenu main;
	LobbyMenu lobbyMenu;	
	boolean pcControls;

	@Override
	public void create () {

		stateMachine = new StateMachine();
		state = GameState.Main;		
		pcControls = true;		
		main = new MainMenu(stateMachine);		
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stateMachine.update();
		stateMachine.draw();		
	}
	

	
	
	public void handleInput(float deltaTime){			
		if (state ==GameState.InGame)
			if (Gdx.input.isKeyPressed(Keys.ESCAPE)){
				stateMachine.setState(race);
			}
	}
	
	public void resize(int width, int height){
		main.resize(width, height);
		//lobbyMenu.resize(width, height);
	}
	
	
	
}
