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

import io.github.revenantduel.Menu;
import io.github.revenantduel.Principal;

public class ScreenPerder implements Screen {
	
	private Principal juego;
	private Stage escena;
	private Skin fuenteTextos;
	
	public ScreenPerder(Principal juego, boolean ganador) {
		this.juego = juego;
		this.escena = new Stage(new ScreenViewport());
		
		this.fuenteTextos = new Skin(Gdx.files.internal("uiskin.json"));
		
		// Crear una tabla para organizar los elementos
        Table table = new Table();
        table.setFillParent(true);
        escena.addActor(table);

        // Crear el cartel "Menu"
        String texto =  ganador ? "El jefe a sido derrotado!" : "El heroe perdio!";
        
        Label titulo = new Label(texto, fuenteTextos);

        // Crear el botón "Jugar"
        TextButton botonJugar = new TextButton("Volver al inicio", fuenteTextos);
        botonJugar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Aquí podrías cambiar de pantalla o iniciar el juego
                juego.setScreen(new Menu(juego));
            }
        });

        // Agregar elementos a la tabla
        table.add(titulo).padBottom(30);
        table.row();
        table.add(botonJugar).width(200).height(50);
	}


	@Override
	public void render(float delta) {
	    Gdx.gl.glClearColor(0, 0, 0, 1); // color de fondo
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        escena.act(delta);
        escena.draw();
		
	}
	
	@Override
	public void resize(int width, int height) {
		escena.getViewport().update(width, height, true);
		
	}
	
	@Override
	public void dispose() {
		escena.dispose();
        fuenteTextos.dispose();
		
	}
	
	@Override
	public void show() {
		Gdx.input.setInputProcessor(escena); // para recibir input del mouse
		
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
