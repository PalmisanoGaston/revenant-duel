package movimientos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import gui.IconMovimiento;
import personajes.PersonajeBase;
import utiles.ProyectilManager;

public class ProyectilBasico extends MovimientoProyectil {

    public ProyectilBasico(Body cuerpo, boolean ladoDerecho, World world, ProyectilManager proyectilManager, PersonajeBase personaje) {
        super(cuerpo, ladoDerecho, 10, world, proyectilManager,
                "basico", "Proyectil Basico", 2f, personaje,0f,
                new IconMovimiento(new Texture("proyectil.png")));
    }
}