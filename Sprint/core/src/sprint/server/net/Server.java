package sprint.server.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import com.badlogic.gdx.Gdx;

import sprint.server.logic.Game;

public class Server {
	private Thread thread;
	private Game game;
	private Lobby lobby;
	private class ServerThread implements Runnable{
		
		@Override
		public void run() {
			System.out.println("Thread is running");
	          
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
                	String command = buffer.readLine();
                	System.out.println("From ip " + socket.getInetAddress() + ":");
                	System.out.println(command);
                	
                	if(command.equals("Test")){                		
                		out.write("Received\n".getBytes());
                		out.flush();
                		lobby.addToQueue(socket.getInetAddress().toString());                		               		                		
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
	public class ServerRunningException extends RuntimeException{		
	}	
	public Server(Game game, Lobby lobby){
		this.lobby = lobby;
		this.game = game;
		thread = new Thread(new ServerThread());
	}
	public void launchServer() {
		if (!thread.isAlive()){			
			thread.start();
		}
		else
			throw new Server.ServerRunningException();
	}
	
	

}
