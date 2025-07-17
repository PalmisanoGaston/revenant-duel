package io.github.revenantduel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Bloque {
    private Texture imagen;
    private Rectangle rect;

    public Bloque(float x, float y, float ancho, float alto, Texture imagen) {
        this.imagen = imagen;
        this.rect = new Rectangle(x, y, ancho, alto);
    }

    public void render(SpriteBatch batch) {
        batch.draw(imagen, rect.x, rect.y, rect.width, rect.height);
    }

    public Rectangle getRect() {
        return rect;
    }
}
