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
    protected Animation<TextureRegion> animacionAtaqueVertical;

    public Animation<TextureRegion> createAnimationFromSheet(Texture sheet, float frameDuration,
                                                             int frameWidth, int frameHeight,
                                                             int rows, int[] colsPerRow) {

        TextureRegion[][] tmp = TextureRegion.split(sheet, frameWidth, frameHeight);
        int totalFrames = 0;

        for (int cols : colsPerRow) {
            totalFrames += cols;
        }

        TextureRegion[] frames = new TextureRegion[totalFrames];
        int frameIndex = 0;

        for (int row = 0; row < rows; row++) {
            int colsInRow = colsPerRow[row];
            for (int col = 0; col < colsInRow; col++) {
                if (frameIndex < totalFrames) {
                    frames[frameIndex++] = tmp[row][col];
                }
            }
        }

        return new Animation<TextureRegion>(frameDuration, frames);
    }

    public Animation<TextureRegion> createAnimationFromSheet(Texture sheet, float frameDuration,
                                                             int frameWidth, int frameHeight) {
        TextureRegion[][] tmp = TextureRegion.split(sheet, frameWidth, frameHeight);
        int rows = tmp.length;
        int cols = tmp[0].length;
        TextureRegion[] frames = new TextureRegion[rows * cols];
        int index = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                frames[index++] = tmp[i][j];
            }
        }

        return new Animation<TextureRegion>(frameDuration, frames);
    }
    

				    
    public Animation<TextureRegion> getVerticalAttackAnimation(){
    	return this.animacionAtaqueVertical;
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