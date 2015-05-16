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
	TextButton accel, brake;
	Skin skin;
	Stage stage;
	Socket socket;
	int state;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		stage = new Stage();
		brake = new TextButton("Brake", skin);
		brake.setSize(80.0f, 80.0f);
		brake.setPosition(Gdx.graphics.getWidth()-brake.getWidth(), 0);
		
		accel = new TextButton("Accelerate", skin);
		accel.setSize(120f, 80f);
		accel.setPosition(0,  0);
		
		
		state = 1;
		  accel.addListener(new ClickListener(-1){
	            @Override 
	            public void clicked(InputEvent event, float x, float y){
	            	setState(0);
	            }
	        });
		  
		  brake.addListener(new ClickListener(-1){
	            @Override 
	            public void clicked(InputEvent event, float x, float y){
	            	setState(2);
	            }
	        });
		
		stage.addActor(brake);
		stage.addActor(accel);
		
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		update(socket, brake, accel);
		batch.begin();
			stage.draw();
		batch.end();
	}
	
	public void update(Socket sock, TextButton br, TextButton acc){
		if(acc.isPressed()){
			setState(0);
		}
		if(br.isPressed()){
			setState(2);
		}
		if(!acc.isPressed() && !br.isPressed()){
			setState(1);
		}
	}
	
	public void setState(int st){
		if(state == st)
			return;
		else{
			state = st;
			if(state == 1){
				try {
					socket = new Socket("192.168.1.6", 8888);
					socket.getOutputStream().write("Nop".getBytes());
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
			else if(state == 0){
				try {
					socket = new Socket("192.168.1.6", 8888);
					socket.getOutputStream().write("Accelerate".getBytes());
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
			else if(state == 2){
				try {
					socket = new Socket("192.168.1.6", 8888);
					socket.getOutputStream().write("Travate".getBytes());
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
		}
	}
	
}
