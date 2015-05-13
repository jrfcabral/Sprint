package android.sprint;

import java.io.IOException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.net.Socket;
import java.net.UnknownHostException;

public class AndroidSprint extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	TextButton button;
	Skin skin;
	Stage stage;
	Socket socket;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		stage = new Stage();
		button = new TextButton("Button", skin);
		button.setSize(80.0f, 80.0f);
		button.setPosition(Gdx.graphics.getWidth()/2.0f, Gdx.graphics.getHeight()/2.0f);
		
		  button.addListener(new ClickListener(){
	            @Override 
	            public void clicked(InputEvent event, float x, float y){
	            	//System.out.println("Top");
					try {
						socket = new Socket("192.168.1.6", 8888);
						socket.getOutputStream().write("Test".getBytes());
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					finally{
		                if(socket != null){
		                    try{
		                        socket.close();
		                    }
		                    catch(IOException e){
		                        e.printStackTrace();
		                    }
		                }
					}
	            }
	        });
		
		stage.addActor(button);
		
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
			stage.draw();
		batch.end();
	}
}
