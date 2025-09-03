// Personaje.java
package personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.World;

import Interfaces.CambioVidaEventListener;
import Interfaces.MuerteEventListener;
import escenas.Arena;
import mejoras.MejoraVida;
import movimientos.AtaqueBasico;
import movimientos.Backdash;
import movimientos.Dash;
import movimientos.Salto;
import sonidos.SonidosPersonaje;

public class Personaje extends PersonajeBase {
	
	private MejoraVida nivelVida;
	private SonidosPersonaje sonidos = new SonidosPersonaje();
	
	// --- INPUT FLAGS (externos, seteados por LectorInputs) ---
	private boolean inLeft;
	private boolean inRight;

	// Acciones edge-triggered: el Lector las dispara con requestX()
	// y el Personaje las consume en act() una sola vez.
	private boolean inJump;
	private boolean inAttack;
	private boolean inDash;
	private boolean inBackdash;

	// Setters llamados por LectorInputs
	public void setInputLeft(boolean v)    { this.inLeft = v; }
	public void setInputRight(boolean v)   { this.inRight = v; }
	public void requestJump()              { this.inJump = true; }
	public void requestAttack()            { this.inAttack = true; }
	public void requestDash()              { this.inDash = true; }
	public void requestBackdash()          { this.inBackdash = true; }

	
    public Personaje(World world, MuerteEventListener muerteListener,  CambioVidaEventListener vidaListener, MejoraVida nivelVida) {
        super(world, "Jugador", 100 * nivelVida.getMultiplicador()   , muerteListener, vidaListener, new AnimacionesPersonaje());

        movimientos.put("Dash", new Dash(body, lado));
        movimientos.put("Salto", new Salto(body,1));
        movimientos.put("Backdash", new Backdash(body, lado));
        movimientos.put("Ataque", new AtaqueBasico(body, lado));
        
        this.animacionActual = animacionPersonaje.getIdleAnimation();
        this.nivelVida = nivelVida;
    }

	@Override
	public void act(float delta) {
	    super.stateTime += delta;

	    if (this.vida > 0) {
	        if (movimientoActual == null || movimientoActual.estaCompletado()) {
	            float velocidadX = 0f;

	            // === Movimiento horizontal continuo (A/D) ===
	            if (inLeft && !inRight) {
	                velocidadX = -5f;        // igual que en tu código original
	                this.lado = false;
	                if (this.animacionActual != this.animacionPersonaje.getRunAnimation()) {
	                    this.animacionActual = this.animacionPersonaje.getRunAnimation();
	                }
	            } else if (inRight && !inLeft) {
	                velocidadX = 5f;         // igual que en tu código original
	                this.lado = true;
	                if (this.animacionActual != this.animacionPersonaje.getRunAnimation()) {
	                    this.animacionActual = this.animacionPersonaje.getRunAnimation();
	                }
	            } else {
	                // Quieto -> Idle
	                if (this.animacionActual != this.animacionPersonaje.getIdleAnimation()) {
	                    this.animacionActual = this.animacionPersonaje.getIdleAnimation();
	                }
	            }

	            // Aplicar velocidad horizontal (conservando Y)
	            body.setLinearVelocity(velocidadX, body.getLinearVelocity().y);

	            // === Acciones edge-trigger (SPACE / SHIFT_LEFT / CONTROL_LEFT / J) ===
	            if (inJump) {
	                // Tu salto original: new Salto(body, 1)
	                movimientoActual = new Salto(body, 1);
	                this.animacionActual = this.animacionPersonaje.getJumpAnimation();
	                inJump = false;
	            }

	            if (inDash) {
	                movimientoActual = new Dash(body, lado);
	                inDash = false;
	            }

	            if (inBackdash) {
	                movimientoActual = new Backdash(body, lado);
	                inBackdash = false;
	            }

	            if (inAttack) {
	                movimientoActual = new AtaqueBasico(body, lado);
	                inAttack = false;
	            }

	        }
	    }

	    // Actualización del movimiento en curso (igual que lo tenías)
	    if (movimientoActual != null && !movimientoActual.estaCompletado()) {
	        movimientoActual.actualizar();
	        movimientoActual.aplicarEfecto();
	    } else {
	        movimientoActual = null;
	    }

	    // Mantener posición según el body (igual que lo tenías)
	    setPosition(
	        (body.getPosition().x / Arena.PIXELS_TO_METERS) - getWidth() / 2,
	        (body.getPosition().y / Arena.PIXELS_TO_METERS) - getHeight() / 2
	    );
	}
	
    public MejoraVida getNivelVida() {
		return nivelVida;
	}
    
    public void mejorarVida() {
        int nivelActual = MejoraVida.buscarNivel(this.nivelVida);
        if (nivelActual < MejoraVida.values().length - 1) {
            this.nivelVida = MejoraVida.values()[nivelActual + 1];
        }
    }

}