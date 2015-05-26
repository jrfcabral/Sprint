package sprint.server.net;

import sprint.server.logic.Car;

public class PlayerControls {
	private String identifier;
	private boolean throttle;
	private boolean brake;
	private Car.SteerDirection steer;
	
	public boolean getThrottle(){return throttle;};
	public Car.SteerDirection getSteer(){return steer;};	
	public boolean getBrake(){return brake;};
}
