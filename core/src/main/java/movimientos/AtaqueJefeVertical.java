package movimientos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;

import gui.IconMovimiento;
import personajes.PersonajeBase;

public class AtaqueJefeVertical extends MovimientoAtaque {

	public AtaqueJefeVertical(Body cuerpo, boolean ladoDerecho, PersonajeBase personaje) {
		super(cuerpo, ladoDerecho, 100,1.0f,50,500 , 17,11,30,"Ataque Jefe", 4f,personaje, new IconMovimiento(new Texture("movimientos/verticalHitJefe2.png")));
	}
}
