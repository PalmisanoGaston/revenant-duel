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

import io.github.revenantduel.Arena;
import io.github.revenantduel.Principal;
import mejoras.MejoraVida;
import personajes.Jefe;
import personajes.Personaje;

public class MenuHeroe implements Screen {
	
	private Principal juego;
	private Stage escena;
	private Skin fuenteTextos;
	private Personaje heroe;
	
	
	
    public MenuHeroe(Principal juego, Personaje heroe, Jefe jefe, int intentos) {
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
                heroe.mejorarVida();
                botonMejoraVida.setText("Mejorar Vida (" + descripcionMejoraVida() + ")");
            }
        });
        
        TextButton botonVolver = new TextButton("Volver a la pelea", fuenteTextos);
        botonVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                juego.setScreen(new Arena(juego, fuenteTextos));
            }
        });
        
        table.add(botonMejoraVida).width(300).height(60).padBottom(20);
        table.row();
        table.add(botonVolver).width(200).height(50);
    }
    
    private String descripcionMejoraVida() {
        int nivelActual = MejoraVida.buscarNivel(heroe.getNivelVida());
        if (nivelActual < MejoraVida.values().length - 1) {
            return "Nivel " + (nivelActual + 2);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}
}
