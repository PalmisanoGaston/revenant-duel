package mejoras;

public enum MejorasPrueba {
	VIDA(1),
	DANIO(1),
	VELOCIDAD(1),
	SALTO(1);

    private int nivelMax = 3;
	private int multiplicador = 1;

    MejorasPrueba(int multiplicador) {
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

}
