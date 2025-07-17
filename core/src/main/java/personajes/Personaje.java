package personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

import io.github.revenantduel.Arena;

public class Personaje extends Actor {
    private Texture skin;
    private Body body;
    
    public Personaje(World world) {
        this.skin = new Texture("personajes/idle.png");
        
        // Crear definición del cuerpo
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(100 * Arena.PIXELS_TO_METERS, 200 * Arena.PIXELS_TO_METERS);
        bodyDef.fixedRotation = true;
        
        // Crear el cuerpo en el mundo
        this.body = world.createBody(bodyDef);
        
        // Definir la forma (hitbox)
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
            skin.getWidth()/2 * Arena.PIXELS_TO_METERS,
            skin.getHeight()/2 * Arena.PIXELS_TO_METERS
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
        setSize(skin.getWidth(), skin.getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Dibujar la textura en la posición del cuerpo físico
        batch.draw(
            skin,
            body.getPosition().x / Arena.PIXELS_TO_METERS - getWidth()/2,
            body.getPosition().y / Arena.PIXELS_TO_METERS - getHeight()/2,
            getWidth(),
            getHeight()
        );
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        
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
            // Verificar si está en el suelo (podrías hacer esto con un rayo o contacto)
            if (Math.abs(body.getLinearVelocity().y) < 0.1f) {
                body.applyLinearImpulse(
                    new Vector2(0, 2.5f),
                    body.getWorldCenter(),
                    true
                );
            }
        }
        
        // Actualizar posición del Actor para que coincida con el cuerpo físico
        setPosition(
            (body.getPosition().x / Arena.PIXELS_TO_METERS) - getWidth()/2,
            (body.getPosition().y / Arena.PIXELS_TO_METERS) - getHeight()/2
        );
    }

    @Override
    public boolean remove() {
        skin.dispose();
        return super.remove();
    }
}