package android.sprint;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import com.badlogic.gdx.Gdx;
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
	boolean ack;
	
	public ConnectMenu(){
		ack = false;
		connectMenu = new Stage();
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		
		ip = new TextArea("Server Ip", skin);
		ip.setSize(300.0f, 30.0f);
		ip.setPosition(Gdx.graphics.getWidth()/2.0f - ip.getWidth()/2.0f, Gdx.graphics.getHeight() - ip.getHeight() - 10.0f);
		
		port = new TextArea("Port", skin);
		port.setSize(300.0f, 30.0f);
		port.setPosition(Gdx.graphics.getWidth()/2.0f - ip.getWidth()/2.0f, ip.getY() - port.getHeight() - 10.0f);
		
		connect = new TextButton("Connect", skin);
		connect.setSize(100.0f, 50.0f);
		connect.setPosition(port.getX(), port.getY() - connect.getHeight() - 20.0f);
		connect.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event , float x, float y){
				try {
					testSocket = new Socket(getIp(), getPort());
					 BufferedReader reader = new BufferedReader(new InputStreamReader(testSocket.getInputStream()));
					testSocket.getOutputStream().write("Test".getBytes());
					
					//String response = reader.readLine();
					//System.out.println(response);
					
					 /*if(response.equals("Received")){
						 ack = true;
					 }*/
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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
	                
	                if(dis != null){
	                	try{
	                        dis.close();
	                    }
	                    catch(IOException e){
	                        e.printStackTrace();
	                    }
	                }
				}
			}
		});
		
		
		back = new TextButton("Back", skin);
		back.setSize(100.0f,  50.0f);
		back.setPosition(connect.getX() + connect.getWidth() + 20.0f, connect.getY());
		
		connectMenu.addActor(ip);
		connectMenu.addActor(port);
		connectMenu.addActor(connect);
		connectMenu.addActor(back);
	}
	
	public void draw(){
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
	
}
