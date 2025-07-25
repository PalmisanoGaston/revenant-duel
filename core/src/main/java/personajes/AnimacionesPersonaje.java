package personajes;

import com.badlogic.gdx.graphics.Texture;

public class AnimacionesPersonaje extends AnimacionBase {
    
    public AnimacionesPersonaje() {
        

        Texture idleSheet = new Texture("personajes/idle.png");
        Texture runSheet = new Texture("personajes/run.png");
        Texture jumpSheet = new Texture("personajes/Jump.png");
        
        idleAnimation = createAnimationFromSheet(idleSheet, 0.1f,32,32);
        jumpAnimation = createAnimationFromSheet(jumpSheet, 0.2f,32,32);
        runAnimation = createAnimationFromSheet(runSheet, 0.1f,32,32);
    }
}