package sprint.server.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * This class is responsible for handling the communication protocol with the client, as well as configuring and handling socket input and output
 */
public class Server {
	private Thread thread;
	private Lobby lobby;
	private HashMap<String, PlayerControls> bindings;
	
	/**
	 * Exception throw by the server when a new server tries to be created if one is still running.
	 */
	public class ServerRunningException extends RuntimeException{
		private static final long serialVersionUID = -8926610866812393171L;}	
	
	private class ServerThread implements Runnable{		
		@Override
		public void run() {			
	          
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
                	
                	if(message.equals("TEST")){
                		lobby.addToQueue(socket.getInetAddress().toString());
                		switch(lobby.getQueueSize()){
                			case 1:
                				out.write("Received Red\n".getBytes());
                				break;
                			case 2:
                				out.write("Received Blue\n".getBytes());
                				break;
                			case 3:
                				out.write("Received Green\n".getBytes());
                				break;
                			case 4:
                				out.write("Received Pink\n".getBytes());
                				break;
                			case 5:
                				out.write("Received Orange\n".getBytes());
                				break;
                			default: 
                				out.write("Received Full\n".getBytes());
                				break;
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
	
	public Server(Lobby lobby){
		this.lobby = lobby;
		thread = new Thread(new ServerThread());
		this.bindings = new HashMap<String, PlayerControls>();
	}
	/**
	 * Launches the server thread and listens for incoming connections
	 */
	public void launchServer() {
		if (!thread.isAlive()){			
			thread.start();
		}
		else
			throw new Server.ServerRunningException();
	}
	
	/**
	 * Binds the identifier of a player to its player controls. From now on any commands received from the identifier are forwarded to the given player controls
	 * Identifiers who are not bound have their commands ignored.
	 * @param identifier
	 * @param playerControls
	 */
	public void bindId(String identifier, PlayerControls playerControls) {		
		this.bindings.put(identifier, playerControls);
		if (this.bindings.get(identifier) == null)
			throw new IllegalArgumentException();
	}
	/**
	 * Removes the binding between the identifier and the associated player controls.
	 * @param identifier
	 */
	public void unbindId(String identifier){
		PlayerControls controls = bindings.get(identifier);
		if(controls == null)
			return;
		
		this.bindings.remove(identifier, controls);
	}
	
	/**
	 * @return a map that holds all currently active bindings between an identifier and the associated playercontrols.
	 */
	public HashMap<String, PlayerControls> getMap(){
		return this.bindings;
	}

}
