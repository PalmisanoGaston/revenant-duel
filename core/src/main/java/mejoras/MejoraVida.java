package mejoras;

public enum MejoraVida {
	
	
	NIVEL_1(1), NIVEL_2(2), NIVEL_3(3);
	
	
	private int multiplicador;
	
	
	private MejoraVida(int multiplicador) {
		this.multiplicador = multiplicador;
	
	}
	
	
	public int getMultiplicador() {
		return this.multiplicador;
	}
	
	
	public static int buscarNivel(MejoraVida nivel) {
		int i =0;
		
		do  {
			
			if(nivel.equals(MejoraVida.values()[i])) {
				return i;
			}
			
			i++;
			
		} while(i<MejoraVida.values().length );
		
		return -1;
	}

}
