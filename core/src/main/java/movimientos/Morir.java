package movimientos;

import personajes.Personaje;
import personajes.PersonajeBase;

public class Morir extends MovimientoBase {

	private PersonajeBase personaje;
	
	public Morir(PersonajeBase personaje) {
		super("Morir", 1, 0, 100);
		this.personaje = personaje;
		
	}

	@Override
	public void aplicarEfecto() {
		
	}
	
	@Override 
	public void finDeMovimiento() {
		this.personaje.getMuerteEventListener().onPersonajeMuerto(this.personaje);
	}

}
