package io.github.revenantduel;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import personajes.Personaje;
import utiles.Colision;

public class Arena implements Screen {
    private Principal juego;
    private Stage escena;
    private SpriteBatch batch;
    private Texture texturaBloque;
    
    // Variables Box2D
    private World world;
    private Box2DDebugRenderer debugRenderer;
    
    // Constantes de conversión
    public static final float PIXELS_TO_METERS = 1/100f; // 100 píxeles = 1 metro
    
    private static final int ANCHO = 800;
    private static final int ALTO = 800;    
    
    public Arena(Principal juego) {
        this.juego = juego;
        this.batch = new SpriteBatch();
        this.texturaBloque = new Texture("tileset.png");
        
        // Crear mundo Box2D con gravedad (en este caso, -10 en el eje Y)
        world = new World(new Vector2(0, -10), true);
        debugRenderer = new Box2DDebugRenderer();
        
        // Crear escena
        this.escena = new Stage(new ExtendViewport(ANCHO, ALTO));
        
        // Crear piso y plataformas
        crearPiso();
        crearPlataformas();
        
        // Crear personaje
        crearPersonaje();
        crearCajaSensor();
        world.setContactListener(new Colision());


        
        crearLimitesMapa(); 
    }
    
    private void crearLimitesMapa() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        // Definir vértices del marco (en metros)
        float margin = 10f; // Margen para evitar fugas
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2((-margin * PIXELS_TO_METERS)+0.08f , -margin * PIXELS_TO_METERS); // Esquina inferior izquierda
        vertices[1] = new Vector2((-margin * PIXELS_TO_METERS)+0.08f, (ALTO + margin) * PIXELS_TO_METERS); // Esquina superior izquierda
        vertices[2] = new Vector2((ANCHO + margin)*1.88f * PIXELS_TO_METERS, (ALTO + margin) * PIXELS_TO_METERS); // Esquina superior derecha
        vertices[3] = new Vector2((ANCHO + margin)*1.88f * PIXELS_TO_METERS, -margin * PIXELS_TO_METERS); // Esquina inferior derecha

        ChainShape shape = new ChainShape();
        shape.createLoop(vertices);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.0f;
        body.createFixture(fixtureDef);

        shape.dispose();
    }
    
    private void crearPiso() {
        // Definir cuerpo físico
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, 0);
        
        // Crear cuerpo en el mundo
        Body body = world.createBody(bodyDef);
        
        // Definir forma (una caja de 10 bloques de ancho y 1 de alto)
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(this.ANCHO , 64 * PIXELS_TO_METERS);
        
        // Definir propiedades físicas
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.3f;
        
        // Añadir forma al cuerpo
        body.createFixture(fixtureDef);
        
        // Liberar la forma
        shape.dispose();
    }
    
    private void crearPlataformas() {
        // Plataforma 1
        crearPlataforma(200, 150, 128, 64);
        
        // Plataforma 2
        crearPlataforma(400, 250, 128, 64);
    }
    
    private void crearPlataforma(float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(
            (x + width/2) * PIXELS_TO_METERS, 
            (y + height/2) * PIXELS_TO_METERS
        );
        
        Body body = world.createBody(bodyDef);
        
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
            width/2 * PIXELS_TO_METERS, 
            height/2 * PIXELS_TO_METERS
        );
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        
        body.createFixture(fixtureDef);
        shape.dispose();
    }
    
    private void crearPersonaje() {
        Personaje heroe = new Personaje(world);
        escena.addActor(heroe);
    }
    

    private void crearCajaSensor() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(ANCHO/2 * PIXELS_TO_METERS, ALTO/2 * PIXELS_TO_METERS);
        
        Body body = world.createBody(bodyDef);
        
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(50 * PIXELS_TO_METERS, 50 * PIXELS_TO_METERS); // Tamaño de la caja
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true; // Esto hace que sea un sensor (no colisión física)
        
        body.createFixture(fixtureDef);
        shape.dispose();
        
        // Guardamos el cuerpo en userData para identificarlo
        body.setUserData("CAJA_SENSOR");
    }

    
    

    @Override
    public void render(float delta) {
        // Actualizar el mundo físico
        world.step(1/60f, 6, 2);
        
        // Limpiar pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Actualizar escena
        escena.act(delta);
        
        // Dibujar
        batch.begin();
        // Aquí dibujarías tus bloques usando sus posiciones físicas
        batch.end();
        
        escena.draw();
        
        // Dibujar debug de Box2D (opcional, para desarrollo)
        debugRenderer.render(world, escena.getCamera().combined.scl(1/PIXELS_TO_METERS));
    }

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
        batch.dispose();
        escena.dispose();
        texturaBloque.dispose();
    }

	
	
	
	@Override
	public void resize(int width, int height) {
		escena.getViewport().update(width, height, true);
		
	}
	


	
	@Override
	public void show() {
	    Gdx.input.setInputProcessor(escena); // Para permitir input si es necesario
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}
}
