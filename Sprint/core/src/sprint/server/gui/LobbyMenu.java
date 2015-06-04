package sprint.server.gui;

import java.net.InetAddress;
import java.net.UnknownHostException;

import sprint.server.net.Lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;

public class LobbyMenu {
	private Stage lobbyMenu;
	private Skin lobbySkin;
	private Sprite lobbyLogo;
	private TextArea ip;
	private TextArea playerCounter;
	private Lobby lobby;
	
	public int getLobbyElapsed(){
		return lobby.getElapsed();
	}
	
	public LobbyMenu(Lobby lobby){
		lobbyMenu=new Stage();
		lobbySkin = new Skin(Gdx.files.internal("data/uiskin.json"));
		lobbyLogo = new Sprite(new Texture("SuperSprintlogo.png"));
		try {
			ip = new TextArea(InetAddress.getLocalHost().toString(),lobbySkin);
		} catch (UnknownHostException e) {
			ip = new TextArea("Nenhum IP", lobbySkin);			
		}
		this.lobby = lobby;
		playerCounter = new TextArea("Players: \n" + Integer.toString( lobby.getQueueSize())  + "/" + Lobby.MAX_PLAYERS, lobbySkin);
		playerCounter.setSize(Gdx.graphics.getWidth()*0.3f, Gdx.graphics.getHeight()*0.2f);
		playerCounter.setPosition(Gdx.graphics.getWidth()/2.0f + playerCounter.getWidth()*1.0f, Gdx.graphics.getHeight()-Gdx.graphics.getHeight()*0.5f);
		
		lobbyLogo.setSize(Gdx.graphics.getWidth()*0.6f, Gdx.graphics.getHeight()*0.3f);
		lobbyLogo.setPosition(Gdx.graphics.getWidth()/2.0f-lobbyLogo.getWidth()/2.0f, Gdx.graphics.getHeight()-lobbyLogo.getHeight());
		ip.setSize(Gdx.graphics.getWidth()*0.3f, Gdx.graphics.getHeight()*0.2f);
		ip.setPosition(Gdx.graphics.getWidth()/1.4f-Gdx.graphics.getWidth()/1.4f, Gdx.graphics.getHeight()-Gdx.graphics.getHeight()*0.5f);
		
		lobbyMenu.addActor(ip);
		lobbyMenu.addActor(playerCounter);
				
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
	
}
