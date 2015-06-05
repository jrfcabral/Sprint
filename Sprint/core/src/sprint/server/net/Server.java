package sprint.server.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;

import sprint.server.logic.Game;

public class Server {
	private Thread thread;
	private Game game;
	private Lobby lobby;
	private HashMap<String, PlayerControls> bindings;
	private class ServerThread implements Runnable{
		
		@Override
		public void run() {
			System.out.println("Thread is running");
			System.out.println(PlayerControls.Command.BRAKE.toString());
	          
        	ServerSocket serverSocket = null;
            try {
				serverSocket = new ServerSocket(8888);				
			} catch (IOException e1) { 
				e1.printStackTrace();
			}
            
            while(true){
                // Create a socket
                Socket socket = null;
                DataOutputStream out = null;
                
                try {
                	socket = serverSocket.accept();
                	BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                	out = new DataOutputStream(socket.getOutputStream());   
                	String message = buffer.readLine();
                	PlayerControls.Command command = PlayerControls.Command.valueOf(message);
                	if(bindings.get(socket.getInetAddress().toString()) != null){
                		command.handleCommand(bindings.get(socket.getInetAddress().toString()));
                		//System.out.println("Disse vou-te encontrar e encontrei");
                	}
                	
                	System.out.println("From ip " + socket.getInetAddress() + ":");
                	System.out.println(message);
                	
                	if(message.equals("TEST")){
                		lobby.addToQueue(socket.getInetAddress().toString());
                		switch(lobby.getQueueSize()){
                			case 1:
                				out.write("Received Red\n".getBytes());
                			case 2:
                				out.write("Received Blue\n".getBytes());
                			case 3:
                				out.write("Received Green\n".getBytes());
                			case 4:
                				out.write("Received Black\n".getBytes());
                			case 5:
                				out.write("Received Yellow\n".getBytes());
                		}
                		
                		out.flush();
                		                		               		                		
                	}
                	if (message.equals("LEAVE")){                		
                		Server.this.unbindId(socket.getInetAddress().toString());
                		lobby.removeFromQueue(socket.getInetAddress().toString());
                		
                	}
                
                	}
                catch (IOException e) {
                    e.printStackTrace();
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
    				
    				if(out != null){
    					try{
    						out.close();
    					}
    					catch(IOException e){
    						e.printStackTrace();
    					}
    				}
    			}
            }
        }
	}
	public class ServerRunningException extends RuntimeException{}	
	public Server(Game game, Lobby lobby){
		this.lobby = lobby;
		this.game = game;
		thread = new Thread(new ServerThread());
		this.bindings = new HashMap<String, PlayerControls>();
	}
	public void launchServer() {
		if (!thread.isAlive()){			
			thread.start();
		}
		else
			throw new Server.ServerRunningException();
	}
	public void bindId(String identifier, PlayerControls playerControls) {	
		System.out.println("Binding " + identifier);
		this.bindings.put(identifier, playerControls);
		if (this.bindings.get(identifier) == null)
			throw new IllegalArgumentException();
	}
	public void unbindId(String identifier){
		PlayerControls controls = bindings.get(identifier);
		if(controls == null)
			return;
		
		this.bindings.remove(identifier, controls);
	}
	
	public HashMap<String, PlayerControls> getMap(){
		return this.bindings;
	}

}
