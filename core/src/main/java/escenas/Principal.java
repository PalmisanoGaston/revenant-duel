package escenas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import red.ServerThread;

public class Principal extends Game {
	
    @Override
    public void create() {
        setScreen(new Arena(this, new Skin(Gdx.files.internal("uiskin.json"))));
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
