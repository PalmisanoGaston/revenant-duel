package movimientos;

public abstract class MovimientoBase {
	private String nombre;
    protected int framesInicio;
    protected int framesActivos;
    protected int framesRecuperacion;
    protected int frameActual;
    protected boolean movimientoCompletado;
    protected float cooldown = 3f;
    protected float cooldownRestante;
    
    public MovimientoBase(String nombre,int inicio, int activos, int recuperacion) {
    	this.nombre = nombre;
        this.framesInicio = inicio;
        this.framesActivos = activos;
        this.framesRecuperacion = recuperacion;
        this.frameActual = 0;
        this.movimientoCompletado = false;
    }
    
    public void actualizar() {
        frameActual++;
        if (frameActual >= framesInicio + framesActivos + framesRecuperacion) {
            movimientoCompletado = true;
            finDeMovimiento();
        }
    }
    
    public boolean estaListo() {
        return cooldownRestante <= 0;
    }
    
    public void activarCooldown() {
        this.cooldownRestante = cooldown;
    }
    
    
    public void actualizarCooldown(float delta) {
        if (cooldownRestante > 0) {
            cooldownRestante -= delta;
        }
    }

    
    public boolean estaEnFramesInicio() {
        return frameActual < framesInicio;
    }
    
    public boolean estaEnFramesActivos() {
        return frameActual >= framesInicio && frameActual < framesInicio + framesActivos;
    }
    
    public boolean estaEnFramesRecuperacion() {
        return frameActual >= framesInicio + framesActivos && !movimientoCompletado;
    }
    
    public boolean estaCompletado() {
        return movimientoCompletado;
    }
    
    public void finDeMovimiento() {}
    
    public void reiniciar() {
        frameActual = 0;
        movimientoCompletado = false;
    }
    
    public abstract void aplicarEfecto();
    
    public String getNombre() {
    	return this.nombre;
    }
    
}