package sprint.server.net;

import sprint.server.logic.Car;

/**
 * The class responsible for transmitting player commands to the cars
  */
public class PlayerControls {
	/**
	 *Enumerates the various commands that the player can give, and translates them from the strings sent from the remote application to the server 
	 *
	 */
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
		/**
		 * 
		 * @param controls Takes an incoming command and updates the internal state of the player controls 
		 */
		public abstract void handleCommand(PlayerControls controls);
	}
	private boolean throttle;
	private boolean brake;
	private Car.SteerDirection steer;
	private String id;
	private String color;
	private boolean active;
	/**
	 * @return true if the player is connected, false if they left
	 */
	public boolean isActive() {
		return active;
	}
	/**
	 * @param active true if the player is connected, false if they left
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	/**
	 * @param throttle true if the player is throttling the car, false otherwise
	 */
	public void setThrottle(boolean throttle) {
		this.throttle = throttle;
	}
	/**
	 * @param brake true if the player is braking, false otherwise
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
	/**
	 * 
	 * @return true if the player is throttling the car, false otherwise.
	 */
	public boolean getThrottle(){return throttle;};
	/**
	 * 
	 * @return the direction of the player is steering the car in
	 */
	public Car.SteerDirection getSteer(){return steer;};	
	/**
	 * @return true if player is breaking the car, false otherwise.
	 */
	public boolean getBrake(){return brake;};
	/**
	 * @return the identifier of the player who is associated with this control
	 */
	public String getId(){
		return this.id;
	}
	
	/**
	 * @return the car color the player is currently controlling
	 */
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
