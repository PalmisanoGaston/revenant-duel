package movimientos;

import com.badlogic.gdx.physics.box2d.Body;

import personajes.PersonajeBase;

public class AtaqueJefeVertical extends MovimientoAtaque {

	public AtaqueJefeVertical(Body cuerpo, boolean ladoDerecho, PersonajeBase personaje) {
		super(cuerpo, ladoDerecho, 100,1.0f,50,500 , 24,7,51,"Ataque Jefe", 4f,personaje);
	}
}
