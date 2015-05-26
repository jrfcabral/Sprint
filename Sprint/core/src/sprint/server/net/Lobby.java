package sprint.server.net;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class Lobby {
	public static final int MAX_PLAYERS = 5;
	
	private LinkedList<String> players;
	int elapsed = 0;
	Timer timer;
	
	public int getQueueSize(){return players.size();};
	
	public Lobby(){
		players = new LinkedList<String>();
		timer = new Timer();		
	}
	
	public void startTimer(){
		timer.scheduleAtFixedRate(new TimerUpdate(), 0, 1000);
	}
	
	public void addToQueue(String identifier){
		players.push(identifier);
	}
	public void removeFromQueue(String identifier){
		players.removeFirstOccurrence(identifier); //and only		
	}
	
	private class TimerUpdate extends TimerTask{

		@Override
		public synchronized void run() {
			Lobby.this.elapsed++;
			System.out.println(Lobby.this.elapsed);
		}
		
	}
	
}
