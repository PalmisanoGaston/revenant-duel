package gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class EscenaEspera implements Screen {
    private Game juego;
    private Stage escena;
    private Skin skin;

    public EscenaEspera(Game juego, Skin skin) {
        this.juego = juego;
        this.skin = skin;
        this.escena = new Stage(new ScreenViewport());
        
        Table table = new Table();
        table.setFillParent(true);
        escena.addActor(table);
        
        Label titulo = new Label("Esperando al Héroe", skin);
        titulo.setFontScale(2f);
        table.add(titulo).padBottom(50);
        table.row();
        
        Label mensaje = new Label("El héroe está eligiendo mejoras...", skin);
        table.add(mensaje);
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