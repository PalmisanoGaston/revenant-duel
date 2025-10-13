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

import escenas.Arena;
import escenas.Principal;
// import mejoras.MejorasHeroe;  // ← eliminado
import personajes.Jefe;
import personajes.Heroe;

public class MenuHeroe implements Screen {

    private Game juego;
    private Stage escena;
    private Skin fuenteTextos;
    private Heroe heroe;
    private Jefe jefe; // Añadir referencia al jefe
    private int intentos;
    private int mejoras_permitidas = 1;

    public MenuHeroe(Game juego, Heroe heroe, Jefe jefe, int intentos) {
        this.juego = juego;
        this.escena = new Stage(new ScreenViewport());
        this.heroe = heroe;
        this.jefe = jefe; // Guardar referencia al jefe
        this.intentos = intentos;
        this.fuenteTextos = new Skin(Gdx.files.internal("uiskin.json"));
        
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
        TextButton botonMejoraVida = new TextButton("Mejorar Vida (" + descripcionMejoraVida() + ")", fuenteTextos);
        botonMejoraVida.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (mejoras_permitidas > 0) {
                    // Asumiendo que este método aplica el aumento de vida (si preferís usar Estadistica directamente, reemplazar por heroe.getEstadistica().aumentarMultVida();)
                    heroe.getEstadistica().aumentarMultVida();
                    botonMejoraVida.setText("Mejorar Vida (" + descripcionMejoraVida() + ")");
                    mejoras_permitidas--;
                }
            }
        });

        // DAÑO
        TextButton botonMejoraDanio = new TextButton(
                "Mejorar Daño (x" + formatearMult(heroe.getEstadistica().getMultDanio()) + ")", fuenteTextos);
        botonMejoraDanio.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (mejoras_permitidas > 0) {
                    heroe.getEstadistica().aumentarmultDanio();
                    botonMejoraDanio.setText("Mejorar Daño (x" + formatearMult(heroe.getEstadistica().getMultDanio()) + ")");
                    mejoras_permitidas--;
                }
            }
        });

        // VELOCIDAD
        TextButton botonMejoraVelocidad = new TextButton(
                "Mejorar Velocidad (x" + formatearMult(heroe.getEstadistica().getMultVelocidad()) + ")", fuenteTextos);
        botonMejoraVelocidad.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (mejoras_permitidas > 0) {
                    heroe.getEstadistica().aumentarmultVelocidad();
                    botonMejoraVelocidad.setText("Mejorar Velocidad (x" + formatearMult(heroe.getEstadistica().getMultVelocidad()) + ")");
                    mejoras_permitidas--;
                }
            }
        });

        // SALTO
        TextButton botonMejoraSalto = new TextButton(
                "Mejorar Salto (x" + formatearMult(heroe.getEstadistica().getMultSalto()) + ")", fuenteTextos);
        botonMejoraSalto.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (mejoras_permitidas > 0) {
                    heroe.getEstadistica().aumentarmultSalto();
                    botonMejoraSalto.setText("Mejorar Salto (x" + formatearMult(heroe.getEstadistica().getMultSalto()) + ")");
                    mejoras_permitidas--;
                }
            }
        });

        // VOLVER
        TextButton botonVolver = new TextButton("Volver a la pelea", fuenteTextos);
        botonVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Pasar las estadísticas actualizadas del héroe
                juego.setScreen(new Arena(
                        juego,
                        fuenteTextos,
                        jefe.getVida(),
                        intentos,
                        heroe.getEstadistica() // ← Pasar las estadísticas
                ));
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
    }

    private String descripcionMejoraVida() {
        // Mostrar el multiplicador actual sin depender de nivelMax de otra clase
        return "x" + formatearMult(heroe.getEstadistica().getMultVida());
    }

    private String formatearMult(float mult) {
        // Evita mostrar demasiados decimales (e.g., 1.0, 2.0, 3.0)
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
