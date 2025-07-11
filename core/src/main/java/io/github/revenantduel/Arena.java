package io.github.revenantduel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import personajes.Personaje;

public class Arena implements Screen{
	
	private Principal juego;
	private Stage escena;
	
	public Arena(Principal juego) {
		this.juego = juego;
		this.escena = new Stage(new ScreenViewport());

	}

	@Override
	public void render(float delta) {
	    Gdx.gl.glClearColor(0, 0, 0, 1); // Limpiar con color negro
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Limpiar el buffer de color
		
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
		
	}
	
	@Override
	public void show() {
	    Gdx.input.setInputProcessor(escena); // Para permitir input si es necesario

	    Personaje heroe = new Personaje();
	    escena.addActor(heroe);
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
