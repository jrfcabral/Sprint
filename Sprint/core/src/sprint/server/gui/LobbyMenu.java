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
	private TextArea ip;
	private TextArea playerCounter;
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
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		lobbyMenu.getBatch().begin();
		lobbyLogo.draw(lobbyMenu.getBatch());
		lobbyMenu.getBatch().end();
		lobbyMenu.draw();
		playerCounter.setText("Players: \n" + Integer.toString( lobby.getQueueSize())  + "/" + Lobby.MAX_PLAYERS);
		
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
		try {
			ip = new TextArea(InetAddress.getLocalHost().toString(),lobbySkin);
		} catch (UnknownHostException e) {
			ip = new TextArea("Nenhum IP", lobbySkin);			
		}
		if (lobby == null)System.out.println("erro");
		playerCounter = new TextArea("Players: \n" + Integer.toString( lobby.getQueueSize())  + "/" + Lobby.MAX_PLAYERS, lobbySkin);
		playerCounter.setSize(Gdx.graphics.getWidth()*0.3f, Gdx.graphics.getHeight()*0.2f);
		playerCounter.setPosition(Gdx.graphics.getWidth()/2.0f + playerCounter.getWidth()*1.0f, Gdx.graphics.getHeight()-Gdx.graphics.getHeight()*0.5f);
		
		lobbyLogo.setSize(Gdx.graphics.getWidth()*0.6f, Gdx.graphics.getHeight()*0.3f);
		lobbyLogo.setPosition(Gdx.graphics.getWidth()/2.0f-lobbyLogo.getWidth()/2.0f, Gdx.graphics.getHeight()-lobbyLogo.getHeight());
		ip.setSize(Gdx.graphics.getWidth()*0.3f, Gdx.graphics.getHeight()*0.2f);
		ip.setPosition(Gdx.graphics.getWidth()/1.4f-Gdx.graphics.getWidth()/1.4f, Gdx.graphics.getHeight()-Gdx.graphics.getHeight()*0.5f);
		
		lobbyMenu.addActor(ip);
		lobbyMenu.addActor(playerCounter);
		Gdx.input.setInputProcessor(lobbyMenu);
		lobby.startTimer();
		
	}

	@Override
	public void resize(int width, int height) {
		
	}
	
}
