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
    // ====== INPUT FLAGS (seteados por LectorInputs) ======
    private boolean inLeft;
    private boolean inRight;
    private boolean inJump;
    private boolean inDash;
    private boolean inBackdash;
    private boolean inAttack;
    private boolean inToggleBestia;

    public void setInputLeft(boolean v)  { this.inLeft = v; }
    public void setInputRight(boolean v) { this.inRight = v; }
    public void requestJump()            { this.inJump = true; }
    public void requestDash()            { this.inDash = true; }
    public void requestBackdash()        { this.inBackdash = true; }
    public void requestAttack()          { this.inAttack = true; }
    public void requestToggleBestia()    { this.inToggleBestia = true; }






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

                // === Movimiento horizontal continuo (LEFT / RIGHT) ===
                if (inLeft && !inRight) {
                    velocidadX = -velocidadActual;
                    this.lado = false;
                    if (this.animacionActual != this.animacionPersonaje.getRunAnimation()) {
                        this.animacionActual = this.animacionPersonaje.getRunAnimation();
                    }
                } else if (inRight && !inLeft) {
                    velocidadX =  velocidadActual;
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

                // === Acciones edge-trigger disparadas por LectorInputs ===

                // Saltar (solo si "apoyado": velY casi cero)
                if (inJump) {
                    if (Math.abs(body.getLinearVelocity().y) < 0.1f) {
                        Salto salto = (Salto) movimientos.get("Salto");
                        salto.setFuerza(fuerzaSaltoActual);
                        salto.reiniciar();
                        this.movimientoActual = salto;
                        this.animacionActual = this.animacionPersonaje.getJumpAnimation();
                        this.sonidos.playSalto();
                    }
                    inJump = false; // consumir
                }

                // Dash (solo si velY ~ 0)
                if (inDash) {
                    if (Math.abs(body.getLinearVelocity().y) < 0.1f) {
                        Dash dash = (Dash) movimientos.get("Dash");
                        dash.setLadoDerecho(lado);
                        dash.reiniciar();
                        movimientoActual = dash;
                        this.sonidos.playDash();
                    }
                    inDash = false;
                }

                // Backdash (solo si velY ~ 0)
                if (inBackdash) {
                    if (Math.abs(body.getLinearVelocity().y) < 0.1f) {
                        Backdash backdash = (Backdash) movimientos.get("Backdash");
                        backdash.setLadoDerecho(lado);
                        backdash.reiniciar();
                        movimientoActual = backdash;
                        this.sonidos.playDash();
                    }
                    inBackdash = false;
                }

                // Ataque (solo si libre y velY ~ 0)
                if (inAttack) {
                    if (movimientoActual == null && Math.abs(body.getLinearVelocity().y) < 0.1f) {
                        AtaqueJefe ataque = (AtaqueJefe) movimientos.get("Ataque");
                        ataque.setLadoDerecho(lado);
                        ataque.reiniciar();
                        movimientoActual = ataque;
                        this.stateTime = 0;
                        this.animacionActual = this.animacionPersonaje.getAnimacionAtaque();
                        this.sonidos.playGolpe();
                    }
                    inAttack = false;
                }

                // Toggle modo bestia (si lo mapeaste en el Lector)
                if (inToggleBestia) {
                    this.modoBestia();
                    inToggleBestia = false;
                }
            }
        }

        // Actualizar movimiento en curso
        if (movimientoActual != null && !movimientoActual.estaCompletado()) {
            movimientoActual.actualizar();
            movimientoActual.aplicarEfecto();
        } else {
            movimientoActual = null;
        }

        // Mantener posición con respecto al body
        setPosition(
                (body.getPosition().x / Arena.PIXELS_TO_METERS) - getWidth() / 2f,
                (body.getPosition().y / Arena.PIXELS_TO_METERS) - getHeight() / 2f
        );
    }



    public void modoBestia() {
    	this.modoBestia = true;
    	this.setColor(colorBestia);
    }
}