package movimientos;

import com.badlogic.gdx.physics.box2d.Body;

import personajes.PersonajeBase;

public class AtaqueBasico extends MovimientoAtaque {

	public AtaqueBasico(Body cuerpo, boolean ladoDerecho, PersonajeBase personaje) {
		super(cuerpo, ladoDerecho, 10,0.6f,30, 20, 8,5,12,"Ataque Comun", 1f,personaje);
	}
}
