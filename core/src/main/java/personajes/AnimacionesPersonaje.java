package personajes;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class AnimacionesPersonaje {
	
    private Animation<TextureRegion> idleAnimation;
    private float stateTime;
    private final int FRAME_WIDTH = 32;
    private final int FRAME_HEIGTH = 32;
    
    public AnimacionesPersonaje() {
        // Crear la animación con los frames (0.1f es el tiempo entre frames)
        Texture idleSheet = new Texture("personajes/idle.png");
        
        idleAnimation = createAnimationFromSheet(idleSheet, this.FRAME_WIDTH, this.FRAME_HEIGTH, 0.1f);
        
        stateTime = 0f;
    }
    
    private Animation<TextureRegion> createAnimationFromSheet(Texture sheet, int frameWidth, int frameHeight, float frameDuration) {
        TextureRegion[][] tmp = TextureRegion.split(sheet, frameWidth, frameHeight);
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

	public Animation<TextureRegion> getIdleAnimation() {
		return idleAnimation;
	}

	public void setIdleAnimation(Animation<TextureRegion> idleAnimation) {
		this.idleAnimation = idleAnimation;
	}

	public int getFRAME_WIDTH() {
		return FRAME_WIDTH;
	}

	public int getFRAME_HEIGTH() {
		return FRAME_HEIGTH;
	}
    
    
    
}
