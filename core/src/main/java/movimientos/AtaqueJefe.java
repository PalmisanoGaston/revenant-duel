package movimientos;

import com.badlogic.gdx.physics.box2d.Body;

public class AtaqueJefe extends MovimientoAtaque {

	public AtaqueJefe(Body cuerpo, boolean ladoDerecho) {
		super(cuerpo, ladoDerecho, 100,200,100 , 24,7,51,"Ataque Jefe");
	}
}
