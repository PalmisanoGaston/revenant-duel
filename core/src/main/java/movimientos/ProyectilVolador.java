package movimientos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import gui.IconMovimiento;
import personajes.PersonajeBase;
import utiles.ProyectilManager;

public class ProyectilVolador extends MovimientoProyectil {

    public ProyectilVolador(Body cuerpo, boolean ladoDerecho, World world, ProyectilManager proyectilManager, PersonajeBase personaje) {
        super(cuerpo, ladoDerecho, 5, world, proyectilManager,
                "volador", "Proyectil Basico", 5f, personaje,0.5f,
                new IconMovimiento(new Texture("movimientos/proyectilVolador.png")));
     
    }
}