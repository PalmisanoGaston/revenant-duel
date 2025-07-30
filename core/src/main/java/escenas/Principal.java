package escenas;

import com.badlogic.gdx.Game;

public class Principal extends Game {
    @Override
    public void create() {
        setScreen(new Menu(this));
    }
}
