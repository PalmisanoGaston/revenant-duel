package utiles;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import personajes.Personaje;

public class Colision implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        
        // Verificar si uno de los fixtures es la caja sensor y el otro es el personaje
        if ((fixtureA.getBody().getUserData() == "CAJA_SENSOR" && fixtureB.getBody().getUserData() instanceof Personaje) ||
            (fixtureB.getBody().getUserData() == "CAJA_SENSOR" && fixtureA.getBody().getUserData() instanceof Personaje)) {
            System.out.println("Jugador entró en la caja!");
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        
        if ((fixtureA.getBody().getUserData() == "CAJA_SENSOR" && fixtureB.getBody().getUserData() instanceof Personaje) ||
            (fixtureB.getBody().getUserData() == "CAJA_SENSOR" && fixtureA.getBody().getUserData() instanceof Personaje)) {
            System.out.println("Jugador salió de la caja!");
        }
    }

    // ... otros métodos requeridos por la interfaz (pueden quedarse vacíos)
    @Override public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}