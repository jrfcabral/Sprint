package sprint.server.logic;

import sprint.server.gui.LobbyMenu;
import sprint.server.gui.MainMenu;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;

/**
 * Represents the game application by itself. Holds the state machine and performs the main update and draw cycle on it. 
 */
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

	/**
	 * Creates the state machine for the game and initializes it.
	 */
	@Override
	public void create () {

		stateMachine = new StateMachine();
		state = GameState.Main;		
		pcControls = true;		
		main = new MainMenu(stateMachine);		
	}

	/**
	 * Asks the current state, via the state machine, to render itself on the screen and then to update itself.
	 */
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
	
	/**
	 * Asks the current state to perform the appropriate actions when the user adjusts window size
	 */
	public void resize(int width, int height){
		main.resize(width, height);
	}
	
	
	
}
