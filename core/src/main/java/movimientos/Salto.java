package movimientos;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Salto extends MovimientoBase {
    private Body cuerpo;
    private final float FUERZA_SALTO = 0.09f;
    private int fuerza;
    
    public Salto(Body cuerpo, int fuerza) {
        super("Salto",1, 12, 4); // Frames de inicio: 3, activos: 5, recuperación: 10
        this.cuerpo = cuerpo;
        this.fuerza = fuerza;
    }
    
    @Override
    public void aplicarEfecto() {
    	if (estaEnFramesActivos()) {
    		cuerpo.applyLinearImpulse( new Vector2(0, this.FUERZA_SALTO * fuerza), cuerpo.getWorldCenter(), true );
        } else if (estaEnFramesRecuperacion()) {
        	// Frenar gradualmente
        	cuerpo.setLinearVelocity(cuerpo.getLinearVelocity().x,cuerpo.getLinearVelocity().y * 0.85f);
        }
      }
    
    public void setFuerza(int fuerza) {
    	this.fuerza = fuerza;
    }
  }
    
    