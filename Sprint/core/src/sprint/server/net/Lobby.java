package sprint.server.net;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class responsible for handling incoming connections while they wait for the game to begin.
  */
public class Lobby {
	public static final int MAX_PLAYERS = 5;
	public static final int READY_TIMER = 3;
	
	private LinkedList<String> players;
	private int elapsed = 0;
	Timer timer;
	public void setOpen(boolean open){}
	
	/**
	 * @return How much time has elapsed since there have been players in the room
	 */
	public int getElapsed(){
		return this.elapsed;
	}
	
	/**
	 * 
	 * @return how many players are in the queue, waiting for the game to begin.
	 */
	public int getQueueSize(){return players.size();};
	
	public Lobby(){
		players = new LinkedList<String>();
	}
	
	/**
	 * Makes the time start counting, called when the first player joins the room so that the game can begin when the timer reaches its 
	 * maximum elapsed time
	 */
	public void startTimer(){
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerUpdate(), 0, 1000);
	}
	
	/**
	 * Makes the timer stop counting. Called when no more players are in the room or a race is underways.
	 */
	public void stopTimer(){
		this.timer.cancel();
		elapsed = 0;		
	}
	
	/**
	 * @return the list of identifiers of the players connected to this lobby
	 */
	public LinkedList<String> getIdentifiers(){
		return this.players;
	}
	
	/**
	 * @param identifier the identifier of the player to be added to the queue
	 */
	public void addToQueue(String identifier){
		players.push(identifier);
		if(players.size() < MAX_PLAYERS)
			elapsed = 0;
	}
	
	/**
	 * 
	 * @param identifier the identifier of the player to be removed from the queue
	 */
	public void removeFromQueue(String identifier){
		players.removeFirstOccurrence(identifier); //and only		
	}
	
	private class TimerUpdate extends TimerTask{
		@Override
		public synchronized void run() {
			if(Lobby.this.players.size() > 0)
				Lobby.this.elapsed++;
		}
		
	}
	
}
