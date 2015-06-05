package sprint.server.logic;

import com.badlogic.gdx.scenes.scene2d.Stage;

import sprint.server.gui.MainMenu;
import sprint.server.net.Lobby;
import sprint.server.net.Server;

/**
 * Part of the State pattern, this class holds the context for the state machine system to work and switches between states
 */
public class StateMachine {
	private State state;
	Thread serverThread;
	Server server;
	Lobby lobby;	
	public StateMachine(){
		state = new MainMenu(this);
		lobby = new Lobby();
		server = new Server(lobby);
	}
	
	/**
	 * Called by the States when they are done and want to create a new state
	 * @param state the new state to be switched to
	 */
	public void setState(State state)
	{
		System.out.println("machine state switching");
		this.state = state;
	}
	
	/**
	 * Updates the current state according to its internal procedures. May cause a state switch
	 */
	public void update(){
		this.state.update();
	}
	
	/**
	 * Causes the current state to draw itself. Will not cause a state switch.
	 */
	public void draw(){
		this.state.draw();
	}
	
	/**
	 * The current state calls its create method, generating itself again.
	 */
	public void create(){
		this.state.create();
	}

	public Server getServer() {
		return this.server;
	}
	public Lobby getLobby(){
		if (lobby == null)System.out.println("erro aqui");
		return this.lobby;
	}
	
}
