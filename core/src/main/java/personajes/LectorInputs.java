package personajes;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.Input;

import escenas.Arena;

public class LectorInputs {
    private final Heroe personaje;
    private final Jefe jefe;
    private final Arena arena;
    private final Set<Integer> pressed = new HashSet<>();

    public LectorInputs(Heroe personaje, Jefe jefe, Arena arena) {
        this.personaje = personaje;
        this.jefe = jefe;
        this.arena = arena;
    }

    private void actualizarMovimiento(int rol) {
        boolean left = pressed.contains(Input.Keys.A);
        boolean right = pressed.contains(Input.Keys.D);
        if (rol == 0 && personaje != null) {
            // Hero movement - A/D keys
            personaje.setInputLeft(left);
            personaje.setInputRight(right);
        } else if (rol == 1 && jefe != null) {
            jefe.setInputLeft(left);
            jefe.setInputRight(right);
        }
    }

    public void procesarInputPorRol(int rol, int keycode, boolean keyDown) {
        if (keyDown) {
            pressed.add(keycode);
        } else {
            pressed.remove(keycode);
        }
        
        // Update movement for the specific role
        actualizarMovimiento(rol);
        
        // Handle actions (only on key down)
        if (keyDown) {
            if (rol == 0 && personaje != null) {
                handleHeroInput(keycode);
            } else if (rol == 1 && jefe != null) {
                handleBossInput(keycode);
            }
        }
    }

    private void handleHeroInput(int keycode) {
        switch (keycode) {
            case Input.Keys.SPACE:
                personaje.requestJump();
                break;
            case Input.Keys.SHIFT_LEFT:
                personaje.requestDash();
                break;
            case Input.Keys.CONTROL_LEFT:
                personaje.requestBackdash();
                break;
            case Input.Keys.Q:
                personaje.requestAttack();
                break;
            case Input.Keys.E:
                personaje.requestProyectil();
                break;
            case Input.Keys.R:
                personaje.requestToggleVolador();
                break;
        }
    }

    private void handleBossInput(int keycode) {
        switch (keycode) {
            case Input.Keys.W:
                jefe.requestJump();
                break;
            case Input.Keys.SHIFT_LEFT:
            case Input.Keys.SHIFT_RIGHT:
                jefe.requestDash();
                break;
            case Input.Keys.CONTROL_LEFT:
            case Input.Keys.CONTROL_RIGHT:
                jefe.requestBackdash();
                break;
            case Input.Keys.Q:
                jefe.requestAttack();
                break;
            case Input.Keys.H:
                jefe.requestToggleBestia();
                break;
            case Input.Keys.E:
                jefe.requestToggleVertical();
                break;
            case Input.Keys.R:
                jefe.requestToggleFinal();
                break;
        }
    }
}