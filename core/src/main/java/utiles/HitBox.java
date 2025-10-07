package utiles;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import escenas.Arena;
import personajes.Jefe;
import personajes.PersonajeBase;

public class HitBox implements ContactListener {
	
	 private ProyectilManager proyectilManager;
	 
	 
	 public HitBox() {
		 this.proyectilManager = null;
	 }
	 
	 
	 public HitBox(ProyectilManager proyectilManager) {
	        this.proyectilManager = proyectilManager;
	 }
    
	 @Override
	 public void beginContact(Contact contact) {
	     Fixture a = contact.getFixtureA();
	     Fixture b = contact.getFixtureB();

	     // 1) Proyectil vs Personaje
	     if (esProyectil(a) && esPersonaje(b)) {
	         manejarColisionProyectil(a, b); // a = proyectil, b = personaje
	         return;
	     }
	     if (esProyectil(b) && esPersonaje(a)) {
	         manejarColisionProyectil(b, a); // b = proyectil, a = personaje
	         return;
	     }

	     // 2) Proyectil vs Entorno
	     if (esProyectil(a) && esEntorno(b)) {
	         manejarColisionProyectilEntorno(a); // a = proyectil
	         return;
	     }
	     if (esProyectil(b) && esEntorno(a)) {
	         manejarColisionProyectilEntorno(b); // b = proyectil
	         return;
	     }

	     // 3) Hitbox de ataque cuerpo-a-cuerpo vs Personaje (original)
	     if (esHitboxAtaque(a) && esPersonaje(b)) {
	         aplicarDaño(a, b);
	         return;
	     }
	     if (esHitboxAtaque(b) && esPersonaje(a)) {
	         aplicarDaño(b, a);
	         return;
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
        PersonajeBase caster = hitboxData.getPersonaje();
        PersonajeBase pj = (PersonajeBase)personaje.getBody().getUserData();

        if(pj.getVida() == 0){
            return;
        }

        if (pj.esInvulnerable()) {
            System.out.println("Invulnerable");
            return;
        }
        

        	pj.recibirDaño(hitboxData.getDaño() *(int) caster.getEstadistica().getMultDanio());
        
        System.out.println("Golpe conectado,  Daño: " + hitboxData.getDaño());
    }
    
    private boolean esProyectil(Fixture fixture) {
        return fixture.getUserData() instanceof HitboxInfo && 
               "PROYECTIL".equals(((HitboxInfo)fixture.getUserData()).getTipo());
    }

    private boolean esEntorno(Fixture fixture) {
        return fixture.getFilterData().categoryBits == Arena.CATEGORY_ENTORNO;
    }

    private void manejarColisionProyectil(Fixture proyectil, Fixture personaje) {
        HitboxInfo proyectilData = (HitboxInfo)proyectil.getUserData();
        PersonajeBase pj = (PersonajeBase)personaje.getBody().getUserData();
        PersonajeBase caster = proyectilData.getPersonaje();

        if(pj.getVida() == 0 || pj.esInvulnerable()){
            return;
        }

        pj.recibirDaño(proyectilData.getDaño() *(int) caster.getEstadistica().getMultDanio());
        
        System.out.println("Proyectil golpeó, Daño: " + proyectilData.getDaño());
        
        // Marcar proyectil para destrucción
        proyectil.getBody().setUserData("DESTRUIDO");
    }

    private void manejarColisionProyectilEntorno(Fixture proyectil) {
    	System.out.println("Sexo");
        // Marcar proyectil para destrucción al chocar con entorno
        proyectil.getBody().setUserData("DESTRUIDO");
    }  

	@Override
	public void endContact(Contact contact) {}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}
}
