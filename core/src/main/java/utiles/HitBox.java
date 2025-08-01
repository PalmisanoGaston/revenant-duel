package utiles;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import personajes.PersonajeBase;

public class HitBox implements ContactListener {
    
    @Override
    public void beginContact(Contact contact) {
    	
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (esHitboxAtaque(fixtureA) && esPersonaje(fixtureB)) {
            aplicarDaño(fixtureA, fixtureB);
            }

        else if (esHitboxAtaque(fixtureB) && esPersonaje(fixtureA)) {
            aplicarDaño(fixtureB, fixtureA);
        }

    }

    private boolean esHitboxAtaque(Fixture fixture) {
        return fixture.getUserData() instanceof HitboxInfo && 
               "HITBOX_ATAQUE".equals(((HitboxInfo)fixture.getUserData()).getTipo());
    }

    private boolean esPersonaje(Fixture fixture) {
        return fixture.getBody().getUserData() instanceof PersonajeBase;
    }

    private void aplicarDaño(Fixture hitbox, Fixture personaje) {

        if (personaje.getUserData() instanceof HitboxInfo) {
            return;
        }
        
        HitboxInfo hitboxData = (HitboxInfo)hitbox.getUserData();
        PersonajeBase pj = (PersonajeBase)personaje.getBody().getUserData();
        
        if (pj.esInvulnerable()) {
            System.out.println("Invulnerable");
            return;
        }
        
        pj.recibirDaño(hitboxData.getDaño());
        System.out.println("Golpe conectado,  Daño: " + hitboxData.getDaño());
    }

	@Override
	public void endContact(Contact contact) {}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}
}