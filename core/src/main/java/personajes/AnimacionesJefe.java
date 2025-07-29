package personajes;

import com.badlogic.gdx.graphics.Texture;

public class AnimacionesJefe extends AnimacionBase {
    
    public AnimacionesJefe() {


        Texture idleSheet = new Texture("jefe/bladeIdle.png");
        Texture runSheet = new Texture("jefe/bladeWalk.png");
        Texture jumpSheet = new Texture("jefe/bladeIdle.png");
        Texture sheetAtaque = new Texture("jefe/bladeAttack.png");
        
        idleAnimation = createAnimationFromSheet(idleSheet, 0.0455f,179,186);
        jumpAnimation = createAnimationFromSheet(jumpSheet, 0.2f,179,186);
        runAnimation = createAnimationFromSheet(runSheet, 0.0555f,185,172);
        animacionAtaque = createAnimationFromSheet(sheetAtaque,0.0183f, 397, 198);
    }
}