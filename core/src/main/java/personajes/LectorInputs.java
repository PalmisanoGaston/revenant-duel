package personajes;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import escenas.Arena;

/**
 * Centraliza todos los inputs y se los pasa a Personaje y Jefe.
 * Respeta tus mapeos originales:
 *  - Personaje: A/D, SPACE, SHIFT_LEFT, CONTROL_LEFT, J
 *  - Jefe: LEFT/RIGHT, UP, SHIFT_RIGHT, CONTROL_RIGHT, M
 *  - (Opcional) H para toggle de modo bestia en Jefe
 */
public class LectorInputs implements InputProcessor {

    private final Heroe personaje; // puede ser null
    private final Jefe jefe;           // puede ser null
    private final Arena arena;

    private final Set<Integer> pressed = new HashSet<>();

    public LectorInputs(Heroe personaje, Jefe jefe, Arena arena) {
        this.personaje = personaje;
        this.jefe = jefe;
        this.arena = arena;
    }

    // ------- Helpers: actualizar movimiento continuo -------
    private void actualizarMovimiento() {
        // Personaje: A/D
        boolean pLeft  = pressed.contains(Input.Keys.A);
        boolean pRight = pressed.contains(Input.Keys.D);

        if (personaje != null) {
            personaje.setInputLeft(pLeft);
            personaje.setInputRight(pRight);
        }

        // Jefe: LEFT/RIGHT
        boolean jLeft  = pressed.contains(Input.Keys.LEFT);
        boolean jRight = pressed.contains(Input.Keys.RIGHT);

        if (jefe != null) {
            jefe.setInputLeft(jLeft);
            jefe.setInputRight(jRight);
        }
    }

    // ------- Helpers: acciones (edge-trigger) -------
    private void accionar(int keycode) {
        if (personaje != null) {
            if (keycode == Input.Keys.SPACE)        personaje.requestJump();
            else if (keycode == Input.Keys.SHIFT_LEFT)     personaje.requestDash();
            else if (keycode == Input.Keys.CONTROL_LEFT)   personaje.requestBackdash();
            else if (keycode == Input.Keys.J)              personaje.requestAttack();
            else if (keycode == Input.Keys.K)              personaje.requestProyectil();
        }
        
        if(arena != null) {
            if(keycode == Input.Keys.ESCAPE) {
                this.arena.mostrarMenuConfiguracion();
            }
        }
        

        if (jefe != null) {
            if (keycode == Input.Keys.UP)           jefe.requestJump();
            else if (keycode == Input.Keys.SHIFT_RIGHT)    jefe.requestDash();
            else if (keycode == Input.Keys.CONTROL_RIGHT)  jefe.requestBackdash();
            else if (keycode == Input.Keys.M)              jefe.requestAttack();
            else if (keycode == Input.Keys.H)              jefe.requestToggleBestia(); // opcional
        }
    }

    // ---------------- InputProcessor ----------------
    @Override
    public boolean keyDown(int keycode) {
        pressed.add(keycode);
        actualizarMovimiento();
        accionar(keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        pressed.remove(keycode);
        actualizarMovimiento();
        return true;
    }

    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}
}