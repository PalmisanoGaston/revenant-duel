package gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.github.revenantduel.Menu;
import io.github.revenantduel.Principal;

public class MenuArena extends WidgetGroup {
    
    private final Principal juego;
    private final Skin skin;
    private final Table tablaMenu;
    
    public MenuArena(Principal juego, Skin skin) {
        this.juego = juego;
        this.skin = skin;
        
        // Configurar tamaño del widget para ocupar toda la pantalla
        this.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        // Crear tabla principal para el menú
        tablaMenu = new Table(skin);
        tablaMenu.defaults().pad(15);
        
        // Configurar el menú
        crearMenu();
        this.addActor(tablaMenu);
    }
    
    private void crearMenu() {
    	  this.setSize(400, 300);
        // Título
        Label titulo = new Label("Opciones", skin);
        titulo.setFontScale(1.5f);
        
        // Botón Volver al Inicio
        TextButton botonInicio = new TextButton("Volver al inicio", skin);
        botonInicio.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                juego.setScreen(new Menu(juego));
            }
        });
        
        // Botón Silenciar Audio
        TextButton botonSonido = new TextButton("Silenciar Audio", skin);
        botonSonido.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Lógica para silenciar/activar audio
            }
        });
        
        // Botón Volver al Juego
        TextButton botonVolver = new TextButton("Volver al juego", skin);
        botonVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                remove();
            }
        });
        
        // Organizar elementos en la tabla
        tablaMenu.add(titulo).colspan(1).padBottom(30).row();
        tablaMenu.add(botonInicio).width(250).height(60).row();
        tablaMenu.add(botonSonido).width(250).height(60).row();
        tablaMenu.add(botonVolver).width(250).height(60);
        
        // Asegurarse de que la tabla calcule su tamaño preferido
        tablaMenu.pack();
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Dibujar fondo oscuro semi-transparente
        batch.setColor(0, 0, 0, 0.7f);
        batch.setColor(Color.WHITE);
        
        super.draw(batch, parentAlpha);
    }
}