// PersonajeBase.java
package personajes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

import Interfaces.CambioVidaEventListener;
import Interfaces.MuerteEventListener;
import escenas.Arena;
import movimientos.Backdash;
import movimientos.Dash;
import movimientos.Morir;
import movimientos.MovimientoBase;
import movimientos.Salto;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class PersonajeBase extends Actor {
    protected float stateTime;
    protected Body body;
    protected boolean lado = true; // true = derecha, false = izquierda
    protected String nombre;
    protected int vida;
    private int vidaMaxima;
    protected int fuerzaSalto; // <- se usa para el salto base
    protected MovimientoBase movimientoActual;
    protected Map<String, MovimientoBase> movimientos = new HashMap<>();
    protected AnimacionBase animacionPersonaje;
    protected MuerteEventListener muerteEventListener;
    protected CambioVidaEventListener cambioVidaEventListener;
    protected Animation<TextureRegion> animacionActual;

    // ====== INPUT FLAGS (seteados por LectorInputs) ======
    protected boolean inLeft;
    protected boolean inRight;
    protected boolean inJump;
    protected boolean inAttack;
    protected boolean inDash;
    protected boolean inBackdash;

    public void setInputLeft(boolean v)    { this.inLeft = v; }
    public void setInputRight(boolean v)   { this.inRight = v; }
    public void requestJump()              { this.inJump = true; }
    public void requestAttack()            { this.inAttack = true; }
    public void requestDash()              { this.inDash = true; }
    public void requestBackdash()          { this.inBackdash = true; }

    // ====== Chequeo de “suelo” por estabilidad de velocidad Y ======
    private int  groundedFrames = 0;
    private boolean grounded     = false;
    public static final short CATEGORY_PERSONAJE = 0x0001;
    public static final short CATEGORY_ENTORNO   = 0x0002;
    public static final short CATEGORY_PROYECTIL = 0x0004; // si más adelante agregás


    protected float groundEps() { return 0.01f; }           // tolerancia de |vy|
    protected int   groundStableFrames() { return 3; }      // frames consecutivos para considerar suelo
    protected final boolean isGrounded() { return grounded; }

    public PersonajeBase(World world, String nombre, int vida,
                         MuerteEventListener muerteListener,
                         CambioVidaEventListener vidaListener,
                         AnimacionBase animacion, int fuerzaSalto) {
        this.nombre = nombre;
        this.vida = vida;
        this.vidaMaxima = vida;
        this.muerteEventListener = muerteListener;
        this.cambioVidaEventListener = vidaListener;
        this.animacionPersonaje = animacion;
        this.fuerzaSalto = fuerzaSalto; // <<< IMPORTANTE

        // Configuración física común
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(100 * Arena.PIXELS_TO_METERS, 200 * Arena.PIXELS_TO_METERS);
        bodyDef.fixedRotation = true;

        this.body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
            this.animacionPersonaje.getIdleAnimation().getKeyFrame(stateTime).getRegionWidth()  / 2f * Arena.PIXELS_TO_METERS,
            this.animacionPersonaje.getIdleAnimation().getKeyFrame(stateTime).getRegionHeight() / 2f * Arena.PIXELS_TO_METERS
        );

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0f; // para que no se queden pegados
        fixtureDef.filter.categoryBits = CATEGORY_PERSONAJE;
        // El personaje solo choca con el entorno (NO con otros personajes)
        fixtureDef.filter.maskBits = CATEGORY_ENTORNO;
        body.createFixture(fixtureDef);

        shape.dispose();

        setSize(
            this.animacionPersonaje.getIdleAnimation().getKeyFrame(stateTime).getRegionWidth(),
            this.animacionPersonaje.getIdleAnimation().getKeyFrame(stateTime).getRegionHeight()
        );

        body.setUserData(this);
        this.body.setSleepingAllowed(false);

        // Movimientos base
        movimientos.put("Dash",     new Dash(body, lado));
        movimientos.put("Salto",    new Salto(body, this.fuerzaSalto));
        movimientos.put("Backdash", new Backdash(body, lado));

        this.animacionActual = animacionPersonaje.getIdleAnimation();
    }

    public int getVidaMaxima() { return vidaMaxima; }

    // ================== ACT UNIFICADO ==================
    @Override
    public void act(float delta) {
        this.stateTime += delta;
        
      Collection< MovimientoBase> collecionMovimiento =  this.movimientos.values();
      MovimientoBase[] movimientosDisponibles = collecionMovimiento.toArray( new MovimientoBase[0]);
      
      for(MovimientoBase movimiento : movimientosDisponibles) {
    	  if(!movimiento.estaListo()) {
    		  movimiento.actualizarCooldown(delta);
    	  }
      }
      
      

        // Actualizar estado “en suelo” con filtro de estabilidad
        float vy = body.getLinearVelocity().y;
        if (Math.abs(vy) < groundEps()) {
            groundedFrames++;
            if (groundedFrames >= groundStableFrames()) grounded = true;
        } else {
            groundedFrames = 0;
            grounded = false;
        }

        if (this.vida > 0) {
            if (movimientoActual == null || movimientoActual.estaCompletado()) {

                float velX = 0f;

                // === Movimiento horizontal continuo (izq/der) ===
                if (inLeft && !inRight) {
                    velX = -getVelocidadHorizontal();
                    this.lado = false;
                    if (this.animacionActual != this.animacionPersonaje.getRunAnimation()) {
                        this.animacionActual = this.animacionPersonaje.getRunAnimation();
                    }
                } else if (inRight && !inLeft) {
                    velX =  getVelocidadHorizontal();
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
                body.setLinearVelocity(velX, body.getLinearVelocity().y);

                // === Acciones edge-trigger ===

                // Salto (solo si está apoyado)
                if (inJump) {
                    if (isGrounded()) {
                        Salto salto = (Salto) movimientos.get("Salto");
                        prepareSalto(salto);                 // hook por si el Jefe cambia la fuerza
                        salto.reiniciar();
                        this.movimientoActual = salto;
                        this.animacionActual = this.animacionPersonaje.getJumpAnimation();
                        onPlaySalto();                       // hook de sonido
                    }
                    inJump = false;
                }

                // Dash (solo si está apoyado)
                if (inDash) {
                    if (isGrounded()) {
                        Dash dash = (Dash) movimientos.get("Dash");
                        dash.setLadoDerecho(lado);
                        dash.reiniciar();
                        if(dash.estaListo()) {
                        movimientoActual = dash;
                        onPlayDash();
                        dash.activarCooldown();
                        }// hook de sonido
                    }
                    inDash = false;
                }

                // Backdash (solo si está apoyado)
                if (inBackdash) {
                    if (isGrounded()) {
                        Backdash back = (Backdash) movimientos.get("Backdash");
                        back.setLadoDerecho(lado);
                        back.reiniciar();
                        movimientoActual = back;
                        onPlayDash();                        // usa mismo sonido que dash por defecto
                    }
                    inBackdash = false;
                }

                // Ataque (solo si libre y apoyado)
                if (inAttack) {
                    if (movimientoActual == null && isGrounded()) {
                        MovimientoBase ataque = createAtaque();   // <-- definido por cada subclase
                        movimientoActual = ataque;
                        this.stateTime = 0;
                        onAttackAnimation();                      // hook para poner anim de ataque (Jefe)
                        onPlayGolpe();                            // hook de sonido
                    }
                    inAttack = false;
                }

                // Hook para acciones extra del hijo (p.ej. toggle bestia del Jefe)
                onExtraActions();
            }
        }

        // Actualización del movimiento en curso
        if (movimientoActual != null && !movimientoActual.estaCompletado()) {
            movimientoActual.actualizar();
            movimientoActual.aplicarEfecto();
        } else {
            movimientoActual = null;
        }

        // Mantener posición respecto al body
        setPosition(
            (body.getPosition().x / Arena.PIXELS_TO_METERS) - getWidth()  / 2f,
            (body.getPosition().y / Arena.PIXELS_TO_METERS) - getHeight() / 2f
        );
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = animacionActual.getKeyFrame(stateTime, true);
        Color color = getColor();
        batch.setColor(color);

        float frameWidth  = currentFrame.getRegionWidth();
        float frameHeight = currentFrame.getRegionHeight();

        if (getWidth() != frameWidth || getHeight() != frameHeight) {
            setSize(frameWidth, frameHeight);
        }

        if (!lado) currentFrame.flip(true, false);

        batch.draw(
            currentFrame,
            body.getPosition().x / Arena.PIXELS_TO_METERS - frameWidth  / 2f,
            body.getPosition().y / Arena.PIXELS_TO_METERS - frameHeight / 2f,
            frameWidth, frameHeight
        );

        if (!lado) currentFrame.flip(true, false);
        batch.setColor(Color.WHITE);
    }

    public boolean esInvulnerable() {
        return movimientoActual != null
                && (movimientoActual.getNombre().equals("Dash") || movimientoActual.getNombre().equals("Backdash"))
                && movimientoActual.estaEnFramesActivos();
    }

    public void recibirDaño(final int DAÑO) {
        this.vida -= DAÑO;
        this.cambioVidaEventListener.onCambioVida(this);
        if (this.vida <= 0) {
            this.vida = 0;
            this.movimientoActual = new Morir(this);
            if (this.animacionPersonaje.getAnimacionMuerte() != null) {
                this.animacionActual = this.animacionPersonaje.getAnimacionMuerte();
            }
        }
    }

    public Body getBody() { return body; }
    public int  getVida() { return this.vida; }
    public void setVida(int vida) { this.vida = vida; }
    public String getNombre() { return this.nombre; }
    public boolean getLado() { return this.lado; }
    public AnimacionBase getAnimacionPersonaje() { return this.animacionPersonaje; }
    public MuerteEventListener getMuerteEventListener() { return this.muerteEventListener; }

    // ================== HOOKS/CONTRATOS PARA SUBCLASES ==================

    /** Velocidad horizontal base: Personaje = 5f, Jefe = (bestia?4:2) */
    protected abstract float getVelocidadHorizontal();

    /** Crear el movimiento de ataque correcto (Personaje: AtaqueBasico, Jefe: AtaqueJefe). */
    protected abstract MovimientoBase createAtaque();

    /** Permitir al hijo ajustar la fuerza del salto antes de reiniciar (Jefe). */
    protected void prepareSalto(Salto salto) {
        // Por defecto, usa fuerzaSalto ya cargada en el constructor
        salto.setFuerza(this.fuerzaSalto);
    }

    /** Sonidos (opcional) */
    protected void onPlayDash()  {}
    protected void onPlaySalto() {}
    protected void onPlayGolpe() {}

    /** Animación de ataque (Jefe la usa; Personaje puede ignorar) */
    protected void onAttackAnimation() {}

    /** Acciones extra por frame (p.ej. toggle de modo bestia del Jefe) */
    protected void onExtraActions() {}
}
