// Personaje.java
package personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.World;

import Interfaces.CambioVidaEventListener;
import Interfaces.MuerteEventListener;
import io.github.revenantduel.Arena;
import mejoras.MejoraVida;
import movimientos.AtaqueBasico;
import movimientos.Backdash;
import movimientos.Dash;
import movimientos.Salto;

public class Personaje extends PersonajeBase {
	
	private MejoraVida nivelVida;
	
	
	

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
        
        if (movimientoActual == null || movimientoActual.estaCompletado()) {
            float velocidadX = 0;
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                velocidadX = -5f;
                this.lado = false;
                if(this.animacionActual != this.animacionPersonaje.getRunAnimation()) {
                    this.animacionActual = this.animacionPersonaje.getRunAnimation();    
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                velocidadX = 5f;
                this.lado = true;
                if(this.animacionActual != this.animacionPersonaje.getRunAnimation()) {
                    this.animacionActual = this.animacionPersonaje.getRunAnimation();    
                }
            } else {
                if(this.animacionActual != this.animacionPersonaje.getIdleAnimation() && this.vida > 0) {
                    this.animacionActual = this.animacionPersonaje.getIdleAnimation();    
                }
            }
            
            body.setLinearVelocity(velocidadX, body.getLinearVelocity().y);
            

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && (Math.abs(body.getLinearVelocity().y) < 0.1f)) {
                Salto salto = (Salto)movimientos.get("Salto");
                salto.reiniciar();
                this.movimientoActual = salto;
                this.animacionActual = this.animacionPersonaje.getJumpAnimation();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) && Math.abs(body.getLinearVelocity().y) < 0.1f) {
            Dash dash = (Dash)movimientos.get("Dash");
            dash.setLadoDerecho(lado);
            dash.reiniciar();
            movimientoActual = dash;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT) && Math.abs(body.getLinearVelocity().y) < 0.1f) {
            Backdash backdash = (Backdash)movimientos.get("Backdash");
            backdash.setLadoDerecho(lado);
            backdash.reiniciar();
            movimientoActual = backdash;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.J) && movimientoActual == null && Math.abs(body.getLinearVelocity().y) < 0.1f) {
            AtaqueBasico ataque = (AtaqueBasico)movimientos.get("Ataque");
            ataque.setLadoDerecho(lado);
            ataque.reiniciar();
            movimientoActual = ataque;
            this.animacionActual = this.animacionPersonaje.getAnimacionAtaque();
        }
        
        if (movimientoActual != null && !movimientoActual.estaCompletado()) {
            movimientoActual.actualizar();
            movimientoActual.aplicarEfecto();
        } else {
            movimientoActual = null;
        }
        
        setPosition(
            (body.getPosition().x / Arena.PIXELS_TO_METERS) - getWidth()/2,
            (body.getPosition().y / Arena.PIXELS_TO_METERS) - getHeight()/2
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