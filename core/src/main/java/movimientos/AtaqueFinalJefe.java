package movimientos;

import com.badlogic.gdx.physics.box2d.Body;

import personajes.PersonajeBase;

public class AtaqueFinalJefe extends MovimientoAtaque {

	public AtaqueFinalJefe(Body cuerpo, boolean ladoDerecho, PersonajeBase personaje) {
		super(cuerpo, ladoDerecho, 9999,10f,800,800 , 24,7,51,"Ataque Jefe", 3f,personaje);
	}
}
