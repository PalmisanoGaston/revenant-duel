package utiles;

import Interfaces.GameController;
import escenas.Arena;

public class InputManager implements com.badlogic.gdx.InputProcessor {
    private GameController gameController;
    private Arena arena;
    private boolean menuMode = false;

    public InputManager(GameController gameController) {
        this.gameController = gameController;
        if (gameController instanceof Arena) {
            this.arena = (Arena) gameController;
        }
    }


    @Override
    public boolean keyDown(int keycode) {
        // Handle escape key for menu (client-side)
        if (keycode == com.badlogic.gdx.Input.Keys.ESCAPE) {
            if (arena != null) {
                if (arena.isMenuAbierto()) {
                    arena.cerrarMenu();
                } else {
                    arena.mostrarMenuConfiguracion();
                }
            }
            return true;
        }
        
        // Only send game inputs if not in menu mode
        if (!menuMode && arena != null && !arena.isMenuAbierto()) {
            // Get the actual assigned role from arena
            int role = arena.getPlayerRole();
            if (role != -1) { // Only send if role is assigned
                System.out.println("Sending input - Role: " + role + ", Key: " + keycode);
                gameController.accionar(role, keycode);
            } else {
                System.out.println("Role not assigned yet, cannot send input");
            }
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public void setMenuMode() {
        this.menuMode = true;
    }

    public void setArenaMode() {
        this.menuMode = false;
    }

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}
}