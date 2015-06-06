package sprint.server.logic;

import com.badlogic.gdx.scenes.scene2d.Stage;

public interface State {
	public void update();
	public void create();
	public void draw();
	public void resize(int width, int height);
}
