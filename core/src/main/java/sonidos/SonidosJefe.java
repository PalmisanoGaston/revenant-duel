package sonidos;

import com.badlogic.gdx.Gdx;

public class SonidosJefe extends SonidoPersonajeBase {
    
    public SonidosJefe() {
        golpe = Gdx.audio.newSound(Gdx.files.internal("sonidos/ataque1.WAV"));
        salto = Gdx.audio.newSound(Gdx.files.internal("sonidos/salto.WAV"));
        dash = Gdx.audio.newSound(Gdx.files.internal("sonidos/dash.WAV"));
    }
}