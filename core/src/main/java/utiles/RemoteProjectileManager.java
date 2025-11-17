package utiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteProjectileManager extends Actor {
    public static class ProjectileState {
        public final String type;
        public final float x;
        public final float y;

        public ProjectileState(String type, float x, float y) {
            this.type = type;
            this.x = x;
            this.y = y;
        }
    }

    private static class RemoteProjectile {
        Texture texture;
        float x;
        float y;
    }

    private final Map<String, Texture> textureCache = new HashMap<>();
    private final List<RemoteProjectile> projectiles = new ArrayList<>();

    public RemoteProjectileManager() {
        textureCache.put("basico", new Texture("proyectil.png"));
        textureCache.put("volador", new Texture("movimientos/proyectilVolador.png"));
    }

    public void updateProjectiles(List<ProjectileState> states) {
        projectiles.clear();
        if (states == null) {
            return;
        }
        for (ProjectileState state : states) {
            Texture texture = resolveTexture(state.type);
            if (texture == null) {
                continue;
            }
            RemoteProjectile projectile = new RemoteProjectile();
            projectile.texture = texture;
            projectile.x = state.x;
            projectile.y = state.y;
            projectiles.add(projectile);
        }
    }

    private Texture resolveTexture(String type) {
        if (type == null) {
            return textureCache.get("basico");
        }
        Texture texture = textureCache.get(type);
        if (texture != null) {
            return texture;
        }
        return textureCache.get("basico");
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for (RemoteProjectile projectile : projectiles) {
            Texture texture = projectile.texture;
            if (texture == null) {
                continue;
            }
            float width = texture.getWidth();
            float height = texture.getHeight();
            batch.draw(texture, projectile.x - width / 2f, projectile.y - height / 2f, width, height);
        }
    }

    public void dispose() {
        for (Texture texture : textureCache.values()) {
            texture.dispose();
        }
        textureCache.clear();
        projectiles.clear();
    }
}
