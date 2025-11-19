package movimientos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import escenas.Arena;
import utiles.HitboxInfo;

public class Proyectil {
    private Vector2 posicion;
    private Vector2 velocidad;
    private Body cuerpo;
    private Texture textura;
    private boolean activo = true;
    private HitboxInfo info;
    private float ancho;
    private float alto;
    private final String tipo;

    // Constantes para las categorías (deben coincidir con Arena.java)
    public static final short CATEGORY_PROYECTIL = 0x0004;  // Proyectil
    public static final short CATEGORY_PERSONAJE = 0x0001;  // Personaje
    public static final short CATEGORY_ENTORNO   = 0x0002;  // Entorno

    public Proyectil(World world, float x, float y, float dirX, float dirY, float velocidad,
                     String tipo, HitboxInfo info, Texture textura, float ancho, float alto, float gravedad) {
        this.posicion = new Vector2(x, y);
        this.velocidad = new Vector2(dirX, dirY).nor().scl(velocidad);
        this.info = info;
        this.textura = textura;
        this.ancho = ancho;
        this.alto = alto;
        this.tipo = tipo;

        crearCuerpo(world,gravedad);
    }

    private void crearCuerpo(World world, float gravedad) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(posicion.x * Arena.PIXELS_TO_METERS, posicion.y * Arena.PIXELS_TO_METERS);
        bodyDef.fixedRotation = true;
        bodyDef.bullet = true;
        bodyDef.gravityScale = gravedad;

        cuerpo = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(ancho * Arena.PIXELS_TO_METERS / 2, alto * Arena.PIXELS_TO_METERS / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        
        // CORRECCIÓN: El proyectil es de categoría CATEGORY_PROYECTIL (0x0004)
        fixtureDef.filter.categoryBits = CATEGORY_PROYECTIL;
        
        // CORRECCIÓN: El proyectil choca con PERSONAJES y ENTORNO
        // Usamos OR bit a bit para combinar ambas categorías
        fixtureDef.filter.maskBits = CATEGORY_PERSONAJE | CATEGORY_ENTORNO;

        Fixture fixture = cuerpo.createFixture(fixtureDef);
        fixture.setUserData(info);

        shape.dispose();

        // Aplicar velocidad inicial
        cuerpo.setLinearVelocity(velocidad.x, velocidad.y);
    }

    public void actualizar(float delta) {
        if (!activo) return;
        
        posicion.set(cuerpo.getPosition().x / Arena.PIXELS_TO_METERS, 
                    cuerpo.getPosition().y / Arena.PIXELS_TO_METERS);
    }

    public void render(SpriteBatch batch) {
        if (!activo) return;
        
        batch.draw(textura, 
                  posicion.x - ancho/2, 
                  posicion.y - alto/2, 
                  ancho, alto);
    }

    public void destruir() {
        if (cuerpo != null) {
            cuerpo.getWorld().destroyBody(cuerpo);
            cuerpo = null;
        }
        activo = false;
    }

    public boolean estaActivo() {
        return activo && cuerpo != null;
    }

    public Vector2 getPosicion() {
        return posicion;
    }

    public String getTipo() {
        return tipo;
    }

    public void dispose() {
        if (textura != null) {
            textura.dispose();
        }
    }

    public Body getCuerpo() {
        return this.cuerpo;
    }

    public Vector2 getVelocidad() {
        if (cuerpo != null) {
            return cuerpo.getLinearVelocity();
        }
        return this.velocidad;
    }


}