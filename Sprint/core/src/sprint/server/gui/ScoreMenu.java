package sprint.server.gui;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import sprint.server.logic.State;
import sprint.server.logic.StateMachine;
import sprint.server.net.Lobby;

public class ScoreMenu implements State {

	private Stage scoreMenu;
	private Skin scoreSkin;
	private TextArea scores;
	private TextArea scoreBanner;
	private TextButton nextButton;
	private LinkedList<String> positionList;
	private String position;
	
	private Lobby lobby;
	private StateMachine stateMachine;
	
	public ScoreMenu(LinkedList<String> positions, Lobby lobby, StateMachine stateMachine){
		positionList = positions;
		this.lobby = lobby;
		this.stateMachine = stateMachine;
		create();
	}
	@Override
	public void update() {
		

	}

	@Override
	public void create() {
		position = new String();
		generateScores();
		scoreMenu = new Stage();
		scoreSkin = new Skin(Gdx.files.internal("data/uiskin.json"));	
		scores = new TextArea(position, scoreSkin);		
		scoreBanner = new TextArea("SCORES:", scoreSkin);
		scoreBanner.setAlignment(1);
		
		scoreBanner.setSize(Gdx.graphics.getWidth()*0.6f, Gdx.graphics.getHeight()*0.3f);
		scoreBanner.setPosition(Gdx.graphics.getWidth()/2.0f-scoreBanner.getWidth()/2.0f, Gdx.graphics.getHeight()-scoreBanner.getHeight());
		scoreMenu.addActor(scoreBanner);
		
		scores.setSize(Gdx.graphics.getWidth()*0.4f, Gdx.graphics.getHeight()*0.25f);
		scores.setPosition(Gdx.graphics.getWidth()/2.0f - scores.getWidth()/2.0f, Gdx.graphics.getHeight()/2.0f - scores.getHeight()/1.7f);
		scoreMenu.addActor(scores);
		
		nextButton = new TextButton("Continue", scoreSkin);
		nextButton.addListener(new ClickListener(-1){
			@Override
			public void clicked(InputEvent event , float x, float y){
				ScoreMenu.this.stateMachine.getServer().launchServer();
				ScoreMenu.this.stateMachine.setState(new LobbyMenu(ScoreMenu.this.stateMachine.getLobby(), ScoreMenu.this.stateMachine));
			}
		});
		

		nextButton.setSize(Gdx.graphics.getWidth()*0.45f, Gdx.graphics.getHeight()*0.08f);
		nextButton.setPosition(Gdx.graphics.getWidth()/2.0f - nextButton.getWidth()/2.0f, nextButton.getY() - 100.0f);
		scoreMenu.addActor(nextButton);
		
	}

	@Override
	public void draw() {
		Gdx.gl.glClearColor(0.66f, 0.66f, 0.66f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		scoreMenu.draw();
	}

	@Override
	public void resize(int width, int height) {
		scoreMenu.getViewport().update(width, height, true);

	}
	
	private void generateScores() {
		int i = 1;
		for(String color:positionList){
			this.position += i + ") " + positionList.get(i-1) + "\n";
			i++;
		}
	}


}
