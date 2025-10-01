package movimientos;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import utiles.ProyectilManager;

public class ProyectilBasico extends MovimientoProyectil {

    public ProyectilBasico(Body cuerpo, boolean ladoDerecho, World world, ProyectilManager proyectilManager) {
        super(cuerpo, ladoDerecho, 10, world, proyectilManager, "Proyectil Basico", 2f);
     
    }
}