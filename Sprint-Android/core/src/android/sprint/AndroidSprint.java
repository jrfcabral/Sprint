package android.sprint;

import java.io.IOException;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
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
	public static enum ControllerState{
		Main, Connect, Game;
	}
	
	public static enum Color{
		Red{
			public void setColor(){
				Gdx.gl.glClearColor(0.81f, 0, 0, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			}
		}, 
		Blue{
			public void setColor(){
				Gdx.gl.glClearColor(0, 0.52f, 0.81f, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			}
		}, 
		Green{
			public void setColor(){
				Gdx.gl.glClearColor(0.03f, 0.74f, 0.01f, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			}
		}, 
		Pink{
			public void setColor(){
				Gdx.gl.glClearColor(0.91f, 0.51f, 0.76f, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			}
		}, 
		Orange{
			public void setColor(){
				Gdx.gl.glClearColor(1, 0.51f, 0.04f, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			}
		};
		public abstract void setColor();
	}
	
	SpriteBatch batch;
	Texture img;
	TextButton accel, brake, left, right;
	Skin skin;
	Stage stage;
	Socket socket;
	ControllerState st;
	MainMenu main;
	ConnectMenu connect;
	Color bg;
	int state;
	int stateSteer;
	
	@Override
	public void create () {
		st = ControllerState.Main;
		main = new MainMenu();
		connect = new ConnectMenu();
		
		batch = new SpriteBatch();
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		stage = new Stage();
		
		left = new TextButton("<", skin);
		left.setSize(Gdx.graphics.getWidth()*0.2f, Gdx.graphics.getHeight()*0.2f);
		left.setPosition(0,  0);
		
		right = new TextButton(">", skin);
		right.setSize(left.getWidth(), left.getHeight());
		right.setPosition(left.getWidth()*1.1f, 0);
		
		brake = new TextButton("Brake", skin);
		brake.setSize(Gdx.graphics.getWidth()*0.2f, Gdx.graphics.getHeight()*0.2f);
		brake.setPosition(Gdx.graphics.getWidth()-brake.getWidth(), 0);
		
		accel = new TextButton("Accelerate", skin);
		accel.setSize(Gdx.graphics.getWidth()*0.2f, Gdx.graphics.getHeight()*0.2f);
		accel.setPosition(brake.getX(),  brake.getHeight()*1.1f);
		
		
		state = 1;
		stateSteer = 3;
		
		
		stage.addActor(brake);
		stage.addActor(accel);
		stage.addActor(left);
		stage.addActor(right);
		
		//Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render () {
		if(st == ControllerState.Main){
			main.draw(batch);
			if(main.enter.isPressed()){
				st = ControllerState.Connect; 						
				Gdx.input.setInputProcessor(connect.connectMenu);
			}
		}
		else if(st == ControllerState.Connect){
			connect.draw();
			if(connect.getAck()){ 
				st = ControllerState.Game;
				Gdx.input.setInputProcessor(stage);
			}
			if(connect.back.isPressed()){
				st = ControllerState.Main;
				
			}
		}
		else if(st == ControllerState.Game){
			bg = Color.valueOf(connect.getColor());
			bg.setColor();
			update(brake, accel, left, right);
			stage.draw();
			if(Gdx.input.isKeyPressed(Buttons.BACK)){
				sendMessage("LEAVE");
				Gdx.app.exit();
			}
		}
	}
	
	public void pause(){
		if(Gdx.app.getType() ==ApplicationType.Android){
			sendMessage("LEAVE");
			Gdx.app.exit();
		}
		
	}
	
	public void update(TextButton br, TextButton acc, TextButton lft, TextButton rgt){
		if(acc.isPressed()){
			setState(0, stateSteer);
		}
		if(br.isPressed()){
			setState(2, stateSteer);
		}
		if(!acc.isPressed() && !br.isPressed()){
			setState(1, stateSteer);
		}
		if(!lft.isPressed() && !rgt.isPressed()){
			setState(state, 3);
		}
		if(lft.isPressed()){
			setState(state, 4);
		}
		if(rgt.isPressed()){
			setState(state, 5);
		}
	}
	
	public void setState(int st, int stst){
		if(state == st && stateSteer == stst)
			return;
		else{
			if(state != st){
				state = st;
				if(state == 1){
					sendMessage("NOP");
				}
				else if(state == 0){
					sendMessage("THROTTLE");
				}
				else if(state == 2){
					sendMessage("BRAKE");
				}
			}
			if(stateSteer != stst){
				stateSteer = stst;
				if(stateSteer == 3){
					
					sendMessage("NOSTEER");
				}
				else if(stateSteer == 4){
				
					sendMessage("STEER_LEFT");
				}
				else if(stateSteer == 5){
					
					sendMessage("STEER_RIGHT");
				}
			}
		}
	}
	private void sendMessage(String msg){
		try {
			socket = new Socket(connect.getIp(), connect.getPort());
			socket.getOutputStream().write(msg.getBytes());
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
