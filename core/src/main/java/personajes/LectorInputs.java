package personajes;

import java.util.HashSet;
import java.util.Set;
import com.badlogic.gdx.Input;
import escenas.Arena;

/**
 * Centraliza todos los inputs y se los pasa a Personaje y Jefe.
 * Ya no implementa InputProcessor. 
 * Los eventos son enviados por InputManager.
 */
public class LectorInputs {

    private final Heroe personaje; // puede ser null
    private final Jefe jefe;       // puede ser null
    private final Arena arena;

    private final Set<Integer> pressed = new HashSet<>();

    public LectorInputs(Heroe personaje, Jefe jefe, Arena arena) {
        this.personaje = personaje;
        this.jefe = jefe;
        this.arena = arena;
    }

    // ------- Helpers: actualizar movimiento continuo -------
    private void actualizarMovimiento() {
        boolean pLeft  = pressed.contains(Input.Keys.A);
        boolean pRight = pressed.contains(Input.Keys.D);

        if (personaje != null) {
            personaje.setInputLeft(pLeft);
            personaje.setInputRight(pRight);
        }

        boolean jLeft  = pressed.contains(Input.Keys.A);
        boolean jRight = pressed.contains(Input.Keys.D);

        if (jefe != null) {
            jefe.setInputLeft(jLeft);
            jefe.setInputRight(jRight);
        }
    }

    private void accionar(int keycode) {
        if (personaje != null) {
            if (keycode == Input.Keys.SPACE)             personaje.requestJump();
            else if (keycode == Input.Keys.SHIFT_LEFT)   personaje.requestDash();
            else if (keycode == Input.Keys.CONTROL_LEFT) personaje.requestBackdash();
            else if (keycode == Input.Keys.J)            personaje.requestAttack();
            else if (keycode == Input.Keys.K)            personaje.requestProyectil();
            else if (keycode == Input.Keys.L)            personaje.requestToggleVolador();

        }

        // --- ESC: toggle de menú ---
        if (arena != null && keycode == Input.Keys.ESCAPE) {
            if (arena.isMenuAbierto()) {
                arena.cerrarMenu();
            } else {
                arena.mostrarMenuConfiguracion();
            }
            // Importante: return para no “caer” a las acciones del jefe por error
            return;
        }

        if (jefe != null) {
            if (keycode == Input.Keys.W)                jefe.requestJump();
            else if (keycode == Input.Keys.SHIFT_RIGHT)  jefe.requestDash();
            else if (keycode == Input.Keys.CONTROL_RIGHT)jefe.requestBackdash();
            else if (keycode == Input.Keys.H)            jefe.requestToggleBestia();
            else if (keycode == Input.Keys.N)            jefe.requestToggleVertical();
            else if (keycode == Input.Keys.B)            jefe.requestToggleFinal();

        }
    }


    // ------- Delegados públicos llamados desde InputManager -------
    public void keyDownDelegado(int keycode) {
        pressed.add(keycode);
        actualizarMovimiento();
        accionar(keycode);
    }

    public void keyUpDelegado(int keycode) {
        pressed.remove(keycode);
        actualizarMovimiento();
    }
}
