package movimientos;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Dash extends MovimientoBase {
    private Body cuerpo;
    private boolean ladoDerecho;
    private final float VELOCIDAD_DASH = 7f;
    
    public Dash(Body cuerpo, boolean ladoDerecho) {
        super("Dash",3, 12, 10); // Frames de inicio: 3, activos: 5, recuperación: 10
        this.cuerpo = cuerpo;
        this.ladoDerecho = ladoDerecho;
    }
    
    @Override
    public void aplicarEfecto() {
        if (estaEnFramesActivos()) {
            float direccion = ladoDerecho ? 1 : -1;
            cuerpo.setLinearVelocity(VELOCIDAD_DASH * direccion, cuerpo.getLinearVelocity().y);
        } else if (estaEnFramesRecuperacion()) {
            // Frenar gradualmente
            cuerpo.setLinearVelocity(cuerpo.getLinearVelocity().x * 0.9f, cuerpo.getLinearVelocity().y);
        }
    }
    
    public void setLadoDerecho(boolean lado) {
        this.ladoDerecho = lado;
    }
}