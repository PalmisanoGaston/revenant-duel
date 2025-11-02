package movimientos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;

import gui.IconMovimiento;
import personajes.PersonajeBase;

public class AtaqueJefe extends MovimientoAtaque {

	public AtaqueJefe(Body cuerpo, boolean ladoDerecho, PersonajeBase personaje) {
		super(cuerpo, ladoDerecho, 100,0.6f,200,100 , 24,7,51,"Ataque Jefe", 2f,personaje, new IconMovimiento(new Texture("movimientos/ataqueJefe.png")));
	}
}
