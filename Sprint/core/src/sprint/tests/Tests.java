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
		
		if(testBrake()){
			passed++;
		}
		else{
			failed++;
		}
		
		if(testTurnLeft()){
			passed++;
		}
		else{
			failed++;
		}
		
		if(testTurnRight()){
			passed++;
		}
		else{
			failed++;
		}
		
		System.out.print("Passed: " + passed + "\nFailed: " + failed + "\n");
	}
	
	private boolean testAccelerate(){
		car.setVelocity(0);
		car.update(game.getThrottle(), game.getBrake(), Car.SteerDirection.SteerNone);
		car.setAngle(0);
		game.setThrottle(true);
		game.setBrake(false);
		car.update(game.getThrottle(), game.getBrake(), Car.SteerDirection.SteerNone);
		if(car.getVelocity() <= 0){
			System.out.println("Accelerate test failed.");
			return false;
		}
		else{
			return true;
		}
		
	}
	
	private boolean testBrake(){
		car.setVelocity(30);
		//car.setAngle(0);
		game.setBrake(true);
		game.setThrottle(false);
		car.update(game.getThrottle(), game.getBrake(), Car.SteerDirection.SteerNone);
		if(car.getVelocity() >= 30){
			System.out.println("Brake test failed");
			return false;
		}
		
		return true;
	}
	
	private boolean testTurnLeft(){
		car.setVelocity(1.0f);
		car.update(game.getThrottle(), game.getBrake(), Car.SteerDirection.SteerNone);
		float oldAng = car.getAngle();
		car.update(game.getThrottle(), game.getBrake(), Car.SteerDirection.SteerLeft);
		if(car.getAngle() <= oldAng){
			System.out.println("Turn Left test failed.");
			return false;
		}
		else return true;
	}
	
	private boolean testTurnRight(){
		car.setVelocity(1.0f);
		car.update(game.getThrottle(), game.getBrake(), Car.SteerDirection.SteerNone);
		float oldAng = car.getAngle();
		car.update(game.getThrottle(), game.getBrake(), Car.SteerDirection.SteerRight);
		if(car.getAngle() >= oldAng){
			System.out.println("Turn Right test failed.");
			return false;
		}
		else return true;
	}
	
	
}
