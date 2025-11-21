// Jefe.java
package personajes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.World;

import Interfaces.CambioVidaEventListener;
import Interfaces.MuerteEventListener;
import movimientos.AtaqueBasico;
import movimientos.AtaqueFinalJefe;
import movimientos.AtaqueJefe;
import movimientos.AtaqueJefeVertical;
import movimientos.MovimientoBase;
import movimientos.Salto;
import sonidos.SonidosJefe;

public class Jefe extends PersonajeBase {

    private boolean modoBestia = false;
    private float velocidadNormal = 2f;
    private float velocidadBestia = 4f;
    private int fuerzaSaltoNormal = 10;
    private int fuerzaSaltoBestia = 20;

    private AtaqueJefeVertical ataqueVertical = new AtaqueJefeVertical(body, super.lado, this);
    private AtaqueFinalJefe ataqueFinal = new AtaqueFinalJefe(body, super.lado, this);

    
    private boolean enAtaqueVertical = false;
    private boolean enAtaqueFinal = false;
    
    private Color colorBestia = new Color(1f, 0.5f, 0.5f, 1f);
    private SonidosJefe sonidos = new SonidosJefe();

    // Si usás toggle de bestia desde el lector:
    private boolean inToggleBestia;
    public void requestToggleBestia() { this.inToggleBestia = true; }
    public void requestToggleVertical() {this.enAtaqueVertical = true;}
    public void requestToggleFinal() {this.enAtaqueFinal = true;}

    public Jefe(World world, MuerteEventListener muerteListener, CambioVidaEventListener vidaListener) {
        super(world, "Jefe", 150, muerteListener, vidaListener, 
              new AnimacionesJefe(), 10, 2f, new Estadistica()); // ← Jefe usa estadísticas nuevas
        this.velocidadNormal = 2f * this.estadisticas.getMultVelocidad();
        this.velocidadBestia = 4f * this.estadisticas.getMultVelocidad();
        this.fuerzaSaltoNormal = 10 * (int)this.estadisticas.getMultSalto();
        this.fuerzaSaltoBestia = 20 * (int)this.estadisticas.getMultSalto();
        super.ataque = new AtaqueJefe(body, super.lado,this);
        movimientos.put("Ataque", super.ataque);
        movimientos.put("AtaqueVertical", this.ataqueVertical);
        movimientos.put("AtaqueFinal",this.ataqueFinal);
    }

    @Override
    protected float getVelocidadHorizontal() {
        return modoBestia ? velocidadBestia : velocidadNormal;
    }

    @Override
    protected MovimientoBase createAtaque() {
        return new AtaqueJefe(body, super.lado,this);
    }

    @Override
    protected void prepareSalto(Salto salto) {
        salto.setFuerza(modoBestia ? fuerzaSaltoBestia : fuerzaSaltoNormal);
    }

    @Override protected void onPlayDash()  { sonidos.playDash(); }
    @Override protected void onPlaySalto() { sonidos.playSalto(); }
    @Override protected void onPlayGolpe() { sonidos.playGolpe(); }

    @Override
    protected void onAttackAnimation() {
        // Jefe tiene animación de ataque específica
        super.animacionActual = super.animacionPersonaje.getAnimacionAtaque();
        super.stateTime = 0;
    }

    private void realizarAtaqueVertical() {
        if (movimientoActual == null && isGrounded()) {
        	 ataqueVertical.setLadoDerecho(super.lado);
            
            MovimientoBase ataqueVertical = movimientos.get("AtaqueVertical");
            if (ataqueVertical != null) {
            	if(ataqueVertical.estaListo()) {
	                movimientoActual = ataqueVertical;
	                super.animacionActual = super.animacionPersonaje.getVerticalAttackAnimation();
	                this.body.setLinearVelocity(0f, this.body.getLinearVelocity().y);
	                super.stateTime = 0;
	                ataqueVertical.reiniciar();
	                ataqueVertical.activarCooldown();
            	}
            }
        }
    }    
    
  private void realizarAtaqueFinal() {
      if (movimientoActual == null && isGrounded()) {

          ataqueFinal.setLadoDerecho(super.lado);
          MovimientoBase ataqueFinal = movimientos.get("AtaqueFinal");
          if (ataqueFinal != null) {
        	 
          	if(ataqueFinal.estaListo()) {
	                movimientoActual = ataqueFinal;
	                this.body.setLinearVelocity(0f, this.body.getLinearVelocity().y);
	                super.stateTime = 0;
	                ataqueFinal.reiniciar();
	                ataqueFinal.activarCooldown();
          	}
          }
      }	  
  }  
    
    @Override
    protected void onExtraActions() {
        if (inToggleBestia) {
            this.modoBestia = true;
            this.setColor(colorBestia);
            inToggleBestia = false;
        }
       if (this.enAtaqueVertical) {
    	   this.realizarAtaqueVertical();
    	   this.enAtaqueVertical = false;
       }
       
       if (this.enAtaqueFinal){
    	   this.enAtaqueFinal = false;
    	   if(this.modoBestia) {
    		   this.realizarAtaqueFinal();
    	   }
       }
       
    }
    
 // Mantener compatibilidad con Arena: activa el modo bestia
    public void modoBestia() {
        this.modoBestia = true;
        this.setColor(colorBestia);
        // si querés que el salto base también suba al entrar en bestia:
        super.fuerzaSalto = fuerzaSaltoBestia; // opcional; tus velocidades ya dependen de modoBestia
    }

    public boolean isModoBestia() {
        return this.modoBestia;
    }

}
