// Jefe.java
package personajes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.World;

import Interfaces.CambioVidaEventListener;
import Interfaces.MuerteEventListener;
import movimientos.AtaqueBasico;
import movimientos.AtaqueJefe;
import movimientos.MovimientoBase;
import movimientos.Salto;
import sonidos.SonidosJefe;

public class Jefe extends PersonajeBase {

    private boolean modoBestia = false;
    private float velocidadNormal = 2f;
    private float velocidadBestia = 4f;
    private int fuerzaSaltoNormal = 10;
    private int fuerzaSaltoBestia = 20;

    private Color colorBestia = new Color(1f, 0.5f, 0.5f, 1f);
    private SonidosJefe sonidos = new SonidosJefe();

    // Si usás toggle de bestia desde el lector:
    private boolean inToggleBestia;
    public void requestToggleBestia() { this.inToggleBestia = true; }

    public Jefe(World world, MuerteEventListener muerteListener, CambioVidaEventListener vidaListener) {
        super(world, "Jefe", 150, muerteListener, vidaListener, 
              new AnimacionesJefe(), 10, 2f, new Estadistica()); // ← Jefe usa estadísticas nuevas
        this.velocidadNormal = 2f * this.estadisticas.getMultVelocidad();
        this.velocidadBestia = 4f * this.estadisticas.getMultVelocidad();
        this.fuerzaSaltoNormal = 10 * (int)this.estadisticas.getMultSalto();
        this.fuerzaSaltoBestia = 20 * (int)this.estadisticas.getMultSalto();
        this.ataque = new AtaqueJefe(body, lado,this);
        movimientos.put("Ataque", ataque);
    }

    @Override
    protected float getVelocidadHorizontal() {
        return modoBestia ? velocidadBestia : velocidadNormal;
    }

    @Override
    protected MovimientoBase createAtaque() {
        return new AtaqueJefe(body, lado,this);
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
        this.animacionActual = this.animacionPersonaje.getAnimacionAtaque();
        this.stateTime = 0;
    }

    @Override
    protected void onExtraActions() {
        if (inToggleBestia) {
            this.modoBestia = true;
            this.setColor(colorBestia);
            inToggleBestia = false;
        }
    }
    
 // Mantener compatibilidad con Arena: activa el modo bestia
    public void modoBestia() {
        this.modoBestia = true;
        this.setColor(colorBestia);
        // si querés que el salto base también suba al entrar en bestia:
        this.fuerzaSalto = fuerzaSaltoBestia; // opcional; tus velocidades ya dependen de modoBestia
    }

}
