package sprint.server.gui;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

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

/**
* Represents the Score Menu that is show to players when the race just ended 
*/
public class ScoreMenu implements State {

	private Stage scoreMenu;
	private Skin scoreSkin;
	private TextArea scores;
	private TextArea scoreBanner;
	private TextButton nextButton;
	private LinkedList<String> positionList;
	private String position;
	private final static int TIMEOUT = 10;
	private int timer = TIMEOUT;
	private Timer clock;
	
	private Lobby lobby;
	private StateMachine stateMachine;
	
	public ScoreMenu(LinkedList<String> positions, Lobby lobby, StateMachine stateMachine){
		positionList = positions;
		this.lobby = lobby;
		this.stateMachine = stateMachine;
		create();
	}
	
	/**
	 * Polls for input and updates the internal state of the menu
	 * May trigger a state change 
	 */
	@Override
	public void update() {
		if (timer < 0)
			this.stateMachine.setState(new LobbyMenu(this.lobby,this.stateMachine));	}

	/**
	 * Generates all the components of the Score Menu and sets listeners for input
	 */
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
				ScoreMenu.this.stateMachine.setState(new LobbyMenu(ScoreMenu.this.stateMachine.getLobby(), ScoreMenu.this.stateMachine));
				ScoreMenu.this.clock.cancel();
			}
		});
		

		nextButton.setSize(Gdx.graphics.getWidth()*0.45f, Gdx.graphics.getHeight()*0.08f);
		nextButton.setPosition(Gdx.graphics.getWidth()/2.0f - nextButton.getWidth()/2.0f, nextButton.getHeight()/2.0f);
		scoreMenu.addActor(nextButton);
		Gdx.input.setInputProcessor(scoreMenu);
		
		clock = new Timer();
		clock.scheduleAtFixedRate(new TimerUpdate(), 0, 1000);		
	}

	/**
	 * Paints the components of the Score Menu on the screen.
	 */
	@Override
	public void draw() {
		Gdx.gl.glClearColor(0.66f, 0.66f, 0.66f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		scoreMenu.draw();
	}

	/**
	 * Prepares the Score Menu for user resizing the screen
	 */
	@Override
	public void resize(int width, int height) {
		scoreMenu.getViewport().update(width, height, true);

	}
	
		
	private void generateScores() {
		int i = 1;
		for(String color:positionList){
			this.position += i + ") " + color + "\n";
			i++;
		}
	}
	
	private class TimerUpdate extends TimerTask{
		@Override
		public synchronized void run() {
			ScoreMenu.this.timer--;
			//System.out.println(Lobby.this.elapsed);
		}
		
	}


}
