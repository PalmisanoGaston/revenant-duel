package utiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import personajes.LectorInputs;

public class InputManager implements InputProcessor {

    private final LectorInputs lectorInputs;
    private final ClickReceptor clickReceptor;
    private boolean menuMode = false;

    public InputManager(LectorInputs lectorInputs, ClickReceptor clickReceptor) {
        this.lectorInputs = lectorInputs;
        this.clickReceptor = clickReceptor;
    }

    public void setMenuMode() {
        menuMode = true;
    }

    public void setArenaMode() {
        menuMode = false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (menuMode && keycode != Input.Keys.ESCAPE)
            return false;

        lectorInputs.keyDownDelegado(keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (menuMode && keycode != Input.Keys.ESCAPE)
            return false;

        lectorInputs.keyUpDelegado(keycode);
        return true;
    }

    // --- Redirigir clicks al Stage cuando el menú está activo ---
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (menuMode) {
            return clickReceptor.touchDown(screenX, screenY, pointer, button);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (menuMode) {
            return clickReceptor.touchUp(screenX, screenY, pointer, button);
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (menuMode) {
            return clickReceptor.touchDragged(screenX, screenY, pointer);
        }
        return false;
    }

    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
}
