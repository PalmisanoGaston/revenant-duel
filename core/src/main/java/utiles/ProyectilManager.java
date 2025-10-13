package utiles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

import movimientos.Proyectil;

import java.util.ArrayList;
import java.util.Iterator;

public class ProyectilManager extends Actor{
    private ArrayList<Proyectil> proyectiles;
    private World world;

    public ProyectilManager(World world) {
        this.world = world;
        this.proyectiles = new ArrayList<>();
    }

    public void agregarProjectil(Proyectil proyectil) {
        proyectiles.add(proyectil);
    }
    
    @Override
    public void act(float delta) {
    	super.act(delta);
        Iterator<Proyectil> iterator = proyectiles.iterator();
        while (iterator.hasNext()) {
            Proyectil proyectil = iterator.next();
            if (proyectil.estaActivo()) {
                proyectil.actualizar(delta);
                
                // Verificar si el proyectil debe ser destruido
                Body cuerpo = proyectil.getCuerpo(); // Necesitarás añadir un getter
                if (cuerpo != null && "DESTRUIDO".equals(cuerpo.getUserData())) {
                    proyectil.destruir();
                    iterator.remove();
                }
            } else {
                proyectil.destruir();
                iterator.remove();
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        for (Proyectil proyectil : proyectiles) {
            if (proyectil.estaActivo()) {
                // El método render de Proyectil probablemente sí recibe un SpriteBatch
                // Así que simplemente casteás (Batch -> SpriteBatch)
                proyectil.render((SpriteBatch) batch);
            }
        }
    }


    public void limpiar() {
        for (Proyectil proyectil : proyectiles) {
            proyectil.destruir();
        }
        proyectiles.clear();
    }

    public ArrayList<Proyectil> getProyectiles() {
        return proyectiles;
    }
}