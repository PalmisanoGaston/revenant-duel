package movimientos;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import escenas.Arena;

import com.badlogic.gdx.physics.box2d.FixtureDef;

import utiles.HitBox;
import utiles.HitboxInfo;

public abstract class MovimientoAtaque extends MovimientoBase {
    private Body cuerpo;
    private boolean ladoDerecho;
    private int daño;
    private Fixture hitboxFixture;
    private HitBox hitBoxHandler;
    private int largo;
    private int ancho;

    public MovimientoAtaque(Body cuerpo, boolean ladoDerecho, int daño, int ancho,int largo, int fInicio, int fActivos, int fRecuperacion, String nombre) {
        super(nombre, fInicio, fActivos, fRecuperacion);
        this.cuerpo = cuerpo;
        this.ladoDerecho = ladoDerecho;
        this.daño = daño;
        this.ancho = ancho;
        this.largo = largo;
        this.hitBoxHandler = new HitBox();
    }
    
    @Override
    public void actualizar() {
        super.actualizar();
    }

	public void aplicarEfecto() {
		
		if (estaEnFramesActivos() && hitboxFixture == null) {
			crearHitbox();
	    } else if (!estaEnFramesActivos() && hitboxFixture != null) {
	    	eliminarHitbox();
	    }
	}
    
	protected void crearHitbox() {
		
	    PolygonShape shape = new PolygonShape();
	    
	    // Convertir ancho y largo de píxeles a metros
	    float anchoMetros = this.ancho * Arena.PIXELS_TO_METERS;
	    float largoMetros = this.largo * Arena.PIXELS_TO_METERS;
	    
	    // Posición relativa (1.5 metros hacia adelante/atrás según la dirección)
	    Vector2 posicionRelativa = new Vector2(ladoDerecho ? 0.6f : -0.6f, 0);
	    
	    shape.setAsBox(anchoMetros, largoMetros, posicionRelativa, 0);
	    
	    FixtureDef fixtureDef = new FixtureDef();
	    fixtureDef.shape = shape;
	    fixtureDef.isSensor = true;
	    
	    // Crea la hitbox como un fixture adicional del cuerpo
	    hitboxFixture = cuerpo.createFixture(fixtureDef);
	    hitboxFixture.setUserData(new HitboxInfo("HITBOX_ATAQUE", this.daño));
	}
    
	protected void eliminarHitbox() {
		if (hitboxFixture != null && cuerpo != null) {
			try {
				// Verificamos que la fixture aún exista
	            	if(cuerpo.getFixtureList().contains(hitboxFixture, true)) {
	            		cuerpo.destroyFixture(hitboxFixture);
	                }
			} catch (Exception e) {
				// El cuerpo podría haber sido eliminado
				System.out.println("Warning: Intento de eliminar hitbox de cuerpo ya destruido");
			}
	        	hitboxFixture = null;
	        }
	    }
    
	public void setLadoDerecho(boolean lado) {
		this.ladoDerecho = lado;
    }
    
    @Override
    public void reiniciar() {
    	
    	super.reiniciar();
    	
        if(this.cuerpo != null) {
        	eliminarHitbox();
        }
    }
}