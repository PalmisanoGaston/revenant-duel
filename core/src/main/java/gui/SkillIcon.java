package gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple holder for skill icons so the client can render the ability bar.
 */
public class SkillIcon {
    private static final Map<String, Texture> CACHE = new HashMap<>();

    private final String name;
    private final String texturePath;
    private final Image image;
    private boolean onCooldown;

    public SkillIcon(String name, String texturePath) {
        this.name = name;
        this.texturePath = texturePath;
        this.image = new Image(getTexture());
        setOnCooldown(false);
    }

    public String getName() {
        return name;
    }

    private Texture getTexture() {
        return CACHE.computeIfAbsent(texturePath, Texture::new);
    }

    public Image getImage() {
        return image;
    }

    public void setOnCooldown(boolean onCooldown) {
        this.onCooldown = onCooldown;
        if (onCooldown) {
            image.setColor(Color.GRAY);
        } else {
            image.setColor(Color.WHITE);
        }
    }

    public boolean isOnCooldown() {
        return onCooldown;
    }
}
