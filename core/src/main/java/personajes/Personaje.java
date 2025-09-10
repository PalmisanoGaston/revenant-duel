// Personaje.java
package personajes;

import com.badlogic.gdx.physics.box2d.World;
import Interfaces.CambioVidaEventListener;
import Interfaces.MuerteEventListener;
import mejoras.MejoraVida;
import movimientos.AtaqueBasico;
import movimientos.MovimientoBase;
import sonidos.SonidosPersonaje;

public class Personaje extends PersonajeBase {

    private MejoraVida nivelVida;
    private SonidosPersonaje sonidos = new SonidosPersonaje();

    public Personaje(World world, MuerteEventListener muerteListener, CambioVidaEventListener vidaListener, MejoraVida nivelVida) {
        super(world, "Jugador", 100 * nivelVida.getMultiplicador(), muerteListener, vidaListener, new AnimacionesPersonaje(), 1);
        this.nivelVida = nivelVida;
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

    public MejoraVida getNivelVida() { return nivelVida; }

    public void mejorarVida() {
        int nivelActual = MejoraVida.buscarNivel(this.nivelVida);
        if (nivelActual < MejoraVida.values().length - 1) {
            this.nivelVida = MejoraVida.values()[nivelActual + 1];
        }
    }
}
