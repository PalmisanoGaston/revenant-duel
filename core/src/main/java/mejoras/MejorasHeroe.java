package mejoras;

import java.util.Iterator;

public enum MejorasHeroe {
	VIDA(1),
	DANIO(1),
	VELOCIDAD(1),
	SALTO(1);

    private int nivelMax = 3;
	private int multiplicador = 1;

    MejorasHeroe(int multiplicador) {
        this.multiplicador = multiplicador;
    }
	
	public void aumentarNigger() {
		if(this.multiplicador < this.nivelMax) {
            this.multiplicador += 1;
        }
	}

    public int getNivelMax() {
        return this.nivelMax;
    }
	
	public int getMultiplicador() {
		return this.multiplicador;
	}
	
	public static void restStats() {
		for (int i = 0; i < MejorasHeroe.values().length; i++) {
			MejorasHeroe.values()[i].multiplicador = 1;
			MejorasHeroe.values()[i].nivelMax = 3;
		}
	}

}
