package gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import sonidos.ControladorMusica;

public class BotonSilenciar extends TextButton {

    public BotonSilenciar(Skin skin) {
        super("Silenciar", skin);

        addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ControladorMusica.silenciarMusica();
                setText(ControladorMusica.estaSilenciado() ? "Activar Sonido" : "Silenciar");
            }
        });
    }
}
