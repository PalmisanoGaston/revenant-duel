// Jefe.java
package personajes;

import com.badlogic.gdx.graphics.Color;

import Interfaces.CambioVidaEventListener;
import Interfaces.MuerteEventListener;

public class Jefe extends PersonajeBase {

    private boolean modoBestia = false;
    private final Color colorBestia = new Color(1f, 0.5f, 0.5f, 1f);

    public Jefe(MuerteEventListener muerteListener, CambioVidaEventListener vidaListener) {
        super("Jefe", 150, muerteListener, vidaListener,
                new AnimacionesJefe(), new Estadistica());
    }

    // Mantener compatibilidad con Arena: activa el modo bestia
    public void modoBestia() {
        this.modoBestia = true;
        this.setColor(colorBestia);
    }
}
