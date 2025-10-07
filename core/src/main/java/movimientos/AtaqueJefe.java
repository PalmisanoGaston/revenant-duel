package movimientos;

import com.badlogic.gdx.physics.box2d.Body;

import personajes.PersonajeBase;

public class AtaqueJefe extends MovimientoAtaque {

	public AtaqueJefe(Body cuerpo, boolean ladoDerecho, PersonajeBase personaje) {
		super(cuerpo, ladoDerecho, 100,200,100 , 24,7,51,"Ataque Jefe", 2f,personaje);
	}
}
