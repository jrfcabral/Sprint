package sprint.server.logic;


/**
 * Interface that represents the methods all the states of the game should implement.
 */
public interface State {
	/**
	 * Causes the state to update itself in accordance to any new input it has received or merely the passage of time.
	 */
	public void update();
	/**
	 * Invokes the behaviours needed to setup the state. Can be used to reset the state.
	 */
	public void create();
	/**
	 * Causes the state to draw its current representation on the screen.
	 */
	public void draw();
	/**
	 * Should be called on the state each time the application window is resized. This method should handle said resize.
	 * @param width
	 * @param height
	 */
	public void resize(int width, int height);
}
