// PersonajeBase.java
package personajes;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

import Interfaces.CambioVidaEventListener;
import Interfaces.MuerteEventListener;
import io.github.revenantduel.Arena;
import movimientos.MovimientoBase;

import java.util.HashMap;
import java.util.Map;

public abstract class PersonajeBase extends Actor {
    protected float stateTime;
    protected Body body;
    protected boolean lado = true; // true = derecha, false = izquierda
    protected String nombre;
    protected int vida;
    protected MovimientoBase movimientoActual;
    protected Map<String, MovimientoBase> movimientos = new HashMap<>();
    protected AnimacionesPersonaje animacionPersonaje;
    protected MuerteEventListener muerteEventListener;
    protected CambioVidaEventListener cambioVidaEventListener;
    protected Animation<TextureRegion> animacionActual;

    public PersonajeBase(World world, String nombre, int vida, 
                        MuerteEventListener muerteListener, 
                        CambioVidaEventListener vidaListener) {
        this.nombre = nombre;
        this.vida = vida;
        this.muerteEventListener = muerteListener;
        this.cambioVidaEventListener = vidaListener;
        this.animacionPersonaje = new AnimacionesPersonaje();
        
        // Configuración física común
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(100 * Arena.PIXELS_TO_METERS, 200 * Arena.PIXELS_TO_METERS);
        bodyDef.fixedRotation = true;
        
        this.body = world.createBody(bodyDef);
        
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
            this.animacionPersonaje.getFRAME_WIDTH()/2 * Arena.PIXELS_TO_METERS,
            this.animacionPersonaje.getFRAME_HEIGTH()/2 * Arena.PIXELS_TO_METERS
        );
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.1f;
        
        body.createFixture(fixtureDef);
        shape.dispose();
        
        setSize(animacionPersonaje.getFRAME_WIDTH(), animacionPersonaje.getFRAME_HEIGTH());
        body.setUserData(this);
    }

    @Override
    public abstract void act(float delta);

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = animacionActual.getKeyFrame(stateTime, true);
        
        if (!lado) {
            currentFrame.flip(true, false);
        }
        
        batch.draw(
            currentFrame,
            body.getPosition().x / Arena.PIXELS_TO_METERS - getWidth()/2,
            body.getPosition().y / Arena.PIXELS_TO_METERS - getHeight()/2,
            getWidth(),
            getHeight()
        );
        
        if (!lado) {
            currentFrame.flip(true, false);
        }
    }

    public void recibirDaño(final int DAÑO) {
        this.vida -= DAÑO;
        this.cambioVidaEventListener.onCambioVida(this);
        if(this.vida <= 0) {
            this.vida = 0;
            if(this.muerteEventListener != null) {
                this.muerteEventListener.onPersonajeMuerto(this);
            }
            this.remove();
        }
    }

    // Getters comunes
    public Body getBody() { return body; }
    public int getVida() { return this.vida; }
    public String getNombre() { return this.nombre; }
    public boolean getLado() { return this.lado; }
}