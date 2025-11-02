package movimientos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;

import gui.IconMovimiento;
import personajes.PersonajeBase;

public class AtaqueBasico extends MovimientoAtaque {

	public AtaqueBasico(Body cuerpo, boolean ladoDerecho, PersonajeBase personaje) {
		super(cuerpo, ladoDerecho, 10,0.6f,30, 20, 8,5,12,"Ataque Comun", 1f,personaje, new IconMovimiento(new Texture("movimientos/basicAttack.png")));
	}
}
