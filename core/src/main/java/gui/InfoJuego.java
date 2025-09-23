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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import escenas.Menu;
import escenas.Principal;
import mejoras.MejorasHeroe;

public class InfoJuego implements Screen {
	
	private Principal juego;
	private Stage escena;
	private Skin fuenteTextos;
	
	public InfoJuego(Principal juego) {
		this.juego = juego;
		this.escena = new Stage(new ScreenViewport());
		
		this.fuenteTextos = new Skin(Gdx.files.internal("uiskin.json"));
		
        Table table = new Table();
        table.setFillParent(true);
        escena.addActor(table);
        
        // Título principal
        Label titulo = new Label("REVENANT DUEL", fuenteTextos);
        titulo.setFontScale(1.5f);
        
        // Introducción
        Label introLabel = new Label("INTRODUCCIÓN", fuenteTextos);
        introLabel.setFontScale(1.2f);
        
        String textoIntroduccion = "Revenant Duel es un juego de Acción 2D inspirado en juegos de pelea y otros géneros. En este, dos jugadores tendrán el rol del héroe (ágil pero débil) y del jefe (lento y poderoso). Cada vez que el héroe muere, vuelve más fuerte, mientras el jefe activa su modo bestia al ser dañado suficientemente.";
        Label introText = new Label(textoIntroduccion, fuenteTextos);
        introText.setWrap(true);
        introText.setAlignment(Align.center);
        
        // Controles
        Label controlesLabel = new Label("CONTROLES", fuenteTextos);
        controlesLabel.setFontScale(1.2f);
        
        String textoControlesHeroe = "HÉROE:\n- Movimiento Horizontal: A y D\n- Salto: ESPACIO\n- Dash: SHIFT IZQUIERDO\n- BackDash: CONTROL IZQUIERDO\n- Ataque: J";
        Label controlesHeroe = new Label(textoControlesHeroe, fuenteTextos);
        controlesHeroe.setAlignment(Align.left);
        
        String textoControlesJefe = "JEFE:\n- Movimiento Horizontal: FLECHAS IZQ/DER\n- Salto: FLECHA ARRIBA\n- Dash: SHIFT DERECHO\n- BackDash: CONTROL DERECHO\n- Ataque: M";
        Label controlesJefe = new Label(textoControlesJefe, fuenteTextos);
        controlesJefe.setAlignment(Align.left);
        
        // Botón de volver
        TextButton botonVolver = new TextButton("Volver al Menú Principal", fuenteTextos);
        botonVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) { 
                juego.setScreen(new Menu(juego));
                MejorasHeroe.restStats();
            }
        });

        // Diseño de la tabla
        table.add(titulo).padBottom(40).colspan(2);
        table.row();
        
        table.add(introLabel).padBottom(10).colspan(2);
        table.row();
        table.add(introText).width(700).padBottom(30).colspan(2);
        table.row();
        
        table.add(controlesLabel).padBottom(15).colspan(2);
        table.row();
        
        table.add(controlesHeroe).width(300).padRight(50).padBottom(30);
        table.add(controlesJefe).width(300).padBottom(30);
        table.row();
        
        table.add(botonVolver).width(250).height(60).padTop(20).colspan(2);
	}

	@Override
	public void render(float delta) {
	    Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
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
		Gdx.input.setInputProcessor(escena);
	}

	@Override
	public void pause() {
		// No implementado
	}

	@Override
	public void resume() {
		// No implementado
	}

	@Override
	public void hide() {
		// No implementado
	}
}