package personajes;

import com.badlogic.gdx.graphics.Texture;

public class AnimacionesPersonaje extends AnimacionBase {
    
    public AnimacionesPersonaje() {
        

        Texture idleSheet = new Texture("heroe/idle.png");
        Texture runSheet = new Texture("heroe/run.png");
        Texture jumpSheet = new Texture("heroe/Jump.png");
        Texture deathSheet = new Texture("heroe/death.png");
        Texture attackSheet = new Texture("heroe/attack.png");
        
        idleAnimation = createAnimationFromSheet(idleSheet, 0.1f,32,32);
        jumpAnimation = createAnimationFromSheet(jumpSheet, 0.2f,32,32);
        runAnimation = createAnimationFromSheet(runSheet, 0.1f,32,32);
        
        animacionAtaque = createAnimationFromSheet(attackSheet,0.16f, 32, 32);
        animacionMuerte = createAnimationFromSheet(deathSheet,0.3f, 32, 32);
    }
}