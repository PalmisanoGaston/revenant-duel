package escenas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.ArrayList;
import java.util.List;

import Interfaces.CambioVidaEventListener;
import Interfaces.GameController;
import Interfaces.MuerteEventListener;
import fondos.FondoBase;
import fondos.FondoPrueba;
import gui.EscenaEspera;
import gui.InfoPersonaje;
import gui.MenuArena;
import gui.MenuHeroe;
import gui.ScreenPerder;
import personajes.Estadistica;
import personajes.Heroe;
import personajes.Jefe;
import red.ClientThread;
import utiles.InputManager;
import utiles.RemoteProjectileManager;
import utiles.ProyectilManager;

public class Arena implements Screen, GameController {
    private final Game juego;
    private final Skin skin;
    private final ExtendViewport viewport;
    private final Stage escena;

    private World world;
    private ProyectilManager proyectilManager;
    private RemoteProjectileManager remoteProjectileManager;
    private FondoBase fondo;
    private Heroe heroe;
    private Jefe jefe;
    private InfoPersonaje uiHeroe;
    private InfoPersonaje uiJefe;

    private ClientThread clientThread;
    private final InputManager inputManager;
    private MenuArena menuArena;

    private int playerRole = -1;
    private final Estadistica estadisticasHeroe;
    private int vidaJefe;
    private int intentosRestantes;
    private final boolean isReconnection;
    private boolean gameEnded = false;

    public static final float PIXELS_TO_METERS = 1 / 100f;
    private static final int ANCHO = 800;
    private static final int ALTO = 800;
    public static final short CATEGORY_PERSONAJE = 0x0001;
    public static final short CATEGORY_ENTORNO = 0x0002;
    public static final short CATEGORY_PROYECTIL = 0x0004;

    public Arena(Game juego, Skin skin) {
        this(juego, skin, 150, 5, new Estadistica(), false, -1);
    }

    public Arena(Game juego, Skin skin, int vidaJefe, int intentosRestantes, Estadistica estadisticasHeroe,
                 boolean isReconnection, int rol) {
        this.juego = juego;
        this.skin = skin;
        this.vidaJefe = vidaJefe;
        this.intentosRestantes = intentosRestantes;
        this.estadisticasHeroe = estadisticasHeroe;
        this.isReconnection = isReconnection;
        this.viewport = new ExtendViewport(ANCHO, ALTO);
        this.escena = new Stage(viewport);

        setupSceneActors();
        this.inputManager = new InputManager(this);

        if (!isReconnection) {
            initializeNetwork();
        } else {
            this.playerRole = rol;
            System.out.println("Arena reconnected - waiting for server state");
        }

        System.out.println("Arena created - Reconnection: " + isReconnection +
                ", Vida Jefe: " + vidaJefe +
                ", Intentos: " + intentosRestantes +
                ", HP Multiplier: " + estadisticasHeroe.getMultVida());
    }

    private void setupSceneActors() {
        this.world = new World(new Vector2(0, 0), true);
        this.proyectilManager = new ProyectilManager(world);
        this.remoteProjectileManager = new RemoteProjectileManager();

        MuerteEventListener muerteListener = personaje -> {};
        CambioVidaEventListener vidaListener = personaje -> {};

        this.heroe = new Heroe(world, muerteListener, vidaListener, proyectilManager, estadisticasHeroe);
        this.jefe = new Jefe(world, muerteListener, vidaListener);
        this.heroe.setRemoteControlled(true);
        this.jefe.setRemoteControlled(true);

        this.fondo = new FondoPrueba();
        this.escena.addActor(this.fondo);
        this.fondo.toBack();

        this.escena.addActor(remoteProjectileManager);
        this.escena.addActor(this.heroe);
        this.escena.addActor(this.jefe);

        construirUI();
        crearLimitesMapa();
    }

    private void construirUI() {
        Table table = new Table();
        table.top();
        table.setFillParent(true);
        escena.addActor(table);

        this.uiHeroe = new InfoPersonaje(this.heroe.getNombre(), this.heroe.getVidaMaxima(),
                new Texture("placeholder.png"), skin, true, this.heroe.getArrayMovimientos());
        this.uiJefe = new InfoPersonaje(this.jefe.getNombre(), this.jefe.getVidaMaxima(),
                new Texture("placeholder.png"), skin, false, this.jefe.getArrayMovimientos());

        table.add(uiHeroe).pad(150).top().left();
        table.add().expandX();
        table.add(uiJefe).pad(150).top().right();
    }

    private void crearLimitesMapa() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, 0);
        BodyDef bodyDefBordes = new BodyDef();
        bodyDefBordes.type = BodyDef.BodyType.StaticBody;

        // Piso
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(ANCHO, 128 * PIXELS_TO_METERS);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.5f;
        fixtureDef.filter.categoryBits = CATEGORY_ENTORNO;
        fixtureDef.filter.maskBits = CATEGORY_PERSONAJE;
        world.createBody(bodyDef).createFixture(fixtureDef);
        shape.dispose();

        // Bordes
        ChainShape borders = new ChainShape();
        float margin = 10f;
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2((-margin * PIXELS_TO_METERS) + 0.08f, -margin * PIXELS_TO_METERS);
        vertices[1] = new Vector2((-margin * PIXELS_TO_METERS) + 0.08f, (ALTO + margin) * PIXELS_TO_METERS);
        vertices[2] = new Vector2((ANCHO + margin) * 1.88f * PIXELS_TO_METERS, (ALTO + margin) * PIXELS_TO_METERS);
        vertices[3] = new Vector2((ANCHO + margin) * 1.88f * PIXELS_TO_METERS, -margin * PIXELS_TO_METERS);
        borders.createLoop(vertices);

        FixtureDef borderFixture = new FixtureDef();
        borderFixture.shape = borders;
        borderFixture.friction = 0.5f;
        borderFixture.filter.categoryBits = CATEGORY_ENTORNO;
        borderFixture.filter.maskBits = CATEGORY_PERSONAJE | CATEGORY_PROYECTIL;
        world.createBody(bodyDefBordes).createFixture(borderFixture);
        borders.dispose();
    }

    private void initializeNetwork() {
        this.clientThread = new ClientThread(this);
        this.clientThread.start();
        this.clientThread.connectToServer();
    }

    public void setClientThread(ClientThread clientThread) {
        this.clientThread = clientThread;
        if (this.clientThread != null) {
            this.clientThread.setGameController(this);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
            menuArena = null;
        }
        if (remoteProjectileManager != null) {
            remoteProjectileManager.dispose();
        }
        if (world != null) {
            world.dispose();
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

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

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

    @Override
    public void startGame() {
        System.out.println("Game started!");
    }

    @Override
    public void accionar(int rol, int keycode) {
        if (clientThread == null) {
            return;
        }
        if (!isMenuAbierto() && playerRole != -1) {
            clientThread.sendInput(playerRole, keycode);
        } else if (playerRole == -1) {
            System.out.println("Cannot send input - player role not assigned yet");
        }
    }

    @Override
    public void heroDied(int heroVida, int intentosRestantes, float multVida, float multDanio,
                         float multVelocidad, float multSalto) {
        if (gameEnded) {
            return;
        }
        this.intentosRestantes = intentosRestantes;
        if (playerRole == 0) {
            showUpgradeMenu(intentosRestantes, multVida, multDanio, multVelocidad, multSalto);
        } else {
            juego.setScreen(new EscenaEspera(juego, skin));
        }
    }

    private void showUpgradeMenu(int intentosRestantes, float multVida, float multDanio,
                                 float multVelocidad, float multSalto) {
        MenuHeroe menuHeroe = new MenuHeroe(juego, intentosRestantes, multVida, multDanio, multVelocidad, multSalto);
        menuHeroe.setClientThread(clientThread);
        juego.setScreen(menuHeroe);
    }

    @Override
    public void heroUpgraded(float multVida, float multDanio, float multVelocidad, float multSalto) {
        this.estadisticasHeroe.setMultVida(multVida);
        this.estadisticasHeroe.setMultDanio(multDanio);
        this.estadisticasHeroe.setMultVelocidad(multVelocidad);
        this.estadisticasHeroe.setMultSalto(multSalto);

        System.out.println("Hero upgraded - Vida: " + multVida + ", Daño: " + multDanio +
                ", Velocidad: " + multVelocidad + ", Salto: " + multSalto +
                ", Intentos restantes: " + intentosRestantes);

        Arena reconnectedArena = new Arena(juego, skin, vidaJefe, intentosRestantes, estadisticasHeroe, true, this.playerRole);
        ClientThread existingThread = this.clientThread;
        this.clientThread = null;
        reconnectedArena.setClientThread(existingThread);
        juego.setScreen(reconnectedArena);
    }

    @Override
    public void resumeGame() {
        System.out.println("Resuming game...");
    }

    public int getPlayerRole() {
        return playerRole;
    }

    @Override
    public void setPlayerRole(int assignedRole) {
        this.playerRole = assignedRole;
        System.out.println("Player role assigned: " + (assignedRole == 0 ? "Hero" : "Boss"));
    }

    public void applyServerState(String[] parts) {
        if (gameEnded || parts == null) {
            return;
        }
        // Expected format:
        // State:heroX:heroY:heroVida:heroVidaMax:heroFacing:heroAnim:heroStateTime:
        //       bossX:bossY:bossVida:bossVidaMax:bossFacing:bossAnim:bossStateTime:
        //       projectileCount:[type:x:y]*
        if (parts.length < 15) {
            return;
        }
        try {
            int index = 1;
            float heroX = Float.parseFloat(parts[index++]);
            float heroY = Float.parseFloat(parts[index++]);
            int heroVida = Integer.parseInt(parts[index++]);
            int heroVidaMax = Integer.parseInt(parts[index++]);
            boolean heroFacing = Integer.parseInt(parts[index++]) == 1;
            String heroAnim = parts[index++];
            float heroStateTime = Float.parseFloat(parts[index++]);
            String heroCooldowns = parts[index++];

            float bossX = Float.parseFloat(parts[index++]);
            float bossY = Float.parseFloat(parts[index++]);
            int bossVida = Integer.parseInt(parts[index++]);
            int bossVidaMax = Integer.parseInt(parts[index++]);
            boolean bossFacing = Integer.parseInt(parts[index++]) == 1;
            String bossAnim = parts[index++];
            float bossStateTime = Float.parseFloat(parts[index++]);
            boolean modoBestia = Integer.parseInt(parts[index++]) == 1;
            String bossCooldowns = parts[index++];

            int projectileCount = Integer.parseInt(parts[index++]);
            List<RemoteProjectileManager.ProjectileState> projectileStates = new ArrayList<>();
            for (int i = 0; i < projectileCount && index + 3 < parts.length; i++) {
                String type = parts[index++];
                float projX = Float.parseFloat(parts[index++]);
                float projY = Float.parseFloat(parts[index++]);
                int direction = Integer.parseInt(parts[index++]); // 1=right, 0=left
                projectileStates.add(new RemoteProjectileManager.ProjectileState(type, projX, projY, direction == 1));
            }

            if(modoBestia){
                this.jefe.modoBestia();
            }

            this.heroe.applyRemoteState(heroX, heroY, heroFacing, heroAnim, heroStateTime, heroVida, heroVidaMax);
            this.heroe.applyCooldowns(heroCooldowns);
            this.jefe.applyRemoteState(bossX, bossY, bossFacing, bossAnim, bossStateTime, bossVida, bossVidaMax);
            this.jefe.applyCooldowns(bossCooldowns);
            this.uiHeroe.modificarInfo(heroVida, heroVidaMax);
            this.uiJefe.modificarInfo(bossVida, bossVidaMax);
            this.vidaJefe = bossVida;
            this.remoteProjectileManager.updateProjectiles(projectileStates);
        } catch (NumberFormatException ex) {
            System.out.println("Failed to parse state message: " + ex.getMessage());
        }
    }

    public void onGameEnded(int winner) {
        if (gameEnded) {
            return;
        }
        gameEnded = true;
        if (clientThread != null) {
            clientThread.terminate();
            clientThread = null;
        }
        juego.setScreen(new ScreenPerder(juego, winner != 0));
    }

    public void onServerDisconnected() {
        if (gameEnded) {
            return;
        }
        gameEnded = true;
        if (clientThread != null) {
            clientThread.terminate();
            clientThread = null;
        }
        juego.setScreen(new ScreenPerder(juego, true));
    }
}
