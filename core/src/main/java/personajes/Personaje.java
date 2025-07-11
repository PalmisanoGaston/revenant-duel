package personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Personaje extends Actor {
	
	private String nombre;
	private int vida;
	private Texture skin;
	private float velocidad = 400f; // pixeles por segundo


	  public Personaje() {
	        this.skin = new Texture("personajes/idle.png"); // Asegurate de tener este archivo en assets/

	        // Definir tamaño y posición inicial
	        setSize(skin.getWidth(), skin.getHeight());
	        setPosition(100, 100); // Por ejemplo, en (100,100)
	    }

	    @Override
	    public void draw(Batch batch, float parentAlpha) {
	        batch.draw(skin, getX(), getY(), getWidth(), getHeight());
	    }

	    @Override
	    public void act(float delta) {
	        super.act(delta);
	    	float movimiento = this.velocidad * delta;
	        
	        if (Gdx.input.isKeyPressed((Input.Keys.A))) {
	        	// restar la posicion en X
	        	float posicionX = super.getX() - movimiento;
	        	System.out.println(posicionX);
	        	super.setX(posicionX);
	        	
	        } else if (Gdx.input.isKeyPressed(Input.Keys.D) ) {
	        	// sumar la posicion en X
	        	// restar la posicion en X
	        	float posicionX = super.getX() + movimiento;
	        	System.out.println(posicionX);
	        	super.setX(posicionX);
	        }
	    }

	    @Override
	    public boolean remove() {
	        skin.dispose(); // Liberar recursos cuando se quite el actor
	        return super.remove();
	
	    }
}
