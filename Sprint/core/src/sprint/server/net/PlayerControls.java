package sprint.server.net;

import sprint.server.logic.Car;

public class PlayerControls {
	public enum Command{
		BRAKE{
			public void handleCommand(PlayerControls controls){
				controls.setBrake(true);
			}			
		}, 
		THROTTLE{
			public void handleCommand(PlayerControls controls){
				if (controls == null)
					System.out.println("nao existe");
				else
				controls.setThrottle(true);
				System.out.println("Accelerate");
			}
		}, 
		STEER_LEFT{
			public void handleCommand(PlayerControls controls){
				controls.setSteer(Car.SteerDirection.SteerRight);
			}
		},
		STEER_RIGHT{
			public void handleCommand(PlayerControls controls){
				controls.setSteer(Car.SteerDirection.SteerLeft);
			}
		},
		NOSTEER{
			public void handleCommand(PlayerControls controls){
				controls.setSteer(Car.SteerDirection.SteerNone);
			}
		},
		NOP{
			public void handleCommand(PlayerControls controls){
				controls.setBrake(false);
				controls.setThrottle(false);
			}
		},
		TEST {			
			public void handleCommand(PlayerControls controls) {System.out.println("eu disse vou-te testar");};
		},
		LEAVE {			
			public void handleCommand(PlayerControls controls) {controls.setActive(false);};
		};
		public abstract void handleCommand(PlayerControls controls);
	}
	private boolean throttle;
	private boolean brake;
	private Car.SteerDirection steer;
	private String id;
	private String color;
	private boolean active;
	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}
	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	/**
	 * @param throttle the throttle to set
	 */
	public void setThrottle(boolean throttle) {
		this.throttle = throttle;
	}
	/**
	 * @param brake the brake to set
	 */
	public void setBrake(boolean brake) {
		this.brake = brake;
	}
	/**
	 * @param steer the steer to set
	 */
	public void setSteer(Car.SteerDirection steer) {
		this.steer = steer;
	}
	public boolean getThrottle(){return throttle;};
	public Car.SteerDirection getSteer(){return steer;};	
	public boolean getBrake(){return brake;};
	
	public String getId(){
		return this.id;
	}
	
	public String getColor(){
		return color;
	}

	
	public PlayerControls(String identifier, Server server , String color){
		server.bindId(identifier, this);		
		steer = Car.SteerDirection.SteerNone;
		id = identifier;
		this.color = color;
		active = true;
	}	
	
}
