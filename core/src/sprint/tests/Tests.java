package sprint.tests;

import static org.junit.Assert.*;

import com.badlogic.gdx.math.Vector2;

import sprint.server.logic.Car;
import sprint.server.logic.Game;;


public class Tests {
	public int passed;
	public int failed;
	public Car car;
	public Game game;
	
	public Tests(Game game){
		passed = 0;
		failed = 0;
		this.game = game;
		car = game.getCar();
	}
	
	public void run(){
		if(testAccelerate()){
			passed++;
		}
		else{
			failed++;
		}
	}
	
	public boolean testAccelerate(){
		float oldVel = car.getVelocity();
		game.setThrottle(true);
		if(car.getVelocity() < oldVel){
			return false;
		}
		else{
			return true;
		}
		
	}
	
}
