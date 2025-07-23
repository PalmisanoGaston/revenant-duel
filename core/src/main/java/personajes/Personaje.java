package personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import Interfaces.CambioVidaEventListener;
import Interfaces.MuerteEventListener;

import java.util.HashMap;
import java.util.Map;
import io.github.revenantduel.Arena;
import movimientos.AtaqueBasico;
import movimientos.Backdash;
import movimientos.Dash;
import movimientos.MovimientoBase;
import movimientos.Salto;

public class Personaje extends Actor {

    private float stateTime;
    private Body body;
    private boolean lado = true;
    private String nombre = "Jugador";
    private int vida = 100;
    private MovimientoBase movimientoActual;
    private Map<String, MovimientoBase> movimientos = new HashMap<>();
    private AnimacionesPersonaje animacionPersonaje = new AnimacionesPersonaje();
    private MuerteEventListener muerteEventListener;
    private CambioVidaEventListener cambioVidaEventListener;

    private Animation<TextureRegion> animacionActual;

    public Personaje(World world, MuerteEventListener muerteListener, CambioVidaEventListener vidaListener) {
        
        // Crear definición del cuerpo
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(100 * Arena.PIXELS_TO_METERS, 200 * Arena.PIXELS_TO_METERS);
        bodyDef.fixedRotation = true;
        
        // Crear el cuerpo en el mundo
        this.body = world.createBody(bodyDef);
        
        // Definir la forma (hitbox) - ahora usamos frameWidth en lugar de skin.getWidth()
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
            this.animacionPersonaje.getFRAME_WIDTH()/2 * Arena.PIXELS_TO_METERS,
            this.animacionPersonaje.getFRAME_HEIGTH()/2 * Arena.PIXELS_TO_METERS
        );
        
        // Definir propiedades físicas
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.1f;
        
        // Añadir forma al cuerpo
        body.createFixture(fixtureDef);
        
        // Liberar la forma
        shape.dispose();
        
        // Configurar tamaño del Actor
        int frameHeigth = this.animacionPersonaje.getFRAME_HEIGTH();
        int frameWidth = this.animacionPersonaje.getFRAME_WIDTH();
        
        setSize(frameWidth, frameHeigth);
        
        
        movimientos.put("Dash",new Dash(body,lado));
        movimientos.put("Salto", new Salto(body));
        movimientos.put("Backdash", new Backdash(body,lado));
        movimientos.put("Ataque", new AtaqueBasico(body, lado));

        this.muerteEventListener = muerteListener;
        this.cambioVidaEventListener = vidaListener;
        body.setUserData(this);

    }

    public Body getBody() {
		return body;
	}

	@Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = this.animacionActual.getKeyFrame(stateTime, true);
        
        // Invertir la imagen si mira a la izquierda
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
        
        // Volver a invertir para el siguiente frame si es necesario
        if (!lado) {
            currentFrame.flip(true, false);
        }
    }
    
    @Override
    public void act(float delta) {
        super.act(delta);
        
        // Actualizar el tiempo de animación
        stateTime += delta;
        
        if (movimientoActual == null || movimientoActual.estaCompletado()) {
            // Movimiento horizontal normal
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
            }
            else {
            	if(this.animacionActual != this.animacionPersonaje.getIdleAnimation()) {
            		this.animacionActual = this.animacionPersonaje.getIdleAnimation();	
            	}
            }
            
            body.setLinearVelocity(velocidadX, body.getLinearVelocity().y);
            
            // Salto
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && (Math.abs(body.getLinearVelocity().y) < 0.1f))  {
	        	Salto salto = (Salto)movimientos.get("Salto");
	        	salto.reiniciar();
	        	this.movimientoActual = salto;
	        	
	        	if(this.animacionActual != this.animacionPersonaje.getJumpAnimation()) {
	        		this.animacionActual = this.animacionPersonaje.getJumpAnimation();	
	        	}
            }

        }
        
        // Iniciar dash
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
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.J) && movimientoActual == null && Math.abs(body.getLinearVelocity().y) < 0.1f ) { // Tecla J para atacar
            AtaqueBasico ataque = (AtaqueBasico)movimientos.get("Ataque");
            ataque.setLadoDerecho(lado);
            ataque.reiniciar();
            movimientoActual = ataque;
        }
        
    
    // Actualizar movimiento especial si existe
    if (movimientoActual != null && !movimientoActual.estaCompletado()) {
        movimientoActual.actualizar();
        movimientoActual.aplicarEfecto();
    } else {
        movimientoActual = null;
    }
        
        
        // Actualizar posición del Actor
        setPosition(
            (body.getPosition().x / Arena.PIXELS_TO_METERS) - getWidth()/2,
            (body.getPosition().y / Arena.PIXELS_TO_METERS) - getHeight()/2
        );
    }

    @Override
    public boolean remove() {
        return super.remove();
    }
    
    public void recibirDaño(final int DAÑO) {
    	this.vida -= DAÑO;
    	this.cambioVidaEventListener.onCambioVida(this);
    	if(this.vida<= 0) {
    		System.out.println("Moriste crack");
    		if(this.muerteEventListener != null) {
    			this.muerteEventListener.onPersonajeMuerto(this);
            }
            this.remove();
    	}
    }
    
    
    public int getVida() {
    	return this.vida;
    }
    
    public String getNombre() {
    	return this.nombre;
    }
}