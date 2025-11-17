package movimientos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import escenas.Arena;
import gui.IconMovimiento;
import personajes.PersonajeBase;
import utiles.HitboxInfo;
import utiles.ProyectilManager;

public class MovimientoProyectil extends MovimientoAtaque {
    private World world;
    private ProyectilManager proyectilManager;
    private Texture texturaProyectil;
    private boolean proyectilLanzado = false;
    private float gravedad;
    private final String tipoIdentificador;

    public MovimientoProyectil(Body cuerpo, boolean ladoDerecho, int daño, World world, ProyectilManager proyectilManager,
                               String tipoIdentificador, String nombre, float cooldown, PersonajeBase personaje,
                               float gravedad, IconMovimiento icon) {
        super(cuerpo, ladoDerecho, daño,0.6f, 20, 20, 10, 5, 15, nombre, cooldown, personaje, icon);
        this.world = world;
        this.proyectilManager = proyectilManager;
        this.texturaProyectil = new Texture("proyectil.png");
        this.gravedad = gravedad;
        this.tipoIdentificador = tipoIdentificador;
    }

    // Método para actualizar la dirección cuando el personaje cambie de lado
    public void actualizarDireccion(boolean nuevoLadoDerecho) {
        this.ladoDerecho = nuevoLadoDerecho;
    }

    @Override
    public void aplicarEfecto() {
        // Lanzar proyectil al final de los frames activos
        if (estaEnFramesActivos() && !proyectilLanzado && frameActual == framesInicio + framesActivos - 1) {
            lanzarProyectil();
            proyectilLanzado = true;
        }
    }

    private void lanzarProyectil() {
        Vector2 posicionCuerpo = cuerpo.getPosition();
        float direccion = ladoDerecho ? 1 : -1;
        System.out.println("Lanzando proyectil. Dirección: " + direccion + ", ladoDerecho: " + ladoDerecho);
        
        // Posición inicial del proyectil (ligeramente adelante del personaje)
        float x = (posicionCuerpo.x / Arena.PIXELS_TO_METERS) + (direccion * 50);
        float y = (posicionCuerpo.y / Arena.PIXELS_TO_METERS) + 20;
        
        HitboxInfo info = new HitboxInfo("PROYECTIL", daño,this.getPersonaje());
        
        Proyectil proyectil = new Proyectil(world, x, y, direccion, 0, 3f,
                                           this.tipoIdentificador, info, texturaProyectil, 20, 20, this.gravedad);
        
        proyectilManager.agregarProjectil(proyectil);
    }

    @Override
    public void reiniciar() {
        super.reiniciar();
        proyectilLanzado = false;
    }

    @Override
    public void finDeMovimiento() {
        // Limpiar si es necesario
    }
}