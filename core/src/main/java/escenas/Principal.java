package escenas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import red.ServerThread;

public class Principal extends Game {
	
    @Override
    public void create() {
        setScreen(new Menu(this));
    }

    @Override public void dispose() {
        ServerThread server = ServerThread.getInstance();

        if( server != null) {
            server.terminate();
        }

        Screen current = getScreen();
        if (current != null) {
            current.dispose();
        }
        super.dispose();
    }
}
