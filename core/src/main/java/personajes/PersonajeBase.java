// PersonajeBase.java
package personajes;

import com.badlogic.gdx.graphics.Color;
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
import escenas.Arena;
import movimientos.Morir;
import movimientos.MovimientoBase;

import java.util.HashMap;
import java.util.Map;

public abstract class PersonajeBase extends Actor {
    protected float stateTime;
    protected Body body;
    protected boolean lado = true; // true = derecha, false = izquierda
    protected String nombre;
    protected int vida;
    private int vidaMaxima;
    protected MovimientoBase movimientoActual;
    protected Map<String, MovimientoBase> movimientos = new HashMap<>();
    protected AnimacionBase animacionPersonaje;
    protected MuerteEventListener muerteEventListener;
    protected CambioVidaEventListener cambioVidaEventListener;
    protected Animation<TextureRegion> animacionActual;

    public PersonajeBase(World world, String nombre, int vida,  MuerteEventListener muerteListener,   CambioVidaEventListener vidaListener, AnimacionBase animacion) {
        this.nombre = nombre;
        this.vida = vida;
        this.vidaMaxima = vida;
        this.muerteEventListener = muerteListener;
        this.cambioVidaEventListener = vidaListener;
        this.animacionPersonaje = animacion;
        
        // Configuración física común
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(100 * Arena.PIXELS_TO_METERS, 200 * Arena.PIXELS_TO_METERS);
        bodyDef.fixedRotation = true;
        
        this.body = world.createBody(bodyDef);
        
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
            this.animacionPersonaje.getIdleAnimation().getKeyFrame(stateTime).getRegionWidth()/2 * Arena.PIXELS_TO_METERS,
            this.animacionPersonaje.getIdleAnimation().getKeyFrame(stateTime).getRegionHeight()/2 * Arena.PIXELS_TO_METERS
        );
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.1f;
        
        body.createFixture(fixtureDef);
        shape.dispose();
        
        setSize(this.animacionPersonaje.getIdleAnimation().getKeyFrame(stateTime).getRegionWidth(),this.animacionPersonaje.getIdleAnimation().getKeyFrame(stateTime).getRegionHeight());
        body.setUserData(this);
        this.body.setSleepingAllowed(false);

    }

	public int getVidaMaxima() {
		return vidaMaxima;
	}


	@Override
    public abstract void act(float delta);

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = animacionActual.getKeyFrame(stateTime, true);
        Color color = getColor();
        batch.setColor(color);
        // Obtener dimensiones actuales del frame
        float frameWidth = currentFrame.getRegionWidth();
        float frameHeight = currentFrame.getRegionHeight();
        
        // Actualizar tamaño del actor si es diferente
        if (getWidth() != frameWidth || getHeight() != frameHeight) {
            setSize(frameWidth, frameHeight);
        }
        
        if (!lado) {
            currentFrame.flip(true, false);
        }
        
        batch.draw(
            currentFrame,
            body.getPosition().x / Arena.PIXELS_TO_METERS - frameWidth/2,
            body.getPosition().y / Arena.PIXELS_TO_METERS - frameHeight/2,
            frameWidth,
            frameHeight
        );
        
        if (!lado) {
            currentFrame.flip(true, false);
        }
        batch.setColor(Color.WHITE);
    }

    
    public boolean esInvulnerable() {
        return movimientoActual != null &&  (movimientoActual.getNombre().equals("Dash") || movimientoActual.getNombre().equals("Backdash")) && movimientoActual.estaEnFramesActivos();
    }
    
    
    public void recibirDaño(final int DAÑO) {
        this.vida -= DAÑO;
        this.cambioVidaEventListener.onCambioVida(this);
        if(this.vida <= 0) {
            this.vida = 0;
            
        	this.movimientoActual = new Morir(this);
            if(this.animacionPersonaje.getAnimacionMuerte() != null) {
            	this.animacionActual = this.animacionPersonaje.getAnimacionMuerte();
            }
        }
    }

    public Body getBody() {
    	return body; 
    	}
    
    public int getVida() { 
    	return this.vida; 
    	}

    public void setVida(int vida) {
		this.vida = vida;
	}
    
    public String getNombre() {
    	return this.nombre; 
    }
    
    public boolean getLado() {
    	return this.lado;
    	}
    
    public AnimacionBase getAnimacionPersonaje() {
    	return this.animacionPersonaje;
    }
    
    public MuerteEventListener getMuerteEventListener() {
    	return this.muerteEventListener;
    }
}