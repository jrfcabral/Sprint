package sprint.server.net;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class Lobby {
	public static final int MAX_PLAYERS = 5;
	public static final int READY_TIMER = 1;
	
	private LinkedList<String> players;
	private int elapsed = 0;
	Timer timer;
	private boolean open;
	private boolean timerActivated;
	
	public void setOpen(boolean open){this.open = open;}
	
	public int getElapsed(){
		return this.elapsed;
	}
	
	public int getQueueSize(){return players.size();};
	
	public Lobby(){
		players = new LinkedList<String>();
		timerActivated = false;
	}
	
	public void startTimer(){
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerUpdate(), 0, 1000);
			timerActivated = true;
	}
	
	public void stopTimer(){
		this.timer.cancel();
		elapsed = 0;
		timerActivated = false;		
	}
	public LinkedList<String> getIdentifiers(){
		return this.players;
	}
	
	public void addToQueue(String identifier){
		players.push(identifier);
		if(players.size() < MAX_PLAYERS)
			elapsed = 0;
	}
	public void removeFromQueue(String identifier){
		players.removeFirstOccurrence(identifier); //and only		
	}
	
	private class TimerUpdate extends TimerTask{
		@Override
		public synchronized void run() {
			if(Lobby.this.players.size() > 0)
				Lobby.this.elapsed++;
			//System.out.println(Lobby.this.elapsed);
		}
		
	}
	
}
