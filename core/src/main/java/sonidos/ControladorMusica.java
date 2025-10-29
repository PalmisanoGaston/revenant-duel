package sonidos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class ControladorMusica {

    private static Music musicaActual;
    private static boolean silenciar = false;

    public static void play(String path) {
        parar(); 

        musicaActual = Gdx.audio.newMusic(Gdx.files.internal(path));
        musicaActual.setLooping(true);
        musicaActual.setVolume(silenciar ? 0 : 0.5f);
        musicaActual.play();
    }

    public static void parar() {
        if (musicaActual != null) {
        	musicaActual.stop();
        	musicaActual.dispose();
        	musicaActual = null;
        }
    }

    public static void silenciarMusica() {
    	silenciar = !silenciar;

        if (musicaActual != null) {
        	musicaActual.setVolume(silenciar ? 0 : 0.5f);
        }
    }

    public static boolean estaSilenciado() {
        return silenciar;
    }
}