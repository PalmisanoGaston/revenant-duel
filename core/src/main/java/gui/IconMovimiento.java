// IconMovimiento.java
package gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class IconMovimiento extends Image {

    private boolean enCooldown = false;

    public IconMovimiento(Texture imagen) {
        super(new TextureRegionDrawable(imagen));
        setEnCooldown(false); // arranca normal
    }

    public void setEnCooldown(boolean value) {
        this.enCooldown = value;
        if (enCooldown) {
            this.setColor(Color.GRAY);
        } else {
            this.setColor(Color.WHITE);
        }
    }

    public boolean isEnCooldown() {
        return enCooldown;
    }
}
