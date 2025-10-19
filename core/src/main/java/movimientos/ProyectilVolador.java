package movimientos;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import personajes.PersonajeBase;
import utiles.ProyectilManager;

public class ProyectilVolador extends MovimientoProyectil {

    public ProyectilVolador(Body cuerpo, boolean ladoDerecho, World world, ProyectilManager proyectilManager, PersonajeBase personaje) {
        super(cuerpo, ladoDerecho, 5, world, proyectilManager, "Proyectil Basico", 5f, personaje,0.5f);
     
    }
}