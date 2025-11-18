package personajes;

public class Estadistica {
    private int nivelMax = 3;
    private float multVida = 1.f,
            multDanio = 1.f,
            multVelocidad = 1.f,
            multSalto = 1.f;


    public void aumentarMultVida(float incremento) {
        if (this.multVida <= this.nivelMax) {
            this.multVida += incremento;
        }
    }

    // Similar methods for other stats
    public void aumentarMultDanio(float incremento) {
        if (this.multDanio <= this.nivelMax) {
            this.multDanio += incremento;
        }
    }

    public void aumentarMultVelocidad(float incremento) {
        if (this.multVelocidad <= this.nivelMax) {
            this.multVelocidad += incremento;
        }
    }

    public void aumentarMultSalto(float incremento) {
        if (this.multSalto <= this.nivelMax) {
            this.multSalto += incremento;
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

    public void setMultVida(float multVida) {
        this.multVida = multVida;
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
