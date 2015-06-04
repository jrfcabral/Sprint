package sprint.server.logic;

import java.util.LinkedList;

import sprint.server.gui.LobbyMenu;
import sprint.server.gui.MainMenu;
import sprint.server.net.Lobby;
import sprint.server.net.PlayerControls;
import sprint.server.net.Server;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;


public class Game extends ApplicationAdapter {
	public static enum GameState{
		Main, Lobby, InGame
	}
	Thread serverThread;
	Server server;
	Lobby lobby;
	GameState state;
	Race race;	
	
	
	
	MainMenu main;
	LobbyMenu lobbyMenu;
	
	boolean pcControls;

	@Override
	public void create () {
		//race = new Race();
		state = GameState.Main;		
		lobby = new Lobby();
		server = new Server(this, lobby);
		pcControls = true;		
		main = new MainMenu();
		lobbyMenu = new LobbyMenu(lobby);		
	}

	@Override
	public void render () {	
		if(state == GameState.Main){
			Gdx.gl.glClearColor(1, 1, 1, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
			race.drawGame(Gdx.graphics.getDeltaTime());
		}
	}
	
	private void startGame(LinkedList<String> identifiers) {
		for (String id : identifiers){
			PlayerControls controls = new PlayerControls(id, server);
			Car car = new Car(this.race, controls);
			race.addCar(car);
		}		
	}
	
	
	public void handleInput(float deltaTime){			
		if (state ==GameState.InGame)
			if (Gdx.input.isKeyPressed(Keys.ESCAPE)){
				state = GameState.Lobby;
				lobby.startTimer();
			}
	}
	
	
	
}
