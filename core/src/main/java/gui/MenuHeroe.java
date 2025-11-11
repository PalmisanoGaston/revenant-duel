package gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import red.ClientThread;
import sonidos.ControladorMusica;

public class MenuHeroe implements Screen {

    private Game juego;
    private Stage escena;
    private Skin fuenteTextos;
    private int intentos;
    private int mejoras_permitidas = 1;
    private ClientThread clientThread;
    
    // Current stats (will be modified by upgrades)
    private float multVida;
    private float multDanio;
    private float multVelocidad;
    private float multSalto;
    
    // UI elements to update
    private TextButton botonMejoraVida;
    private TextButton botonMejoraDanio;
    private TextButton botonMejoraVelocidad;
    private TextButton botonMejoraSalto;

    public MenuHeroe(Game juego, int intentos, float multVida, float multDanio, float multVelocidad, float multSalto) {
        this.juego = juego;
        this.escena = new Stage(new ScreenViewport());
        this.intentos = intentos;
        this.multVida = multVida;
        this.multDanio = multDanio;
        this.multVelocidad = multVelocidad;
        this.multSalto = multSalto;
        this.fuenteTextos = new Skin(Gdx.files.internal("uiskin.json"));
        
        construirInterfaz();
        ControladorMusica.play("musicaTienda.mp3");
    }

    private void construirInterfaz() {
        Table table = new Table();
        table.setFillParent(true);
        escena.addActor(table);

        Label titulo = new Label("Elige tus mejoras", fuenteTextos);
        table.add(titulo).colspan(2).padBottom(30);
        table.row();

        Label aviso = new Label("Intentos restantes: " + intentos, fuenteTextos);
        table.add(aviso).colspan(2).padBottom(30);
        table.row();

        // VIDA
        botonMejoraVida = new TextButton("Mejorar Vida (x" + formatearMult(multVida) + ")", fuenteTextos);
        botonMejoraVida.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (mejoras_permitidas > 0) {
                    multVida += 0.5f; // Increase by 50%
                    botonMejoraVida.setText("Mejorar Vida (x" + formatearMult(multVida) + ")");
                    mejoras_permitidas--;
                    actualizarBotones();
                }
            }
        });

        // DAÑO
        botonMejoraDanio = new TextButton("Mejorar Daño (x" + formatearMult(multDanio) + ")", fuenteTextos);
        botonMejoraDanio.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (mejoras_permitidas > 0) {
                    multDanio += 0.5f; // Increase by 50%
                    botonMejoraDanio.setText("Mejorar Daño (x" + formatearMult(multDanio) + ")");
                    mejoras_permitidas--;
                    actualizarBotones();
                }
            }
        });

        // VELOCIDAD
        botonMejoraVelocidad = new TextButton("Mejorar Velocidad (x" + formatearMult(multVelocidad) + ")", fuenteTextos);
        botonMejoraVelocidad.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (mejoras_permitidas > 0) {
                    multVelocidad += 0.5f; // Increase by 50%
                    botonMejoraVelocidad.setText("Mejorar Velocidad (x" + formatearMult(multVelocidad) + ")");
                    mejoras_permitidas--;
                    actualizarBotones();
                }
            }
        });

        // SALTO
        botonMejoraSalto = new TextButton("Mejorar Salto (x" + formatearMult(multSalto) + ")", fuenteTextos);
        botonMejoraSalto.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (mejoras_permitidas > 0) {
                    multSalto += 0.5f; // Increase by 50%
                    botonMejoraSalto.setText("Mejorar Salto (x" + formatearMult(multSalto) + ")");
                    mejoras_permitidas--;
                    actualizarBotones();
                }
            }
        });

        // VOLVER
        TextButton botonVolver = new TextButton("Volver a la pelea", fuenteTextos);
        botonVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Send upgraded stats to server
                if (clientThread != null) {
                    clientThread.sendHeroUpgraded(multVida, multDanio, multVelocidad, multSalto, intentos);
                    System.out.println("Sent upgrades to server - Vida: " + multVida + ", Daño: " + multDanio + 
                                     ", Velocidad: " + multVelocidad + ", Salto: " + multSalto);
                } else {
                    System.out.println("Error: ClientThread not set in MenuHeroe");
                }
                // Don't create new Arena here - server will send ResumeGame message
            }
        });

        table.add(botonMejoraVida).width(300).height(60).padBottom(20);
        table.row();
        table.add(botonMejoraDanio).width(300).height(60).padBottom(20);
        table.row();
        table.add(botonMejoraVelocidad).width(300).height(60).padBottom(20);
        table.row();
        table.add(botonMejoraSalto).width(300).height(60).padBottom(20);
        table.row();
        table.add(botonVolver).width(200).height(50);
        
        actualizarBotones();
    }

    private void actualizarBotones() {
        // Disable buttons if no upgrades left
        boolean habilitado = (mejoras_permitidas > 0);
        botonMejoraVida.setDisabled(!habilitado);
        botonMejoraDanio.setDisabled(!habilitado);
        botonMejoraVelocidad.setDisabled(!habilitado);
        botonMejoraSalto.setDisabled(!habilitado);
    }

    public void setClientThread(ClientThread clientThread) {
        this.clientThread = clientThread;
    }

    private String formatearMult(float mult) {
        if (Math.abs(mult - Math.round(mult)) < 1e-3) {
            return String.valueOf((int) Math.round(mult));
        }
        return String.format(java.util.Locale.US, "%.2f", mult);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        escena.act(delta);
        escena.draw();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void dispose() {
        escena.dispose();
        fuenteTextos.dispose();
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
}