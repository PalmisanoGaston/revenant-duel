// Heroe.java
package personajes;

import com.badlogic.gdx.physics.box2d.World;
import Interfaces.CambioVidaEventListener;
import Interfaces.MuerteEventListener;
import mejoras.MejorasHeroe;
import movimientos.AtaqueBasico;
import movimientos.MovimientoBase;
import movimientos.ProyectilBasico;
import sonidos.SonidosPersonaje;
import utiles.ProyectilManager;

public class Heroe extends PersonajeBase {

    private MejorasHeroe mejoraVida = MejorasHeroe.VIDA;
    private MejorasHeroe mejoraDanio = MejorasHeroe.DANIO;
    private MejorasHeroe mejoraVelocidad = MejorasHeroe.VELOCIDAD;
    private MejorasHeroe mejoraSalto = MejorasHeroe.SALTO;
    private SonidosPersonaje sonidos = new SonidosPersonaje();
    private ProyectilManager proyectilManager;
    private ProyectilBasico proyectilBasico;

    public Heroe(World world, MuerteEventListener muerteListener, CambioVidaEventListener vidaListener, ProyectilManager proyectilManager) {
        super(world, "Jugador", (int)(100 * MejorasHeroe.VIDA.getMultiplicador()), muerteListener, vidaListener,
                new AnimacionesPersonaje(), (int)(1*MejorasHeroe.SALTO.getMultiplicador()) );
        this.proyectilManager = proyectilManager;
        
        this.proyectilBasico = new ProyectilBasico(body, lado, world, proyectilManager);
        
        this.ataque = new AtaqueBasico(body, lado);
        movimientos.put("Ataque", ataque);
        movimientos.put("Proyectil", proyectilBasico);

    }

    @Override
    protected float getVelocidadHorizontal() {
        return 5f * mejoraVelocidad.getMultiplicador();
    }

    @Override
    protected MovimientoBase createAtaque() {
        return new AtaqueBasico(body, lado);
    }

    // Método para manejar el ataque con proyectil
    public void realizarAtaqueProyectil() {
        if (movimientoActual == null && isGrounded()) {
            // Actualizar la dirección del proyectil antes de lanzarlo
            if (proyectilBasico != null) {
                proyectilBasico.actualizarDireccion(lado);
            }
            
            MovimientoBase proyectil = movimientos.get("Proyectil");
            if (proyectil != null) {
            	if(proyectil.estaListo()) {
	                movimientoActual = proyectil;
	                this.stateTime = 0;
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
        if (enAtaqueProyectil) {
            realizarAtaqueProyectil();
            enAtaqueProyectil = false;
        }
       
    }

    public int getNivelVida() { return (int)this.mejoraVida.getMultiplicador(); }

    public void mejorarVida() {
        this.mejoraVida.aumentarNivel();
    }
}