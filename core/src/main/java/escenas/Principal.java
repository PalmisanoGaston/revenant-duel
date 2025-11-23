package escenas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import red.ClientThread;

public class Principal extends Game {
	
    @Override
    public void create() {
        setScreen(new Menu(this));
    }

    @Override public void dispose() {
        ClientThread client = ClientThread.getInstance();
        if (client != null) {
            client.terminate();
        }
        Screen current = getScreen();
        if (current != null) { 
            current.dispose(); 
        } 
        super.dispose();
    }
}
