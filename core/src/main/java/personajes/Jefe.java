// Jefe.java
package personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.World;

import Interfaces.CambioVidaEventListener;
import Interfaces.MuerteEventListener;
import io.github.revenantduel.Arena;
import movimientos.AtaqueJefe;
import movimientos.Backdash;
import movimientos.Dash;
import movimientos.Salto;
import sonido.SonidosJefe;

public class Jefe extends PersonajeBase {
	
	private boolean modoBestia = false;
	private float velocidadNormal = 2f;
	private float velocidadBestia = 4f;
	private int fuerzaSaltoNormal = 10;
	private int fuerzaSaltoBestia = 20;

	private Color colorBestia = new Color(1f, 0.5f, 0.5f, 1f);
	private SonidosJefe sonidos = new SonidosJefe();
	

    public Jefe(World world, MuerteEventListener muerteListener,  CambioVidaEventListener vidaListener) {
        super(world, "Jefe", 150, muerteListener, vidaListener, new AnimacionesJefe());
        
        movimientos.put("Dash", new Dash(body, lado));
        movimientos.put("Salto", new Salto(body,10));
        movimientos.put("Backdash", new Backdash(body, lado));
        movimientos.put("Ataque", new AtaqueJefe(body, lado));


        this.animacionActual = animacionPersonaje.getIdleAnimation();
    }

    @Override
    public void act(float delta) {
        super.stateTime += delta;
        
        float velocidadActual = modoBestia ? velocidadBestia : velocidadNormal;
        int fuerzaSaltoActual = modoBestia ? fuerzaSaltoBestia : fuerzaSaltoNormal;

        
        if (movimientoActual == null || movimientoActual.estaCompletado()) {
            float velocidadX = 0;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                velocidadX = -velocidadActual;
                this.lado = false;
                if(this.animacionActual != this.animacionPersonaje.getRunAnimation()) {
                    this.animacionActual = this.animacionPersonaje.getRunAnimation();    
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                velocidadX = velocidadActual;
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
                salto.setFuerza(fuerzaSaltoActual);
                salto.reiniciar();
                this.movimientoActual = salto;
                this.animacionActual = this.animacionPersonaje.getJumpAnimation();
                this.sonidos.playSalto();
            }
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT) && Math.abs(body.getLinearVelocity().y) < 0.1f) {
            Dash dash = (Dash)movimientos.get("Dash");
            dash.setLadoDerecho(lado);
            dash.reiniciar();
            movimientoActual = dash;
            this.sonidos.playDash();
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_RIGHT) && Math.abs(body.getLinearVelocity().y) < 0.1f) {
            Backdash backdash = (Backdash)movimientos.get("Backdash");
            backdash.setLadoDerecho(lado);
            backdash.reiniciar();
            movimientoActual = backdash;
            this.sonidos.playDash();
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.M) && movimientoActual == null && Math.abs(body.getLinearVelocity().y) < 0.1f) {
            AtaqueJefe ataque = (AtaqueJefe)movimientos.get("Ataque");
            ataque.setLadoDerecho(lado);
            ataque.reiniciar();
            movimientoActual = ataque;
            this.stateTime = 0;
            this.animacionActual = this.animacionPersonaje.getAnimacionAtaque();
            this.sonidos.playGolpe();
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
    
    
    public void modoBestia() {
    	this.modoBestia = true;
    	this.setColor(colorBestia);
    }
}