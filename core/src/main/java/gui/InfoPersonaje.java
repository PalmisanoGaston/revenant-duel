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

import movimientos.MovimientoBase;

public class InfoPersonaje extends WidgetGroup {
    private Label labelInfo;
    private Image imagenPersonaje;
    private String nombre;
    private int vidaActual;
    private int vidaMax;
    private Skin skin;
    private boolean esHeroe; 
    
    public InfoPersonaje(String nombre, int vidaMax, Texture textura, Skin skin, boolean esHeroe, MovimientoBase[] movimientos) {
        this.nombre = nombre;
        this.vidaMax = vidaMax;
        this.vidaActual = vidaMax;
        this.skin = skin;
        this.esHeroe = esHeroe;
        
        this.imagenPersonaje = new Image(textura);
        imagenPersonaje.setSize(48, 48); 
        this.labelInfo = new Label(nombre + ": " + vidaActual + "/" + vidaMax, skin);
        
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
        
        
        this.setSize(table.getPrefWidth(), table.getPrefHeight());
        
        Table tablaMovimientos = new Table();
        tablaMovimientos.defaults().pad(5);
        for (MovimientoBase movimiento: movimientos) {
        	tablaMovimientos.add(movimiento.getIcon()).size(32, 32);
		}
        
        table.row();
     // que ocupe ambas columnas y se centre
        table.add(tablaMovimientos)
             .colspan(2)       // ocupa las 2 columnas
             .padTop(10f)
             .expandX()        // toma el ancho disponible
             .center();        // centra el actor dentro de la celda
    
        this.addActor(table);
        
    }
    public void modificarInfo(int vida) {
        this.vidaActual = Math.max(0, Math.min(vida, vidaMax));
        this.labelInfo.setText(nombre + ": " + vidaActual + "/" + vidaMax);
    }

    // Add a new method to update max health:
    public void actualizarVidaMaxima(int nuevaVidaMax) {
        this.vidaMax = nuevaVidaMax;
        // Keep current health within new bounds
        this.vidaActual = Math.min(this.vidaActual, this.vidaMax);
        this.labelInfo.setText(nombre + ": " + vidaActual + "/" + vidaMax);
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}