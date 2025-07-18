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

import io.github.revenantduel.Arena;

public class Personaje extends Actor {
    private Texture spriteSheet;
    private Animation<TextureRegion> idleAnimation;
    private float stateTime;
    private Body body;
    
    public Personaje(World world) {
        // Cargar el sprite sheet que contiene todos los frames de animación
        this.spriteSheet = new Texture("personajes/idle.png");
        
        // Dividir el sprite sheet en frames individuales
        int frameWidth = spriteSheet.getWidth() / 4; // Suponiendo 4 frames horizontales
        int frameHeight = spriteSheet.getHeight();
        
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(spriteSheet, i * frameWidth, 0, frameWidth, frameHeight));
        }
        
        // Crear la animación con los frames (0.1f es el tiempo entre frames)
        idleAnimation = new Animation<>(0.1f, frames);
        stateTime = 0f;
        
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
            frameWidth/2 * Arena.PIXELS_TO_METERS,
            frameHeight/2 * Arena.PIXELS_TO_METERS
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
        setSize(frameWidth, frameHeight);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Obtener el frame actual de la animación
        TextureRegion currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        
        // Dibujar el frame actual
        batch.draw(
            currentFrame,
            body.getPosition().x / Arena.PIXELS_TO_METERS - getWidth()/2,
            body.getPosition().y / Arena.PIXELS_TO_METERS - getHeight()/2,
            getWidth(),
            getHeight()
        );
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        
        // Actualizar el tiempo de animación
        stateTime += delta;
        
        // Movimiento horizontal
        float velocidadX = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocidadX = -5f;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocidadX = 5f;
        }
        
        // Aplicar velocidad
        body.setLinearVelocity(velocidadX, body.getLinearVelocity().y);
        
        // Salto
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (Math.abs(body.getLinearVelocity().y) < 0.1f) {
                body.applyLinearImpulse(
                    new Vector2(0, 0.7f),
                    body.getWorldCenter(),
                    true
                );
            }
        }
        
        // Actualizar posición del Actor
        setPosition(
            (body.getPosition().x / Arena.PIXELS_TO_METERS) - getWidth()/2,
            (body.getPosition().y / Arena.PIXELS_TO_METERS) - getHeight()/2
        );
    }

    @Override
    public boolean remove() {
        spriteSheet.dispose();
        return super.remove();
    }
}