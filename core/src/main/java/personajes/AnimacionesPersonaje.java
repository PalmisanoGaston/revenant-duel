package personajes;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class AnimacionesPersonaje {
	
	private final int FRAME_WIDTH = 32;
    private final int FRAME_HEIGTH = 32;
	
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> runAnimation;
    private Animation<TextureRegion> jumpAnimation;
    
    
    public AnimacionesPersonaje() {
        // Crear la animación con los frames (0.1f es el tiempo entre frames)
        Texture idleSheet = new Texture("personajes/idle.png");
        Texture runSheet = new Texture("personajes/run.png");
        Texture jumpSheet = new Texture("personajes/Jump.png");
        
        idleAnimation = createAnimationFromSheet(idleSheet, 0.1f);
        jumpAnimation = createAnimationFromSheet(jumpSheet, 0.2f);
        runAnimation = createAnimationFromSheet(runSheet, 0.1f);
    }

	private Animation<TextureRegion> createAnimationFromSheet(Texture sheet, float frameDuration) {
        TextureRegion[][] tmp = TextureRegion.split(sheet, this.FRAME_WIDTH, this.FRAME_HEIGTH);
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

	public Animation<TextureRegion> getIdleAnimation() {
		return this.idleAnimation;
	}

	public void setIdleAnimation(Animation<TextureRegion> idleAnimation) {
		this.idleAnimation = idleAnimation;
	}

	public int getFRAME_WIDTH() {
		return this.FRAME_WIDTH;
	}

	public int getFRAME_HEIGTH() {
		return this.FRAME_HEIGTH;
	}
    
    
    
}
