// Heroe.java
package personajes;

import com.badlogic.gdx.physics.box2d.World;
import Interfaces.CambioVidaEventListener;
import Interfaces.MuerteEventListener;
import movimientos.AtaqueBasico;
import movimientos.MovimientoBase;
import movimientos.ProyectilBasico;
import sonidos.SonidosPersonaje;
import utiles.ProyectilManager;

public class Heroe extends PersonajeBase {

    private SonidosPersonaje sonidos = new SonidosPersonaje();
    private ProyectilManager proyectilManager;
    private ProyectilBasico proyectilBasico;

    public Heroe(World world, MuerteEventListener muerteListener, 
            CambioVidaEventListener vidaListener, 
            ProyectilManager proyectilManager, Estadistica estadisticas) {
   super(world, "Jugador", 100, muerteListener, vidaListener,
           new AnimacionesPersonaje(), 1, 5f, estadisticas);
        this.proyectilManager = proyectilManager;
        
        this.proyectilBasico = new ProyectilBasico(body, super.lado, world, proyectilManager,this);
        super.fuerzaSalto = 1 * (int)this.estadisticas.getMultSalto();
        super.velocidadHorizontal = 5f * this.estadisticas.getMultVelocidad();

        super.ataque = new AtaqueBasico(body, super.lado,this);
        movimientos.put("Ataque", super.ataque);
        movimientos.put("Proyectil", proyectilBasico);

    }

    @Override
    protected MovimientoBase createAtaque() {
        return new AtaqueBasico(body, super.lado,this);
    }

    // Método para manejar el ataque con proyectil
    public void realizarAtaqueProyectil() {
        if (movimientoActual == null && isGrounded()) {
            // Actualizar la dirección del proyectil antes de lanzarlo
            if (proyectilBasico != null) {
                proyectilBasico.actualizarDireccion(super.lado);
            }
            
            MovimientoBase proyectil = movimientos.get("Proyectil");
            if (proyectil != null) {
            	if(proyectil.estaListo()) {
	                movimientoActual = proyectil;
	                this.body.setLinearVelocity(0f, this.body.getLinearVelocity().y);
	                super.stateTime = 0;
	                proyectil.reiniciar();
	                onPlayProyectil();
	                proyectil.activarCooldown();
            	}
            }
        }
    }


    // (Opcional) sonidos
    @Override protected void onPlayDash()  { sonidos.playDash(); }
    @Override protected void onPlaySalto() { sonidos.playSalto(); }
    @Override protected void onPlayGolpe() { sonidos.playGolpe(); }
    protected void onPlayProyectil() { 
        // Agregar sonido de proyectil si lo tienes
        // sonidos.playProyectil();
    }

    @Override
    protected void onExtraActions() {
        // Manejar el ataque con proyectil
        if (super.enAtaqueProyectil) {
            realizarAtaqueProyectil();
            super.enAtaqueProyectil = false;
        }
       
    }

}