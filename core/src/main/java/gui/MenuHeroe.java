package gui;

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
import mejoras.MejorasHeroe;
import personajes.Jefe;
import personajes.Heroe;

public class MenuHeroe implements Screen {
	
	private Principal juego;
	private Stage escena;
	private Skin fuenteTextos;
	private Heroe heroe;
	private int mejoras_permitidas = 1; 
	
	
    public MenuHeroe(Principal juego, Heroe heroe, Jefe jefe, int intentos) {
        this.juego = juego;
        this.escena = new Stage(new ScreenViewport());
        this.heroe = heroe;
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
        
        TextButton botonMejoraVida = new TextButton("Mejorar Vida (" + descripcionMejoraVida() + ")", fuenteTextos);
        botonMejoraVida.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	if(mejoras_permitidas > 0) {
            		heroe.mejorarVida();
                    botonMejoraVida.setText("Mejorar Vida (" + descripcionMejoraVida() + ")");
                    mejoras_permitidas--;
            	}
            }
        });
        
        TextButton botonMejoraDanio = new TextButton("Mejorar Daño (Nivel " + MejorasHeroe.DANIO.getMultiplicador() + ")", fuenteTextos);
        botonMejoraDanio.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(mejoras_permitidas > 0) {
                    MejorasHeroe.DANIO.aumentarNivel(); // aumenta el nivel
                    botonMejoraDanio.setText("Mejorar Daño (Nivel " + MejorasHeroe.DANIO.getMultiplicador() + ")");
                    mejoras_permitidas--;
                }
            }
        });

        TextButton botonMejoraVelocidad = new TextButton("Mejorar Velocidad (Nivel " + MejorasHeroe.VELOCIDAD.getMultiplicador() + ")", fuenteTextos);
        botonMejoraVelocidad.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(mejoras_permitidas > 0) {
                    MejorasHeroe.VELOCIDAD.aumentarNivel();
                    botonMejoraVelocidad.setText("Mejorar Velocidad (Nivel " + MejorasHeroe.VELOCIDAD.getMultiplicador() + ")");
                    mejoras_permitidas--;
                }
            }
        });

        TextButton botonMejoraSalto = new TextButton("Mejorar Salto (Nivel " + MejorasHeroe.SALTO.getMultiplicador() + ")", fuenteTextos);
        botonMejoraSalto.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(mejoras_permitidas > 0) {
                    MejorasHeroe.SALTO.aumentarNivel();
                    botonMejoraSalto.setText("Mejorar Salto (Nivel " + MejorasHeroe.SALTO.getMultiplicador() + ")");
                    mejoras_permitidas--;
                }
            }
        });
        
        TextButton botonVolver = new TextButton("Volver a la pelea", fuenteTextos);
        botonVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                juego.setScreen(new Arena(juego, fuenteTextos, MejorasHeroe.VIDA.getMultiplicador(),jefe.getVida(),intentos));
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
        int nivelActual = MejorasHeroe.VIDA.getMultiplicador();
        if (nivelActual < MejorasHeroe.VIDA.getNivelMax()) {
            return "Nivel " + (nivelActual);
        }
        return "Máximo nivel alcanzado";
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        escena.act(delta);
        escena.draw();
    }
	
	@Override
	public void resize(int width, int height) {
		
	}
	
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
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}
}
