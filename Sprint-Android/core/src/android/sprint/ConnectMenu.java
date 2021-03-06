package android.sprint;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ConnectMenu {
	TextArea ip;
	TextArea port;
	TextButton connect;
	TextButton back;
	Stage connectMenu;
	Skin skin;
	Socket testSocket;
	DataInputStream dis;
	BufferedReader reader;
	String color;
	boolean ack;
	boolean ok;
	
	public ConnectMenu(){
		ack = false;
		ok = true;
		connectMenu = new Stage();
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		
		ip = new TextArea("192.168.1.10", skin);
		ip.setSize(Gdx.graphics.getWidth()*0.6f, Gdx.graphics.getHeight()*0.1f);
		ip.setPosition(Gdx.graphics.getWidth()/2.0f - ip.getWidth()/2.0f, Gdx.graphics.getHeight() - ip.getHeight()*4.0f);
		
		port = new TextArea("8888", skin);
		port.setSize(Gdx.graphics.getWidth()*0.6f, Gdx.graphics.getHeight()*0.1f);
		port.setPosition(Gdx.graphics.getWidth()/2.0f - ip.getWidth()/2.0f, ip.getY() - port.getHeight()*1.2f);
		
		connect = new TextButton("Connect", skin);		
		connect.setSize(ip.getWidth()*0.3f, ip.getHeight()*1.3f);
		connect.setPosition(port.getX(), port.getY() - connect.getHeight()*1.4f);
		connect.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event , float x, float y){
				try {
					testSocket = new Socket(getIp(), getPort());
					reader = new BufferedReader(new InputStreamReader(testSocket.getInputStream()));
					String test = "TEST\n";
					testSocket.getOutputStream().write(test.getBytes());
					testSocket.getOutputStream().flush();					
					
				} catch (Exception e) {
					ip.setText("Server not found at the address");
					ok = false;
				}
				
				try{	
					if(ok){
						String response = reader.readLine();
						String[] tokens = response.split(" ");
						
						
						if(tokens[0].equals("Received")){
							 if(!tokens[1].equals("Full")){
								 color = tokens[1];
								 ack = true;
							 }
							 else{
								 ip.setText("The room is full. Try again in a few minutes");
							 }
						}
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				finally{
	                if(testSocket != null){
	                    try{
	                        testSocket.close();
	                    }
	                    catch(IOException e){
	                        e.printStackTrace();
	                    }
	                }	     
				}
			}
		});
		
		
		back = new TextButton("Back", skin);
		back.setSize(ip.getWidth()*0.3f, ip.getHeight()*1.3f);
		back.setPosition(connect.getX() + connect.getWidth() + connect.getHeight()*1.4f, connect.getY());
		
		connectMenu.addActor(ip);
		connectMenu.addActor(port);
		connectMenu.addActor(connect);
		//connectMenu.addActor(back);
	}
	
	public void draw(){
		Gdx.gl.glClearColor(0.66f, 0.66f, 0.66f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		connectMenu.draw();
	}
	
	public String getIp(){
		return ip.getText();
	}
	
	public int getPort(){
		return Integer.parseInt(port.getText());
	}
	
	public boolean getAck(){
		return ack;
	}
	
	public String getColor(){
		return color;
	}
	
}
