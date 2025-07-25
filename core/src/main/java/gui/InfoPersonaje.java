package gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class InfoPersonaje extends WidgetGroup {
    private Label labelInfo;
    private Image imagenPersonaje;
    private String nombre;
    private int vidaActual;
    private int vidaMax;
    private Skin skin;
    private boolean esHeroe; // Para determinar la alineación
    
    public InfoPersonaje(String nombre, int vidaMax, Texture textura, Skin skin, boolean esHeroe) {
        this.nombre = nombre;
        this.vidaMax = vidaMax;
        this.vidaActual = vidaMax;
        this.skin = skin;
        this.esHeroe = esHeroe;
        
        // Configurar imagen del personaje con escalado
        this.imagenPersonaje = new Image(textura);
        imagenPersonaje.setSize(48, 48); // Tamaño más pequeño
        

        
        
        // Configurar label de información
        this.labelInfo = new Label(nombre + ": " + vidaActual + "/" + vidaMax, skin);
        
        // Organizar elementos en una tabla para mejor control
        Table table = new Table();
        table.defaults().pad(5);
        
        if (esHeroe) {
            // Para héroe: imagen a la izquierda
            table.add(imagenPersonaje).size(imagenPersonaje.getWidth(), imagenPersonaje.getHeight());
            table.add(labelInfo);
        } else {
            // Para jefe: imagen a la derecha
            table.add(labelInfo);
            table.add(imagenPersonaje).size(imagenPersonaje.getWidth(), imagenPersonaje.getHeight());
        }
        
        this.addActor(table);
        this.setSize(table.getPrefWidth(), table.getPrefHeight());
    }
    
    public void modificarInfo(int vida) {
        this.vidaActual = Math.max(0, Math.min(vida, vidaMax));
        this.labelInfo.setText(nombre + ": " + vidaActual + "/" + vidaMax);
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}