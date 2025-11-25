// Heroe.java
package personajes;

import Interfaces.CambioVidaEventListener;
import Interfaces.MuerteEventListener;

public class Heroe extends PersonajeBase {

    public Heroe(MuerteEventListener muerteListener,
                 CambioVidaEventListener vidaListener,
                 Estadistica estadisticas) {
        super("Jugador", 100, muerteListener, vidaListener,
                new AnimacionesPersonaje(), estadisticas);
    }
}
