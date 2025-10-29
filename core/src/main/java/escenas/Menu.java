package escenas;

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

import gui.BotonSilenciar;
import gui.InfoJuego;
import sonidos.ControladorMusica;

public class Menu implements Screen {
	
	private Game juego;
	private Stage escena;
	private Skin fuenteTextos;
	
	public Menu(Game juego) {
		this.juego = juego;
		this.escena = new Stage(new ScreenViewport());
		
		this.fuenteTextos = new Skin(Gdx.files.internal("uiskin.json"));
		
        Table table = new Table();
        table.setFillParent(true);
        escena.addActor(table);

        Label titulo = new Label("Revenant Duel", fuenteTextos);

        TextButton botonJugar = new TextButton("Jugar", fuenteTextos);
        botonJugar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                juego.setScreen(new Arena(juego,fuenteTextos));
            }
        });
        
        TextButton botonManual = new TextButton("Manual", fuenteTextos);
        botonManual.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                juego.setScreen(new InfoJuego(juego));
            }
        });
        BotonSilenciar botonMusica = new BotonSilenciar(fuenteTextos);
        table.add(titulo).padBottom(30);
        table.row();
        table.add(botonJugar).width(200).height(50);
        table.add(botonManual).width(200).height(50);
        table.add(botonMusica).width(200).height(50);
        ControladorMusica.play("musicaMenuPrincipal.mp3");
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
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}
}
