package utiles;

public class HitboxInfo {
	   public String tipo;
	   public int daño;
	    
	    public HitboxInfo(String tipo, int daño) {
	        this.tipo = tipo;
	        this.daño = daño;
	    }

		public String getTipo() {
			return tipo;
		}

		public int getDaño() {
			return daño;
		}
}