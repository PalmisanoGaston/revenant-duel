// Personaje.java
package personajes;

import com.badlogic.gdx.physics.box2d.World;
import Interfaces.CambioVidaEventListener;
import Interfaces.MuerteEventListener;
import mejoras.MejoraVida;
import mejoras.MejorasPrueba;
import movimientos.AtaqueBasico;
import movimientos.MovimientoBase;
import sonidos.SonidosPersonaje;

public class Heroe extends PersonajeBase {

    //private MejoraVida nivelVida;
    private MejorasPrueba mejoraVida = MejorasPrueba.VIDA;
    private MejorasPrueba mejoraDanio = MejorasPrueba.DANIO;
    private MejorasPrueba mejoraVelocidad = MejorasPrueba.VELOCIDAD;
    private MejorasPrueba mejoraSalto = MejorasPrueba.SALTO;
    private SonidosPersonaje sonidos = new SonidosPersonaje();

    public Heroe(World world, MuerteEventListener muerteListener, CambioVidaEventListener vidaListener) {
        super(world, "Jugador", (int)(100 * MejorasPrueba.VIDA.getMultiplicador()), muerteListener, vidaListener,
                new AnimacionesPersonaje(), (int)(1*MejorasPrueba.SALTO.getMultiplicador()) );
        // Si querés mantener el mapa, podés ignorar "Ataque" aquí porque usamos createAtaque()
        // movimientos.put("Ataque", new AtaqueBasico(body, lado));
    }

    @Override
    protected float getVelocidadHorizontal() {
        return 5f;
    }

    @Override
    protected MovimientoBase createAtaque() {
        return new AtaqueBasico(body, lado);
    }

    // (Opcional) sonidos
    @Override protected void onPlayDash()  { sonidos.playDash(); }
    @Override protected void onPlaySalto() { sonidos.playSalto(); }
    @Override protected void onPlayGolpe() { sonidos.playGolpe(); }

    public int getNivelVida() { return (int)this.mejoraVida.getMultiplicador(); }

    public void mejorarVida() {
//        int nivelActual = MejoraVida.buscarNivel(this.nivelVida);
//        if (nivelActual < MejoraVida.values().length - 1) {
//            this.nivelVida = MejoraVida.values()[nivelActual + 1];
//        }

        this.mejoraVida.aumentarNigger();

    }
}
