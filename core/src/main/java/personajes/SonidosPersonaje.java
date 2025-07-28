package personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SonidosPersonaje {

	public Sound golpe;
	public Sound salto;
	public Sound dash;
	    
	public SonidosPersonaje () {
	    golpe = Gdx.audio.newSound(Gdx.files.internal("sonidos/ataque2.WAV"));
	    salto = Gdx.audio.newSound(Gdx.files.internal("sonidos/salto.WAV"));
	    dash = Gdx.audio.newSound(Gdx.files.internal("sonidos/dash.WAV"));
	}
	    
	public void dispose() {
	    golpe.dispose();
	    salto.dispose();
	    dash.dispose();
	}

	public void playDash() {
		this.dash.play();
	}
	
	public void playSalto() {
		this.salto.play();
	}
	
	public void playGolpe() {
		this.golpe.play();
	}

}
