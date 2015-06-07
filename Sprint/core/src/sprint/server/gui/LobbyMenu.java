package sprint.server.gui;

import java.net.InetAddress;
import java.net.UnknownHostException;

import sprint.server.logic.Race;
import sprint.server.logic.State;
import sprint.server.logic.StateMachine;
import sprint.server.net.Lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;

public class LobbyMenu implements State {
	private Stage lobbyMenu;
	private Skin lobbySkin;
	private Sprite lobbyLogo;
	private TextArea info;
	private Lobby lobby;
	private final StateMachine state;
	
	public int getLobbyElapsed(){
		return lobby.getElapsed();
	}
	
	public LobbyMenu(Lobby lobby, StateMachine state){
		this.state = state;
		this.lobby = lobby;
		create();					
	}
	
	public void draw(){
		Gdx.gl.glClearColor(0.66f, 0.66f, 0.66f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		lobbyMenu.getBatch().begin();
		lobbyLogo.draw(lobbyMenu.getBatch());
		lobbyMenu.getBatch().end();
		lobbyMenu.draw();
		String ip;
		try {
			ip = "Server IP: " + InetAddress.getLocalHost().getHostAddress().toString();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			ip = "Server IP: N/A";
		}
		String players = "Players: " + Integer.toString(lobby.getQueueSize()) + "/" + Lobby.MAX_PLAYERS;
		String startTime;
		if(lobby.getQueueSize() > 0){
			startTime = "Time to start: " +  (lobby.READY_TIMER - lobby.getElapsed());
		}
		else{
			startTime = "Time to start: N/A";
		}
		info.setText(ip + "\n\n" + players + "\n\n" + startTime);
		
	}

	@Override
	public void update() {
		if (lobby.getElapsed() > Lobby.READY_TIMER)
		{
			System.out.println("Vou mudar");
			this.lobby.stopTimer();
			Race race = new Race(state, lobby);
			race.startGame(this.lobby.getIdentifiers());
			this.state.setState(race);			
		}
		
	}

	@Override
	public void create() {
		lobbyMenu=new Stage();
		lobbySkin = new Skin(Gdx.files.internal("data/uiskin.json"));
		lobbyLogo = new Sprite(new Texture("SuperSprintlogo.png"));
				
		lobbyLogo.setSize(Gdx.graphics.getWidth()*0.6f, Gdx.graphics.getHeight()*0.3f);
		lobbyLogo.setPosition(Gdx.graphics.getWidth()/2.0f-lobbyLogo.getWidth()/2.0f, Gdx.graphics.getHeight()-lobbyLogo.getHeight());
		
		String ip;
		try {
			ip = "Server IP: " + InetAddress.getLocalHost().getHostAddress().toString();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			ip = "Server IP: N/A";
		}
		String players = "Players: " + Integer.toString(lobby.getQueueSize()) + "/" + Lobby.MAX_PLAYERS;
		String startTime;
		if(lobby.getQueueSize() > 0){
			startTime = "Time to start: " +  (lobby.READY_TIMER - lobby.getElapsed());
		}
		else{
			startTime = "Time to start: N/A";
		}
		
		info = new TextArea(ip + "\n\n" + players + "\n\n" + startTime, lobbySkin);
		info.setSize(Gdx.graphics.getWidth()*0.4f, Gdx.graphics.getHeight()*0.25f);
		info.setPosition(Gdx.graphics.getWidth()/2.0f - info.getWidth()/2.0f, Gdx.graphics.getHeight()/2.0f - info.getHeight()/1.7f);
		//lobbyMenu.addActor(ip);
		//lobbyMenu.addActor(playerCounter);
		lobbyMenu.addActor(info);
		Gdx.input.setInputProcessor(lobbyMenu);
		lobby.startTimer();
		
	}

	@Override
	public void resize(int width, int height) {
		
	}
	
}
