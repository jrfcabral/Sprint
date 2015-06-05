package sprint.server.logic;

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
		//race = new Race();
		stateMachine = new StateMachine();
		state = GameState.Main;		
		
		pcControls = true;		
		main = new MainMenu(stateMachine);
		//lobbyMenu = new LobbyMenu(lobby, stateMachine);		
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stateMachine.update();
		stateMachine.draw();
		/*if(state == GameState.Main){
			
			main.draw();
			if(main.isStartServer()){
				lobby.startTimer();
				state = GameState.Lobby;				
					server.launchServer();
			}
		}
		else if(state == GameState.Lobby){
			lobbyMenu.draw();
			if (lobby.getElapsed() == 5){
				this.state = GameState.InGame;
				lobby.stopTimer();
				race = new Race();
				this.startGame(lobby.getIdentifiers());
			}
		}
		else if(state == GameState.InGame){
			if(race.getEnded()){
				state = GameState.Lobby;
				lobby.startTimer();
			}
			race.draw();
		}*/
	}
	
	
	
	
	public void handleInput(float deltaTime){			
		if (state ==GameState.InGame)
			if (Gdx.input.isKeyPressed(Keys.ESCAPE)){
				state = GameState.Lobby;
				//lobby.startTimer();
			}
	}
	
	
	
}
