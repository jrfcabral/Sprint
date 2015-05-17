package sprint.tests;

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
		
		System.out.print("Passed: " + passed + "\nFailed: " + failed + "\n");
	}
	
	private boolean testAccelerate(){
		car.setVelocity(0);
		car.setAngle(0);
		game.setThrottle(true);
		game.setBrake(false);
		car.update(game.getThrottle(), game.getBrake(), Car.SteerDirection.SteerNone);
		if(car.getVelocity() <= 0){
			return false;
		}
		else{
			return true;
		}
		
	}
	
	private boolean testBrake(){
		car.setVelocity(30);
		car.setAngle(90);
		game.setBrake(true);
		game.setThrottle(false);
		car.update(game.getThrottle(), game.getBrake(), Car.SteerDirection.SteerNone);
		if(car.getVelocity() >= 30){
			return false;
		}
		
		return true;
	}
	
}
