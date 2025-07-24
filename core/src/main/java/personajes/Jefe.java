// Jefe.java
package personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.World;

import Interfaces.CambioVidaEventListener;
import Interfaces.MuerteEventListener;
import io.github.revenantduel.Arena;
import movimientos.AtaqueBasico;
import movimientos.Backdash;
import movimientos.Dash;
import movimientos.Salto;

public class Jefe extends PersonajeBase {

    public Jefe(World world, MuerteEventListener muerteListener, 
               CambioVidaEventListener vidaListener) {
        super(world, "Jefe", 150, muerteListener, vidaListener);
        
        movimientos.put("Dash", new Dash(body, lado));
        movimientos.put("Salto", new Salto(body));
        movimientos.put("Backdash", new Backdash(body, lado));
        movimientos.put("Ataque", new AtaqueBasico(body, lado));
        
        this.animacionActual = animacionPersonaje.getIdleAnimation();
    }

    @Override
    public void act(float delta) {
        super.stateTime += delta;
        
        if (movimientoActual == null || movimientoActual.estaCompletado()) {
            float velocidadX = 0;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                velocidadX = -2f;
                this.lado = false;
                if(this.animacionActual != this.animacionPersonaje.getRunAnimation()) {
                    this.animacionActual = this.animacionPersonaje.getRunAnimation();    
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                velocidadX = 2f;
                this.lado = true;
                if(this.animacionActual != this.animacionPersonaje.getRunAnimation()) {
                    this.animacionActual = this.animacionPersonaje.getRunAnimation();    
                }
            } else {
                if(this.animacionActual != this.animacionPersonaje.getIdleAnimation()) {
                    this.animacionActual = this.animacionPersonaje.getIdleAnimation();    
                }
            }
            
            body.setLinearVelocity(velocidadX, body.getLinearVelocity().y);
            
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && (Math.abs(body.getLinearVelocity().y) < 0.1f)) {
                Salto salto = (Salto)movimientos.get("Salto");
                salto.reiniciar();
                this.movimientoActual = salto;
                this.animacionActual = this.animacionPersonaje.getJumpAnimation();
            }
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT) && Math.abs(body.getLinearVelocity().y) < 0.1f) {
            Dash dash = (Dash)movimientos.get("Dash");
            dash.setLadoDerecho(lado);
            dash.reiniciar();
            movimientoActual = dash;
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_RIGHT) && Math.abs(body.getLinearVelocity().y) < 0.1f) {
            Backdash backdash = (Backdash)movimientos.get("Backdash");
            backdash.setLadoDerecho(lado);
            backdash.reiniciar();
            movimientoActual = backdash;
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.M) && movimientoActual == null && Math.abs(body.getLinearVelocity().y) < 0.1f) {
            AtaqueBasico ataque = (AtaqueBasico)movimientos.get("Ataque");
            ataque.setLadoDerecho(lado);
            ataque.reiniciar();
            movimientoActual = ataque;
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
}