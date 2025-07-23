package utiles;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import personajes.Personaje;

public class HitBox extends Colision {
    private int daño;
    
    public HitBox(int daño) {
        this.daño = daño;
    }
    
    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        
        procesarContacto(fixtureA, fixtureB);
        procesarContacto(fixtureB, fixtureA);
    }

    private void procesarContacto(Fixture posibleHitbox, Fixture posiblePersonaje) {
        if (posibleHitbox.getUserData() instanceof HitboxInfo && 
            posiblePersonaje.getBody().getUserData() instanceof Personaje) {
            
        	HitboxInfo hitboxData = (HitboxInfo)posibleHitbox.getUserData();
            if ("HITBOX_ATAQUE".equals(hitboxData.tipo)) {
                ((Personaje)posiblePersonaje.getBody().getUserData()).recibirDaño(hitboxData.daño);
                System.out.println("Golpe conectado! Daño: " + hitboxData.daño);
            }
        }
    }
}