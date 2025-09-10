package mejoras;

public enum MejorasPrueba {
	VIDA(),
	DANIO(),
	VELOCIDAD(),
	SALTO();
	
	private float multiplicador = 1.0f;
	
	public void aumentarNigger() {
		this.multiplicador += 1;
	}
	
	public float getMultiplicador() {
		return this.multiplicador;
	}

}
