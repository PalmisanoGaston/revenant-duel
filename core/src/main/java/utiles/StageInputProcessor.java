package utiles;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class StageInputProcessor implements InputProcessor {
    private final Stage stage;
    
    public StageInputProcessor(Stage stage) {
        this.stage = stage;
    }
    
    @Override
    public boolean keyDown(int keycode) {
        return stage.keyDown(keycode);
    }
    
    @Override
    public boolean keyUp(int keycode) {
        return stage.keyUp(keycode);
    }
    
    @Override
    public boolean keyTyped(char character) {
        return stage.keyTyped(character);
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return stage.touchDown(screenX, screenY, pointer, button);
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return stage.touchUp(screenX, screenY, pointer, button);
    }
    
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return stage.touchCancelled(screenX, screenY, pointer, button);
    }
    
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return stage.touchDragged(screenX, screenY, pointer);
    }
    
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return stage.mouseMoved(screenX, screenY);
    }
    
    @Override
    public boolean scrolled(float amountX, float amountY) {
        return stage.scrolled(amountX, amountY);
    }
}