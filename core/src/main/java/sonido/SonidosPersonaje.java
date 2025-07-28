package sonido;

import com.badlogic.gdx.Gdx;

public class SonidosPersonaje extends SonidoPersonajeBase {
    
	 public SonidosPersonaje() {
	        golpe = Gdx.audio.newSound(Gdx.files.internal("sonidos/ataque2.WAV"));
	        salto = Gdx.audio.newSound(Gdx.files.internal("sonidos/salto.WAV"));
	        dash = Gdx.audio.newSound(Gdx.files.internal("sonidos/dash.WAV"));
	    }
}