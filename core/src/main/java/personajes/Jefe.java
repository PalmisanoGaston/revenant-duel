// Jefe.java
package personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.World;

import Interfaces.CambioVidaEventListener;
import Interfaces.MuerteEventListener;
import escenas.Arena;
import movimientos.AtaqueJefe;
import movimientos.Backdash;
import movimientos.Dash;
import movimientos.Salto;
import sonidos.SonidosJefe;

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

        float velocidadActual   = modoBestia ? velocidadBestia : velocidadNormal;
        int   fuerzaSaltoActual = modoBestia ? fuerzaSaltoBestia : fuerzaSaltoNormal;

        if (this.vida > 0) {
            if (movimientoActual == null || movimientoActual.estaCompletado()) {

                float velocidadX = 0f;

                // === Movimiento horizontal continuo (LEFT/RIGHT) ===
                if (inLeft && !inRight) {
                    velocidadX = -velocidadActual;
                    this.lado = false;
                    if (this.animacionActual != this.animacionPersonaje.getRunAnimation()) {
                        this.animacionActual = this.animacionPersonaje.getRunAnimation();
                    }
                } else if (inRight && !inLeft) {
                    velocidadX = velocidadActual;
                    this.lado = true;
                    if (this.animacionActual != this.animacionPersonaje.getRunAnimation()) {
                        this.animacionActual = this.animacionPersonaje.getRunAnimation();
                    }
                } else {
                    if (this.animacionActual != this.animacionPersonaje.getIdleAnimation()) {
                        this.animacionActual = this.animacionPersonaje.getIdleAnimation();
                    }
                }

                // Aplicar velocidad horizontal (conservando Y)
                body.setLinearVelocity(velocidadX, body.getLinearVelocity().y);

                // === Acciones edge-trigger (UP / SHIFT_RIGHT / CONTROL_RIGHT / M / H) ===
                if (inJump) {
                    movimientoActual = new Salto(body, fuerzaSaltoActual);
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
                    movimientoActual = new AtaqueJefe(body, lado);
                    this.animacionActual = this.animacionPersonaje.getAnimacionAtaque();
                    inAttack = false;
                }

                if (inToggleBestia) {
                    // Usamos tu método para activar el modo bestia (y color)
                    this.modoBestia();
                    inToggleBestia = false;
                }
            }
        }

        if (movimientoActual != null && !movimientoActual.estaCompletado()) {
            movimientoActual.actualizar();
            movimientoActual.aplicarEfecto();
        } else {
            movimientoActual = null;
        }

        setPosition(
            (body.getPosition().x / Arena.PIXELS_TO_METERS) - getWidth() / 2,
            (body.getPosition().y / Arena.PIXELS_TO_METERS) - getHeight() / 2
        );
    }
    
    
    public void modoBestia() {
    	this.modoBestia = true;
    	this.setColor(colorBestia);
    }
}