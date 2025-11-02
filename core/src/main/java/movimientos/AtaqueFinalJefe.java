package movimientos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;

import gui.IconMovimiento;
import personajes.PersonajeBase;

public class AtaqueFinalJefe extends MovimientoAtaque {

	public AtaqueFinalJefe(Body cuerpo, boolean ladoDerecho, PersonajeBase personaje) {
		super(cuerpo, ladoDerecho, 9999,10f,800,800 , 24,7,51,"Ataque Jefe", 3f,personaje, new IconMovimiento(new Texture("movimientos/ataqueFinalJefe.png")));
	}
}
