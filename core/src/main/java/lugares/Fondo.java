package lugares;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public abstract class Fondo {
	

	private Animation<TextureRegion> animacion;
	private float stateTime = 0f; 
	
	
	public Fondo(Texture textura,float duracionFrame, int ancho, int alto) {
		
		this.animacion = createAnimationFromSheet(textura, duracionFrame, ancho, alto);
		
		
		
	}
	
	
	
	private Animation<TextureRegion> createAnimationFromSheet(Texture sheet, float frameDuration, int ancho, int alto) {
        TextureRegion[][] tmp = TextureRegion.split(sheet,ancho , alto);
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
	
    public void render(SpriteBatch batch, float deltaTime, int width, int height) {
        stateTime += deltaTime; 
        TextureRegion currentFrame = animacion.getKeyFrame(stateTime);
        batch.draw(currentFrame, 0, 0, width, height); 
    }
	
	
	public Animation<TextureRegion> getAnimacion() {
		return this.animacion;
	}

}
