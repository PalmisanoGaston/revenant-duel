package escenas;


import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import Interfaces.CambioVidaEventListener;
import Interfaces.MuerteEventListener;
import fondos.FondoBase;
import fondos.FondoPrueba;
import gui.InfoPersonaje;
import gui.MenuArena;
import gui.MenuHeroe;
import gui.ScreenPerder;
import mejoras.MejoraVida;
import personajes.Jefe;
import personajes.Personaje;
import personajes.PersonajeBase;
import utiles.HitBox;

public class Arena implements Screen, MuerteEventListener , CambioVidaEventListener {
    private Principal juego;
    private Stage escena;
    private SpriteBatch batch;
    private Texture texturaBloque;
    private FondoBase fondo;
    private ExtendViewport viewport;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    
    public static final float PIXELS_TO_METERS = 1/100f; // 100 píxeles = 1 metro
    
    private Skin skin;
    private static final int ANCHO = 800;
    private static final int ALTO = 800;    
    
    
    private ArrayList<Body> cuerposAEliminar = new ArrayList<>();

    private Personaje heroe;
    
    private int intentosHeroe;
    private Jefe jefe;
    
    
    private InfoPersonaje uiHeroe;
    private InfoPersonaje uiJefe;
    
    private MenuArena menuArena;
    private boolean menuVisible = false;
    
    public Arena(Principal juego, Skin skin) {
        this.juego = juego;
        this.batch = new SpriteBatch();
        this.texturaBloque = new Texture("tileset.png");
        this.intentosHeroe = 5;
        
        world = new World(new Vector2(0, -10), true);
        debugRenderer = new Box2DDebugRenderer();
        this.viewport = new ExtendViewport(ANCHO, ALTO);
        this.escena = new Stage(viewport);
        
        crearPiso();
        
	    this.heroe = crearHeroe();
	    this.jefe = crearJefe();
	    this.skin = skin;
        construirArena(skin);
    }

    public Arena(Principal juego, Skin skin, MejoraVida mejoraVida, int vidaJefe, int intentosRestantes) {
        this.juego = juego;
        this.batch = new SpriteBatch();
        this.texturaBloque = new Texture("tileset.png");
        this.intentosHeroe = intentosRestantes;
        
        
        world = new World(new Vector2(0, -10), true);
        debugRenderer = new Box2DDebugRenderer();
        
        this.viewport = new ExtendViewport(ANCHO, ALTO);
        this.escena = new Stage(viewport);
        
        crearPiso();
        this.skin = skin;

       this.heroe = crearHeroe(mejoraVida);
       this.jefe = crearJefe(vidaJefe);
        construirArena(skin);
        
        
    }

	private void construirArena(Skin skin) {
        world.setContactListener(new HitBox());

        crearLimitesMapa(); 
        
        Table table = new Table();
        table.top();
        table.setFillParent(true);
        escena.addActor(table);
        
        this.uiHeroe = new InfoPersonaje(this.heroe.getNombre(), this.heroe.getVidaMaxima(), new Texture("placeholder.png"), skin, true);
        this.uiJefe =   new InfoPersonaje(this.jefe.getNombre(), this.jefe.getVidaMaxima(), new Texture("placeholder.png"), skin, false);

        table.add(uiHeroe).pad(100).top().left();
        table.add().expandX(); // Espacio flexible en el centro
        table.add(uiJefe).pad(100).top().right();

        this.fondo = new FondoPrueba();
	}
        
    private void crearLimitesMapa() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);


        float margin = 10f;
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
        // cuerpo físico
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, 0);
        
        // Crear cuerpo en el mundo
        Body body = world.createBody(bodyDef);
        
        // Definir forma (una caja de 10 bloques de ancho y 1 de alto)
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(this.ANCHO , 128 * PIXELS_TO_METERS);
        
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
    
    private Personaje crearHeroe(MejoraVida mejora) {
        Personaje heroe = new Personaje(world, this, this, mejora);
        escena.addActor(heroe);
        return heroe;
    }
    
    private Personaje crearHeroe() {
        Personaje heroe = new Personaje(world, this, this, MejoraVida.NIVEL_1);
        escena.addActor(heroe);
        return heroe;
    }
    
    private Jefe crearJefe() {
        Jefe jefe = new Jefe(world,this,this);
        escena.addActor(jefe);
        return jefe;
    }
    
    
    private Jefe crearJefe(int vidaRestante) {
        Jefe jefe = new Jefe(world,this,this);
        escena.addActor(jefe);
        jefe.setVida(vidaRestante);
        return jefe;
    }

    @Override
    public void render(float delta) {
    	 if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
    		 
    		 menuVisible = !menuVisible;
    	        
    	     if(menuVisible) {
    	    	 if(menuArena == null) {
    	    		 menuArena = new MenuArena(juego,this.skin );
    	         }
    	         this.escena.addActor(menuArena);
    	         float centerX = viewport.getWorldWidth() / 2 - menuArena.getWidth() / 2+50;
    	         float centerY = viewport.getWorldHeight() / 2 - menuArena.getHeight() / 2;
    	         menuArena.setPosition(centerX, centerY);

    	         Gdx.input.setInputProcessor(escena); 
    	     } else {
    	    	 if(menuArena != null) {
    	    		 menuArena.remove();
    	         }
    	     }
    	 }
    	 
    	//Hay que hacerlo de esta manera o si no explota
    	 if(!world.isLocked() && !cuerposAEliminar.isEmpty()) {
    		 for(Body body : cuerposAEliminar) {
    			 if(body != null) {
    				 world.destroyBody(body);
                 }
             }
             cuerposAEliminar.clear();
          }
    	 
    	 // Actualizar el mundo físico
    	 world.step(1/60f, 6, 2);

    	 
    	 // Limpiar pantalla
    	 Gdx.gl.glClearColor(0, 0, 0, 1);
    	 Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
    	 // Actualizar escena
    	 escena.act(delta);

    	 batch.setProjectionMatrix(viewport.getCamera().combined);
    	 batch.begin();
    	 this.fondo.render(batch, delta, viewport.getWorldWidth(), viewport.getWorldHeight());
    	 batch.end();
        
    	 escena.draw();
        
    	 // Mostrar hitboxes
        //debugRenderer.render(world, escena.getCamera().combined.scl(1/PIXELS_TO_METERS));
    }

    @Override
    public void dispose() {
        if(menuArena != null) {
            menuArena.remove();
        }
        world.dispose();
        debugRenderer.dispose();
        batch.dispose();
        escena.dispose();
        texturaBloque.dispose();
    }

	@Override
	public void resize(int width, int height) {
		escena.getViewport().update(width, height, true);
		if (menuVisible && menuArena != null) {
			float centerX = viewport.getWorldWidth() / 2 - menuArena.getWidth() / 2+50;
		    float centerY = viewport.getWorldHeight() / 2 - menuArena.getHeight() / 2;
		    menuArena.setPosition(centerX, centerY);
		}
	}

	@Override
	public void show() {
	    Gdx.input.setInputProcessor(escena); 
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void onPersonajeMuerto(PersonajeBase personaje) {
		
		if(personaje.getBody()!= null) {
		this.cuerposAEliminar.add(personaje.getBody());
		}

		if(personaje == this.jefe) {
			this.juego.setScreen(new ScreenPerder(this.juego,false));
		}
		
		if(personaje == this.heroe && this.intentosHeroe>0) {
			this.intentosHeroe--;
			this.juego.setScreen(new MenuHeroe(juego,this.heroe,this.jefe,this.intentosHeroe));
		}
		else {
			this.juego.setScreen(new ScreenPerder(this.juego,true));
		}
	}

	@Override
	public void onCambioVida(PersonajeBase personaje) {
		
	    if (personaje == this.heroe) {
	    	
	        uiHeroe.modificarInfo(heroe.getVida());
	    } else if (personaje == this.jefe) {
	    	
	    	if(this.jefe.getVida()<(this.jefe.getVidaMaxima()/2)) {
	    		
	    		this.jefe.modoBestia();
	    	}
	        uiJefe.modificarInfo(jefe.getVida());
	    }
	}
}
