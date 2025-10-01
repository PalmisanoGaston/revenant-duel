package movimientos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import gui.IconMovimiento;

public abstract class MovimientoBase {
	private String nombre;
    protected int framesInicio;
    protected int framesActivos;
    protected int framesRecuperacion;
    protected int frameActual;
    protected boolean movimientoCompletado;
    protected float cooldown = 3f;
    protected float cooldownRestante;
    protected IconMovimiento icon = new IconMovimiento(new Texture("movimientos/dash icon.png"));
    protected boolean llegoAcero = false; 
    
    public MovimientoBase(String nombre,int inicio, int activos, int recuperacion, float cooldown) {
    	this.nombre = nombre;
        this.framesInicio = inicio;
        this.framesActivos = activos;
        this.framesRecuperacion = recuperacion;
        this.frameActual = 0;
        this.movimientoCompletado = false;
        this.cooldown = cooldown;
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
        if (cooldownRestante <= 0f) {          // evita re-activar mientras ya está en cooldown
            this.cooldownRestante = cooldown;
            this.icon.setEnCooldown(true);     // gris
            this.llegoAcero = true;
        }
    }
    
    public void actualizarCooldown(float delta) {
        if (cooldownRestante > 0f) {
            cooldownRestante -= delta;
            if (cooldownRestante <= 0f) {
                cooldownRestante = 0f;
                if (llegoAcero) {
                    icon.setEnCooldown(false); // vuelve a color normal
                    llegoAcero = false;
                }
            }
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
    
    public IconMovimiento getIcon() {
    	return this.icon;
    }
    
}