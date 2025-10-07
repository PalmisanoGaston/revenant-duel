package utiles;

import personajes.PersonajeBase;

public class HitboxInfo {
	   public PersonajeBase personaje;
	   public String tipo;
	   public int daño;
	    
	    public HitboxInfo(String tipo, int daño,  PersonajeBase personaje) {
	        this.tipo = tipo;
	        this.daño = daño;
	        this.personaje = personaje;
	    }

		public String getTipo() {
			return tipo;
		}

		public int getDaño() {
			return daño;
		}
		
		public PersonajeBase getPersonaje() {
			return this.personaje;
		}
		
}