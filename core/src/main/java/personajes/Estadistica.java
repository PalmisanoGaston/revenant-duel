package personajes;

public class Estadistica {
    private int nivelMax = 3;
    private float multVida = 1.f,
            multDanio = 1.f,
            multVelocidad = 1.f,
            multSalto = 1.f;


    public void aumentarMultVida() {
        if (this.multVida <= this.nivelMax) {
            this.multVida += 1;
        }
    }

    public void aumentarmultDanio() {
        if (this.multDanio <= this.nivelMax) {
            this.multDanio += 1;
        }
    }

    public void aumentarmultVelocidad() {
        if (this.multVelocidad <= this.nivelMax) {
            this.multVelocidad += 1;
        }
    }

    public void aumentarmultSalto() {
        if (this.multSalto <= this.nivelMax) {
            this.multSalto += 1;
        }
    }

    public float getMultDanio() {
        return this.multDanio;
    }

    public float getMultSalto() {
        return this.multSalto;
    }

    public float getMultVelocidad() {
        return this.multVelocidad;
    }

    public float getMultVida() {
        return this.multVida;
    }

	public void setMultVida(float multVida2) {
		// TODO Auto-generated method stub
		
	}

	public int getNivelMax() {
		return nivelMax;
	}

	public void setNivelMax(int nivelMax) {
		this.nivelMax = nivelMax;
	}

	public void setMultDanio(float multDanio) {
		this.multDanio = multDanio;
	}

	public void setMultVelocidad(float multVelocidad) {
		this.multVelocidad = multVelocidad;
	}

	public void setMultSalto(float multSalto) {
		this.multSalto = multSalto;
	}
}
