package escenas;


import java.util.ArrayList;
import java.util.Locale;

import utiles.ProyectilManager;
import utiles.StageInputProcessor;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import Interfaces.CambioVidaEventListener;
import Interfaces.GameController;
import Interfaces.MuerteEventListener;
import fondos.FondoBase;
import fondos.FondoPrueba;
import gui.InfoPersonaje;
import gui.MenuArena;
import gui.MenuHeroe;
import gui.ScreenPerder;
import mejoras.MejoraVida;
import personajes.Jefe;
import personajes.LectorInputs;
import personajes.Estadistica;
import personajes.Heroe;
import personajes.PersonajeBase;
import movimientos.Proyectil;
import red.ServerThread;
import sonidos.ControladorMusica;
import utiles.ClickReceptor;
import utiles.HitBox;
import utiles.InputManager;

public class Arena implements Screen, MuerteEventListener , CambioVidaEventListener, ClickReceptor, GameController {
    private Game juego;
    private Stage escena;
    private SpriteBatch batch;
    private Texture texturaBloque;
    private FondoBase fondo;
    private ExtendViewport viewport;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private ProyectilManager proyectilManager;
    
    public static final float PIXELS_TO_METERS = 1/100f; // 100 píxeles = 1 metro
    
    private Skin skin;
    private static final int ANCHO = 800;
    private static final int ALTO = 800;
    private static final Locale LOCALE = Locale.US;
    
    private Estadistica estadisticasHeroe; 
    
    private ArrayList<Body> cuerposAEliminar = new ArrayList<>();

    private Heroe heroe;
    
    private int intentosHeroe;
    private Jefe jefe;
    private int storedBossHealth;

    
    private InfoPersonaje uiHeroe;
    private InfoPersonaje uiJefe;
    
    private MenuArena menuArena;
    private boolean menuVisible = false;
    private LectorInputs lectorInputs;
    
    public static final short CATEGORY_PERSONAJE = 0x0001;
    public static final short CATEGORY_ENTORNO   = 0x0002;
    public static final short CATEGORY_PROYECTIL = 0x0004; // si más adelante agregás
    private InputManager inputManager;
    
    private ServerThread serverThread;
    private boolean gameFinished = false;
    
    
    public Arena(Game juego, Skin skin) {
        this.juego = juego;
        this.batch = new SpriteBatch();
        this.texturaBloque = new Texture("tileset.png");
        this.intentosHeroe = 5;
        this.estadisticasHeroe = new Estadistica();
        world = new World(new Vector2(0, -10), true);
        this.proyectilManager = new ProyectilManager(world);
        debugRenderer = new Box2DDebugRenderer();
        this.viewport = new ExtendViewport(ANCHO, ALTO);
        this.escena = new Stage(viewport);
        
        crearPiso();
        
        this.jefe = crearJefe();
	    this.heroe = crearHeroe();
	    escena.addActor(this.proyectilManager);

        this.lectorInputs = new LectorInputs(this.heroe, this.jefe, this);
        StageInputProcessor stageProcessor = new StageInputProcessor(escena);
        this.inputManager = new InputManager(this.lectorInputs, this);
	    this.skin = skin;
	    this.serverThread = new ServerThread(this);
        construirArena(skin);
        this.serverThread.start();

    }

    public Arena(Game juego, Skin skin, int vidaJefe, int intentosRestantes, Estadistica estadisticasHeroe) {
        this.juego = juego;
        this.batch = new SpriteBatch();
        this.texturaBloque = new Texture("tileset.png");
        this.intentosHeroe = intentosRestantes;
        this.estadisticasHeroe = estadisticasHeroe; // ← Guardar las estadísticas pasadas
        
        world = new World(new Vector2(0, -10), true);
        this.proyectilManager = new ProyectilManager(world);
        debugRenderer = new Box2DDebugRenderer();
        
        this.viewport = new ExtendViewport(ANCHO, ALTO);
        this.escena = new Stage(viewport);
        
        crearPiso();
        this.skin = skin;
        this.jefe = crearJefe(vidaJefe);
        this.heroe = crearHeroe(); // ← This hero should now use the upgraded stats
        escena.addActor(this.proyectilManager);
     
        construirArena(skin);
        this.lectorInputs = new LectorInputs(this.heroe, this.jefe, this);
        StageInputProcessor stageProcessor = new StageInputProcessor(escena);
        this.inputManager = new InputManager(this.lectorInputs, this);
        
        // Don't start a new server thread here - it should be set by the calling method
    }

	private void construirArena(Skin skin) {
        this.fondo = new FondoPrueba();
        escena.addActor(this.fondo);
        this.fondo.toBack();
        world.setContactListener(new HitBox(this.proyectilManager));

        crearLimitesMapa(); 
        
        Table table = new Table();
        table.top();
        table.setFillParent(true);
        escena.addActor(table);
        
        this.uiHeroe = new InfoPersonaje(this.heroe.getNombre(), this.heroe.getVidaMaxima(), new Texture("placeholder.png"), skin, true, this.heroe.getArrayMovimientos());
        this.uiJefe =   new InfoPersonaje(this.jefe.getNombre(), this.jefe.getVidaMaxima(), new Texture("placeholder.png"), skin, false,  this.jefe.getArrayMovimientos());

        table.add(uiHeroe).pad(150).top().left();
        table.add().expandX(); // Espacio flexible en el centro
        table.add(uiJefe).pad(150).top().right();
        this.jefe.recibirDaño(0);
        ControladorMusica.play("temaBatalla.mp3");
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
        fixtureDef.friction = 0.5f;
        fixtureDef.filter.categoryBits = CATEGORY_ENTORNO;
	    fixtureDef.filter.maskBits = CATEGORY_PERSONAJE | CATEGORY_PROYECTIL;
	    
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
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.5f;
        fixtureDef.filter.categoryBits = CATEGORY_ENTORNO;
	    // CORRECCIÓN: El entorno choca con personajes Y proyectiles
	    fixtureDef.filter.maskBits = CATEGORY_PERSONAJE | CATEGORY_PROYECTIL;
        body.createFixture(fixtureDef);

        
        // Liberar la forma
        shape.dispose();
    }
    
    private Heroe crearHeroe() {
        Heroe heroe = new Heroe(world, this, this, this.proyectilManager, this.estadisticasHeroe);
        escena.addActor(heroe);
        
        // Debug output to verify stats
        System.out.println("Hero created with stats - Vida: " + this.estadisticasHeroe.getMultVida() +
                          ", Daño: " + this.estadisticasHeroe.getMultDanio() +
                          ", Velocidad: " + this.estadisticasHeroe.getMultVelocidad() +
                          ", Salto: " + this.estadisticasHeroe.getMultSalto());
        
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
    
    
    public void mostrarMenuConfiguracion() {
        if (menuArena == null) {
            menuArena = new MenuArena(juego, this.skin, this);
            this.escena.addActor(menuArena);

            float centerX = viewport.getWorldWidth() / 2 - menuArena.getWidth() / 2 + 50;
            float centerY = viewport.getWorldHeight() / 2 - menuArena.getHeight() / 2;
            menuArena.setPosition(centerX, centerY);

            inputManager.setMenuMode();  // ← reactivamos esta línea
        }
    }

    public void cerrarMenu() {
        menuArena.remove();
        menuArena = null;
        inputManager.setArenaMode();    // ← también reactivamos esta
    }
    
 // Agregá esto en la clase Arena (cerca de los otros métodos públicos)
    public boolean isMenuAbierto() {
        return menuArena != null;
    }
    
    public void storeBossHealth() {
        this.storedBossHealth = this.jefe.getVida();
        System.out.println("Stored boss health: " + storedBossHealth);
    }



    @Override
    public void render(float delta) {

    	 
    	//Hay que hacerlo de esta manera o si no explota
    	 if(!world.isLocked() && !cuerposAEliminar.isEmpty()) {
    		 for(Body body : cuerposAEliminar) {
    			 if(body != null) {
    				 world.destroyBody(body);
                 }
             }
             cuerposAEliminar.clear();

    	           
    	        } 
    	  
    	 
    	//Hay que hacerlo de esta manera o si explota
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
        escena.draw();

        broadcastGameState();
        
    	 // Mostrar hitboxes
        debugRenderer.render(world, escena.getCamera().combined.scl(1/PIXELS_TO_METERS));
}

    @Override
    public void dispose() {
        if(menuArena != null) {
            menuArena.remove();
        }
        this.world.dispose();
        this.debugRenderer.dispose();
        this.batch.dispose();
        this.escena.dispose();
        this. texturaBloque.dispose();
        this. proyectilManager.limpiar();
    }

    private void broadcastGameState() {
        if (serverThread == null || gameFinished || serverThread.getConnectedClientsCount() == 0) {
            return;
        }

        StringBuilder builder = new StringBuilder("State");
        appendCharacterState(builder, this.heroe);
        appendCharacterState(builder, this.jefe);

        ArrayList<Proyectil> activos = new ArrayList<>();
        for (Proyectil proyectil : this.proyectilManager.getProyectiles()) {
            if (proyectil != null && proyectil.estaActivo()) {
                activos.add(proyectil);
            }
        }

        builder.append(':').append(activos.size());
        for (Proyectil proyectil : activos) {
            builder.append(':').append(proyectil.getTipo())
                   .append(':').append(formatFloat(proyectil.getPosicion().x))
                   .append(':').append(formatFloat(proyectil.getPosicion().y))
                   .append(':').append(proyectil.getVelocidad().x > 0 ? 1 : 0); // Add direction (1=right, 0=left)
        }

        serverThread.sendMessageToAll(builder.toString());
    }

    private void appendCharacterState(StringBuilder builder, PersonajeBase personaje) {
        builder.append(':').append(formatFloat(personaje.getX()))
               .append(':').append(formatFloat(personaje.getY()))
               .append(':').append(personaje.getVida())
               .append(':').append(personaje.getVidaMaxima())
               .append(':').append(personaje.getLado() ? 1 : 0)
               .append(':').append(personaje.getAnimacionActualNombre())
               .append(':').append(formatFloat(personaje.getStateTime()));
        System.out.println("SERVER STATE = " + builder.toString());

        if (personaje instanceof Jefe) {
            builder.append(':').append(((Jefe) personaje).isModoBestia() ? 1 : 0);
        }

        builder.append(':').append(personaje.getCooldownsString());
    }

    private String formatFloat(float value) {
        return String.format(LOCALE, "%.2f", value);
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
		  Gdx.input.setInputProcessor(this.inputManager);
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}
	public void onPersonajeMuerto(PersonajeBase personaje) {
	    if (personaje.getBody() != null) {
	        this.cuerposAEliminar.add(personaje.getBody());
	    }

	    // 1) Murió el Jefe => gana el héroe
            if (personaje == this.jefe) {
                this.juego.setScreen(new ScreenPerder(this.juego, false));
                if (serverThread != null) {
                    serverThread.sendMessageToAll("EndGame:0"); // Hero wins
                    serverThread.disconnectClients();
                    serverThread.terminate(); // Terminate thread
                    serverThread = null;
                }
                this.gameFinished = true;
                return;
            }

	    // 2) Murió el Héroe
	    if (personaje == this.heroe) {
	        if (this.intentosHeroe > 0) {
	            this.intentosHeroe--; // Decrement attempts
	            
	            // Store current game state
	        this.storedBossHealth= this.jefe.getVida();
	            
	            // Notify server thread to handle upgrade process
	            serverThread.heroDied(
	                heroe.getVida(), 
	                this.intentosHeroe,
	                heroe.getEstadistica().getMultVida(),
	                heroe.getEstadistica().getMultDanio(),
	                heroe.getEstadistica().getMultVelocidad(),
	                heroe.getEstadistica().getMultSalto()
	            );
                } else {
                    this.juego.setScreen(new ScreenPerder(this.juego, true));
                    if (serverThread != null) {
                        serverThread.sendMessageToAll("EndGame:1"); // Boss wins
                        serverThread.disconnectClients();
                        serverThread.terminate(); // Terminate thread
                        serverThread = null;
                    }
                    this.gameFinished = true;
                }
                return;
            }
	}
	
	public Arena resumeGameWithUpgrades(float multVida, float multDanio, float multVelocidad, float multSalto, int intentos) {
	    
	    this.estadisticasHeroe.setMultVida(multVida);
	    this.estadisticasHeroe.setMultDanio(multDanio);
	    this.estadisticasHeroe.setMultVelocidad(multVelocidad);
	    this.estadisticasHeroe.setMultSalto(multSalto);
	    
	    // Use the passed intentos instead of constant 5
	    Arena reconnectedArena = new Arena(juego, skin, this.storedBossHealth, intentos, estadisticasHeroe);
	    
	    reconnectedArena.setServerThread(this.serverThread);
	    this.juego.setScreen(reconnectedArena);
	    
	    System.out.println("Game resumed with upgrades - Vida: " + multVida + 
	                      ", Daño: " + multDanio + ", Velocidad: " + multVelocidad + 
	                      ", Salto: " + multSalto + ", Boss Health: " + storedBossHealth +
	                      ", Intentos restantes: " + intentos);
	    
	    return reconnectedArena;
	}
	
	

	public void setServerThread(ServerThread serverThread) {
		this.serverThread = serverThread;
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
	

	

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
	    return escena.touchDown(x, y, pointer, button);
	}
	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
	    return escena.touchUp(x, y, pointer, button);
	}
	@Override
	public boolean touchDragged(int x, int y, int pointer) {
	    return escena.touchDragged(x, y, pointer);
	}

	@Override
	public void startGame() {
		// TODO Auto-generated method stub
		
	}

	@Override
        public void accionar(int rol, int keycode) {
            boolean keyDown = keycode >= 0;
            int codigoReal = Math.abs(keycode);

            System.out.println("Server: Processing input - Role: " + rol + ", Keycode: " + codigoReal +
                               (keyDown ? " (DOWN)" : " (UP)"));

            this.lectorInputs.procesarInputPorRol(rol, codigoReal, keyDown);
        }

        // Add a method for key releases as well
        public void liberarInput(int rol, int keycode) {
            this.lectorInputs.procesarInputPorRol(rol, keycode, false);
        }

	@Override
	public void heroDied(int heroVida, int intentosRestantes, float multVida, float multDanio, float multVelocidad,
			float multSalto) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void heroUpgraded(float multVida, float multDanio, float multVelocidad, float multSalto) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resumeGame() {
		// TODO Auto-generated method stub
		
	}
	

}
