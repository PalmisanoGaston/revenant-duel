package escenas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import Interfaces.GameController;
import gui.EscenaEspera;
import gui.MenuArena;
import gui.MenuHeroe;
import personajes.Estadistica;
import personajes.Heroe;
import personajes.Jefe;
import red.ClientThread;
import utiles.InputManager;

public class Arena implements Screen, GameController {
    private Game juego;
    private Stage escena;
    private Skin skin;
    private ExtendViewport viewport;
    private int playerRole = -1;
    
    // Keep constants for compatibility with existing classes
    public static final float PIXELS_TO_METERS = 1/100f;
    private static final int ANCHO = 800;
    private static final int ALTO = 800;    
    public static final short CATEGORY_PERSONAJE = 0x0001;
    public static final short CATEGORY_ENTORNO   = 0x0002;
    public static final short CATEGORY_PROYECTIL = 0x0004;
    
    private ClientThread clientThread;
    private InputManager inputManager;
    private MenuArena menuArena;
    
    private boolean isReconnection = false;
    
    // Fields for upgrade system
    private int vidaJefe;
    private int intentosRestantes;
    private Estadistica estadisticasHeroe;

    
    // Main constructor for initial game start
    public Arena(Game juego, Skin skin) {
        this(juego, skin, 150, 5, new Estadistica(),false,0); // Default values
    }
    
    public Arena(Game juego, Skin skin, int vidaJefe, int intentosRestantes, Estadistica estadisticasHeroe, boolean isReconnection, int rol) {
        this.juego = juego;
        this.skin = skin;
        this.vidaJefe = vidaJefe;
        this.intentosRestantes = intentosRestantes; // Store the actual remaining tries
        this.estadisticasHeroe = estadisticasHeroe;
        this.isReconnection = isReconnection;
        
        this.viewport = new ExtendViewport(ANCHO, ALTO);
        this.escena = new Stage(viewport);
        
        if (!isReconnection) {
            initializeNetwork();
        } else {
            this.inputManager = new InputManager(this);
            System.out.println("Arena reconnected - waiting for server state");
            this.playerRole = rol;
        }
        
        System.out.println("Arena created - Reconnection: " + isReconnection + 
                          ", Vida Jefe: " + vidaJefe + 
                          ", Intentos: " + intentosRestantes +
                          ", HP Multiplier: " + estadisticasHeroe.getMultVida());
    }

    private void initializeNetwork() {
        // Initialize network
        this.clientThread = new ClientThread(this);
        this.clientThread.start();
        
        // Connect to server
        this.clientThread.connectToServer();
        
        // Input manager for sending inputs to server
        this.inputManager = new InputManager(this);
    }
    
    public void setClientThread(ClientThread clientThread) {
        this.clientThread = clientThread;
    }
    
    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Update and draw scene (visual only - no game logic)
        escena.act(delta);
        escena.draw();
    }

    @Override
    public void dispose() {
        if (clientThread != null) {
            clientThread.terminate();
        }
        if (menuArena != null) {
            menuArena.remove();
        }
        escena.dispose();
    }

    @Override
    public void resize(int width, int height) {
        escena.getViewport().update(width, height, true);
        if (menuArena != null) {
            float centerX = viewport.getWorldWidth() / 2 - menuArena.getWidth() / 2 + 50;
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

    // Menu methods (client-side functionality)
    public void mostrarMenuConfiguracion() {
        if (menuArena == null) {
            menuArena = new MenuArena(juego, this.skin, this);
            this.escena.addActor(menuArena);

            float centerX = viewport.getWorldWidth() / 2 - menuArena.getWidth() / 2 + 50;
            float centerY = viewport.getWorldHeight() / 2 - menuArena.getHeight() / 2;
            menuArena.setPosition(centerX, centerY);

            inputManager.setMenuMode();
        }
    }

    public void cerrarMenu() {
        if (menuArena != null) {
            menuArena.remove();
            menuArena = null;
            inputManager.setArenaMode();
        }
    }
    
    public boolean isMenuAbierto() {
        return menuArena != null;
    }

    // GameController implementation
    @Override
    public void startGame() {
        System.out.println("Game started!");
        // The server will handle all game logic
        // This client only sends inputs
    }

    @Override
    public void accionar(int rol, int keycode) {
        // Don't set playerRole here - it should be set by the server via ClientThread
        // Just send the input with the assigned role
        
        // Don't send inputs if menu is open OR if role isn't assigned yet
        if (!isMenuAbierto() && playerRole != -1) {
            clientThread.sendInput(playerRole, keycode); // Use the assigned role
        } else if (playerRole == -1) {
            System.out.println("Cannot send input - player role not assigned yet");
        }
    }
    
    @Override
    public void heroDied(int heroVida, int intentosRestantes, float multVida, float multDanio, float multVelocidad, float multSalto) {
        System.out.println("DEBUG: heroDied called on main thread");
        
        if (playerRole == 0) { // Hero client
            // Show upgrade menu with just the stats
            showUpgradeMenu(intentosRestantes, multVida, multDanio, multVelocidad, multSalto);
        } else { // Boss client or unassigned
            // Show waiting screen
            juego.setScreen(new EscenaEspera(juego, skin));
        }
    }

    private void showUpgradeMenu(int intentosRestantes, float multVida, float multDanio, float multVelocidad, float multSalto) {
        // Create menu with just the stats - no Heroe/Jefe instances needed
        MenuHeroe menuHeroe = new MenuHeroe(juego, intentosRestantes, multVida, multDanio, multVelocidad, multSalto);
        menuHeroe.setClientThread(clientThread);
        juego.setScreen(menuHeroe);
    }

    public void heroUpgraded(float multVida, float multDanio, float multVelocidad, float multSalto) {
        // Update stats with new upgrades
        this.estadisticasHeroe.setMultVida(multVida);
        this.estadisticasHeroe.setMultDanio(multDanio);
        this.estadisticasHeroe.setMultVelocidad(multVelocidad);
        this.estadisticasHeroe.setMultSalto(multSalto);
        
        System.out.println("Hero upgraded - Vida: " + multVida + ", Daño: " + multDanio + 
                          ", Velocidad: " + multVelocidad + ", Salto: " + multSalto +
                          ", Intentos restantes: " + intentosRestantes);
        
        // Create reconnection Arena with current intentos
        Arena reconnectedArena = new Arena(juego, skin, vidaJefe, intentosRestantes, estadisticasHeroe, true, this.playerRole);
        
        // Pass the existing client thread to the new arena
        reconnectedArena.setClientThread(this.clientThread);
        
        // Switch to the reconnected arena
        juego.setScreen(reconnectedArena);
    }
    @Override
    public void resumeGame() {
        // Server signals to resume the game
        System.out.println("Resuming game...");
        // The game should already be running, this is just a notification
    }
    

    // Getters for game state
    public int getPlayerRole() {
        return playerRole;
    }

	@Override
	public void setPlayerRole(int assignedRole) {
        this.playerRole = assignedRole;
        System.out.println("Player role assigned: " + (assignedRole == 0 ? "Hero" : "Boss"));
		
	}
}