package personajes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import Interfaces.CambioVidaEventListener;
import Interfaces.MuerteEventListener;

/**
 * Simple render-only character that only applies remote state snapshots.
 */
public abstract class PersonajeBase extends Actor {
    protected float stateTime;
    protected boolean lado = true; // true = derecha, false = izquierda
    private String nombre;
    private int vida;
    private int vidaMaxima;
    protected AnimacionBase animacionPersonaje;
    private final MuerteEventListener muerteEventListener;
    private final CambioVidaEventListener cambioVidaEventListener;
    protected Animation<TextureRegion> animacionActual;
    protected Estadistica estadisticas = new Estadistica();
    private boolean remoteControlled = false;

    // Position in screen units (center based)
    private float posX;
    private float posY;

    public PersonajeBase(String nombre, int vida,
                         MuerteEventListener muerteListener,
                         CambioVidaEventListener vidaListener,
                         AnimacionBase animacion,
                         Estadistica estadisticas) {

        this.estadisticas = estadisticas;
        this.nombre = nombre;
        this.vida = vida * (int) this.estadisticas.getMultVida();
        this.vidaMaxima = vida * (int) this.estadisticas.getMultVida();
        this.muerteEventListener = muerteListener;
        this.cambioVidaEventListener = vidaListener;
        this.animacionPersonaje = animacion;

        this.animacionActual = animacionPersonaje.getIdleAnimation();
        TextureRegion frame = animacionActual.getKeyFrame(stateTime, true);
        setSize(frame.getRegionWidth(), frame.getRegionHeight());
    }

    public int getVidaMaxima() { return vidaMaxima; }

    @Override
    public void act(float delta) {
        if (remoteControlled) {
            return;
        }
        this.stateTime += delta;
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

        if (!lado) {
            currentFrame.flip(true, false);
        }

        batch.draw(
                currentFrame,
                posX - frameWidth / 2f,
                posY - frameHeight / 2f,
                frameWidth, frameHeight
        );

        if (!lado) {
            currentFrame.flip(true, false);
        }
        batch.setColor(Color.WHITE);
    }

    public boolean esInvulnerable() {
        return false;
    }

    public void recibirDaño(final int danio) {
        this.vida -= danio;
        if (this.cambioVidaEventListener != null) {
            this.cambioVidaEventListener.onCambioVida(this);
        }
        if (this.vida <= 0) {
            this.vida = 0;
            if (this.muerteEventListener != null) {
                this.muerteEventListener.onPersonajeMuerto(this);
            }
            if (this.animacionPersonaje.getAnimacionMuerte() != null) {
                this.animacionActual = this.animacionPersonaje.getAnimacionMuerte();
            }
        }
    }

    public int  getVida() { return this.vida; }
    public void setVida(int vida) { this.vida = vida; }
    public String getNombre() { return this.nombre; }
    public boolean getLado() { return this.lado; }
    public AnimacionBase getAnimacionPersonaje() { return this.animacionPersonaje; }
    public MuerteEventListener getMuerteEventListener() { return this.muerteEventListener; }

    public boolean isRemoteControlled() { return remoteControlled; }

    public void setRemoteControlled(boolean remoteControlled) {
        this.remoteControlled = remoteControlled;
    }

    @Override
    public float getX() {
        return this.posX;
    }

    @Override
    public float getY() {
        return this.posY;
    }

    public float getStateTime() {
        return this.stateTime;
    }

    public String getAnimacionActualNombre() {
        if (this.animacionActual == this.animacionPersonaje.getRunAnimation()) {
            return "run";
        }
        if (this.animacionActual == this.animacionPersonaje.getJumpAnimation()) {
            return "jump";
        }
        if (this.animacionActual == this.animacionPersonaje.getAnimacionAtaque()) {
            return "attack";
        }
        if (this.animacionPersonaje.getAnimacionMuerte() != null
                && this.animacionActual == this.animacionPersonaje.getAnimacionMuerte()) {
            return "death";
        }
        return "idle";
    }

    public void applyRemoteState(float x, float y, boolean facingRight, String animationKey,
                                 float remoteStateTime, int vidaActual, int vidaMaxima) {
        this.remoteControlled = true;
        this.posX = x;
        this.posY = y;
        setPosition(posX - getWidth() / 2f, posY - getHeight() / 2f);
        this.lado = facingRight;
        this.stateTime = remoteStateTime;
        this.vida = vidaActual;
        this.vidaMaxima = vidaMaxima;
        this.animacionActual = resolveAnimation(animationKey);
    }

    private Animation<TextureRegion> resolveAnimation(String key) {
        if (key == null) {
            return this.animacionPersonaje.getIdleAnimation();
        }
        switch (key) {
            case "run":
                return this.animacionPersonaje.getRunAnimation();
            case "jump":
                return this.animacionPersonaje.getJumpAnimation();
            case "attack":
                return this.animacionPersonaje.getAnimacionAtaque();
            case "attack-Vertical":
                return this.animacionPersonaje.getVerticalAttackAnimation();
            case "death":
                return this.animacionPersonaje.getAnimacionMuerte() != null
                        ? this.animacionPersonaje.getAnimacionMuerte()
                        : this.animacionPersonaje.getIdleAnimation();
            default:
                return this.animacionPersonaje.getIdleAnimation();
        }
    }

    public Estadistica getEstadistica() {
        return this.estadisticas;
    }

    public void applyCooldowns(String cooldownsStr) {
        // Cooldowns are handled server-side; no-op in render-only client.
    }
}
