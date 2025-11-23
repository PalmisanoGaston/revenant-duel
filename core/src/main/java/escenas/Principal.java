package escenas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class Principal extends Game {
	
    @Override
    public void create() {
        setScreen(new Menu(this));
    }

    @Override public void dispose() {
        Screen current = getScreen();
        if (current != null) {
            current.dispose();
        }
        super.dispose();
    }
}
