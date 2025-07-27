package personajes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public abstract class AnimacionBase {

	
    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> runAnimation;
    protected Animation<TextureRegion> jumpAnimation;
    protected Animation<TextureRegion> animacionAtaque;
    protected Animation<TextureRegion> animacionMuerte;


    protected Animation<TextureRegion> createAnimationFromSheet(Texture sheet, float frameDuration, int ancho, int alto) {
        TextureRegion[][] tmp = TextureRegion.split(sheet, ancho, alto);
        Array<TextureRegion> frames = new Array<>();
        for (TextureRegion[] row : tmp) {
            for (TextureRegion frame : row) {
                frames.add(frame);
            }
        }

        if (frames.size == 0) {
            throw new IllegalArgumentException("La hoja de sprites no contiene frames o no se ha cargado correctamente: " + sheet);
        }

        return new Animation<TextureRegion>(frameDuration, frames, Animation.PlayMode.LOOP);
    }
    
    public Animation<TextureRegion> getRunAnimation() {
        return this.runAnimation;
    }

    public Animation<TextureRegion> getJumpAnimation() {
        return this.jumpAnimation;
    }
    
    public Animation<TextureRegion> getAnimacionAtaque() {
        return this.animacionAtaque;
    }

    public Animation<TextureRegion> getIdleAnimation() {
        return this.idleAnimation;
    }

    public Animation<TextureRegion> getAnimacionMuerte() {
		return animacionMuerte;
	}

	public void setIdleAnimation(Animation<TextureRegion> idleAnimation) {
        this.idleAnimation = idleAnimation;
    }


}