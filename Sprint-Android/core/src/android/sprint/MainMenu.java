package android.sprint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenu {
	Stage mainMenu;
	Skin skin;
	TextButton enter;
	TextButton exit;
	Sprite logo;
	
	public MainMenu(){
		mainMenu = new Stage();
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		enter = new TextButton("Enter Game", skin);
		exit = new TextButton("Exit", skin);
		logo = new Sprite(new Texture("SuperSprintlogo.png"));
		
		logo.setSize(Gdx.graphics.getWidth()*0.8f, Gdx.graphics.getHeight()*0.3f);
		logo.setPosition(Gdx.graphics.getWidth()/2.0f - logo.getWidth()/2.0f, Gdx.graphics.getHeight() - 30.0f - logo.getHeight());
		
		enter.setSize(Gdx.graphics.getWidth()*0.6f, Gdx.graphics.getHeight()*0.1f);
		enter.setPosition(Gdx.graphics.getWidth()/2.0f - enter.getWidth()/2.0f, Gdx.graphics.getHeight()- logo.getHeight()*2.0f);
		
		exit.setSize(Gdx.graphics.getWidth()*0.6f, Gdx.graphics.getHeight()*0.1f);
		exit.setPosition(Gdx.graphics.getWidth()/2.0f - exit.getWidth()/2.0f, enter.getY() - enter.getHeight()*2.0f);
		exit.addListener(new ClickListener(-1){
			@Override
			public void clicked(InputEvent event , float x, float y){
				Gdx.app.exit();
			}
		});
		
		
		
		mainMenu.addActor(enter);
		mainMenu.addActor(exit);
		
		Gdx.input.setInputProcessor(mainMenu);
		
	}
	
	public void draw(SpriteBatch batch){
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mainMenu.getBatch().begin();
		logo.draw(mainMenu.getBatch());
		mainMenu.getBatch().end();
		mainMenu.draw();
		
	}
	
	
}

