package sonido;

import com.badlogic.gdx.audio.Sound;

public abstract class SonidoPersonajeBase {
    protected static boolean sonidoActivo = true;
    
    protected Sound golpe;
    protected Sound salto;
    protected Sound dash;
    
    public static void setGlobalSoundEnabled(boolean enabled) {
    	sonidoActivo = enabled;
    }
    
    public static boolean isGlobalSoundEnabled() {
        return sonidoActivo;
    }
    
    public void dispose() {
        if (golpe != null) golpe.dispose();
        if (salto != null) salto.dispose();
        if (dash != null) dash.dispose();
    }

    public void playDash() {
        if (sonidoActivo && dash != null) {
            this.dash.play();
        }
    }
    
    public void playSalto() {
        if (sonidoActivo && salto != null) {
            this.salto.play();
        }
    }
    
    public void playGolpe() {
        if (sonidoActivo && golpe != null) {
            this.golpe.play();
        }
    }
}