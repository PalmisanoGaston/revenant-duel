package personajes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimacionesJefe extends AnimacionBase {
    
    public AnimacionesJefe() {

        Texture idleSheet = new Texture("jefe/bossidle.png");
        Texture runSheet = new Texture("jefe/bladeWalk.png");
        Texture jumpSheet = new Texture("jefe/bladeIdle.png");
        Texture sheetAtaque = new Texture("jefe/bladeAttack.png");
        Texture sheetAtaqueVertical = new Texture("jefe/bladeVerticalAttack.png");
        
        super.idleAnimation = createAnimationFromSheet(idleSheet, 0.0455f,179,186);
        super.jumpAnimation = createAnimationFromSheet(jumpSheet, 0.2f,179,186);
        super.runAnimation = createAnimationFromSheet(runSheet, 0.0555f,185,172);
        super.animacionAtaque = createAnimationFromSheet(sheetAtaque,0.0183f, 397, 198);
        super.animacionAtaqueVertical = createAnimationFromSheet(sheetAtaqueVertical,0.02f, 367, 257);
    }
 
}