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
        public final boolean facingRight; // Add direction

        public ProjectileState(String type, float x, float y, boolean facingRight) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.facingRight = facingRight;
        }
    }

    private static class RemoteProjectile {
        Texture texture;
        float x;
        float y;
        float width;
        float height;
        boolean facingRight; // Store direction
    }

    private final Map<String, Texture> textureCache = new HashMap<>();
    private final Map<String, Float> projectileSizes = new HashMap<>();
    private final List<RemoteProjectile> projectiles = new ArrayList<>();

    public RemoteProjectileManager() {
        textureCache.put("basico", new Texture("proyectil.png"));
        textureCache.put("volador", new Texture("movimientos/proyectilVolador.png"));
        
        projectileSizes.put("basico", 20f);
        projectileSizes.put("volador", 20f);
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
            projectile.facingRight = state.facingRight; // Store direction
            
            Float size = projectileSizes.get(state.type);
            if (size != null) {
                projectile.width = size;
                projectile.height = size;
            } else {
                projectile.width = 20f;
                projectile.height = 20f;
            }
            
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
            
            float width = projectile.width;
            float height = projectile.height;
            
            // Handle texture flipping based on direction
            if (!projectile.facingRight) {
                // When facing left, we need to flip the texture
                batch.draw(texture, 
                          projectile.x + width / 2f,  // Start from right edge
                          projectile.y - height / 2f, 
                          -width, height);           // Negative width flips horizontally
            } else {
                // Normal drawing for right direction
                batch.draw(texture, 
                          projectile.x - width / 2f, 
                          projectile.y - height / 2f, 
                          width, height);
            }
        }
    }

    public void dispose() {
        for (Texture texture : textureCache.values()) {
            texture.dispose();
        }
        textureCache.clear();
        projectileSizes.clear();
        projectiles.clear();
    }
}