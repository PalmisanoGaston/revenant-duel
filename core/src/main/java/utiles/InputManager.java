package utiles;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class InputManager implements InputProcessor {
    private InputProcessor currentProcessor;
    private InputProcessor arenaProcessor;
    private InputProcessor menuProcessor;
    
    public InputManager(InputProcessor arenaProcessor, InputProcessor menuProcessor) {
        this.arenaProcessor = arenaProcessor;
        this.menuProcessor = menuProcessor;
        this.currentProcessor = arenaProcessor; // Start with arena inputs
    }
    
    public void setArenaMode() {
        this.currentProcessor = arenaProcessor;
    }
    
    public void setMenuMode() {
        this.currentProcessor = menuProcessor;
    }
    
    public boolean isInMenuMode() {
        return currentProcessor == menuProcessor;
    }
    
    public boolean isInArenaMode() {
        return currentProcessor == arenaProcessor;
    }
    
    // Delegate all input methods to the current processor
    @Override
    public boolean keyDown(int keycode) {
        return currentProcessor.keyDown(keycode);
    }
    
    @Override
    public boolean keyUp(int keycode) {
        return currentProcessor.keyUp(keycode);
    }
    
    @Override
    public boolean keyTyped(char character) {
        return currentProcessor.keyTyped(character);
    }
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return currentProcessor.touchDown(screenX, screenY, pointer, button);
    }
    
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return currentProcessor.touchUp(screenX, screenY, pointer, button);
    }
    
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return currentProcessor.touchCancelled(screenX, screenY, pointer, button);
    }
    
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return currentProcessor.touchDragged(screenX, screenY, pointer);
    }
    
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return currentProcessor.mouseMoved(screenX, screenY);
    }
    
    @Override
    public boolean scrolled(float amountX, float amountY) {
        return currentProcessor.scrolled(amountX, amountY);
    }
}