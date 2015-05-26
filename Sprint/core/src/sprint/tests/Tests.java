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
		car.update(false, false, Car.SteerDirection.SteerNone);
		
		for(int i = 0; i < 10; i++){
			car.update(true, false, Car.SteerDirection.SteerNone);
			game.getWorld().step(1/60f, 6, 2);
		}
		if(car.getVelocity() <= 0){
			System.out.println("Accelerate test failed.");
			return false;
		}
		else{
			return true;
		}
		
	}
	
	private boolean testBrake(){
		car.setVelocity(0f);
		car.update(false, false, Car.SteerDirection.SteerNone);
		
		car.setVelocity(30);
		car.update(false, false, Car.SteerDirection.SteerNone);
		
		for(int i = 0; i < 10; i++){
			car.update(false, true, Car.SteerDirection.SteerNone);
			game.getWorld().step(1/60f, 6, 2);
		}
		
		if(car.getVelocity() >= 30){
			System.out.println("Brake test failed");
			return false;
		}
		
		return true;
	}
	
	private boolean testTurnLeft(){
		car.setVelocity(0f);
		car.update(false, false, Car.SteerDirection.SteerNone);
		
		car.setVelocity(1.0f);
		car.update(false, false, Car.SteerDirection.SteerNone);
		
		float oldAng = car.getAngle();
		
		for(int i = 0; i < 10; i++){
			car.update(false, false, Car.SteerDirection.SteerLeft);
			game.getWorld().step(1/60f, 6, 2);
		}
		
		if(car.getAngle() >= oldAng){
			System.out.println("Turn Left test failed.");
			return false;
		}
		else return true;
	}
	
	private boolean testTurnRight(){
		car.setVelocity(0f);
		car.update(false, false, Car.SteerDirection.SteerNone);
		
		car.setVelocity(1.0f);
		car.update(false, false, Car.SteerDirection.SteerNone);
		
		float oldAng = car.getAngle();
		
		for(int i = 0; i < 10; i++){
			car.update(false, false, Car.SteerDirection.SteerRight);
			game.getWorld().step(1/60f, 6, 2);
		}
		
		if(car.getAngle() <= oldAng){
			return false;
		}
		else return true;
	}
	
	
}
