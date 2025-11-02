package gui;

import com.badlogic.gdx.Game;
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

import escenas.Arena;
import escenas.Menu;
import escenas.Principal;
import sonidos.SonidoPersonajeBase;

public class MenuArena extends WidgetGroup {
    private final Game juego;
    private final Skin skin;
    private final Table tablaMenu;
    private static boolean sonidoActivado = true;
    private TextButton botonSonido;
    private final Arena arena; // Reference to arena for closing menu
    
    public MenuArena(Game juego, Skin skin, Arena arena) {
        this.juego = juego;
        this.skin = skin;
        this.arena = arena; // Store arena reference
        
        this.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        tablaMenu = new Table(skin);
        tablaMenu.defaults().pad(15);
        
        crearMenu();
        this.addActor(tablaMenu);
    }
    
    private void crearMenu() {
        this.setSize(400, 300);
        Label titulo = new Label("Opciones", skin);
        titulo.setFontScale(1.5f);
        
        TextButton botonInicio = new TextButton("Volver al inicio", skin);
        botonInicio.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                juego.setScreen(new Menu(juego));
            }
        });
        
        TextButton botonCerrar = new TextButton("Cerrar Menú", skin);
        botonCerrar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                arena.cerrarMenu(); 
            }
        });
        
        botonSonido = new TextButton(sonidoActivado ? "Silenciar Audio" : "Activar Audio", skin);
        botonSonido.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sonidoActivado = !sonidoActivado;
                botonSonido.setText(sonidoActivado ? "Silenciar Audio" : "Activar Audio");
                actualizarEstadoSonido();
            }
        });
        BotonSilenciar botonMusica = new BotonSilenciar(skin);
        
        tablaMenu.add(titulo).colspan(1).padBottom(30).row();
        tablaMenu.add(botonInicio).width(250).height(60).row();
        tablaMenu.add(botonSonido).width(250).height(60).row();
        tablaMenu.add(botonMusica).width(250).height(60).row();

        tablaMenu.add(botonCerrar).width(250).height(60).row();

        
        tablaMenu.pack();
    }
   
    private void actualizarEstadoSonido() {
        SonidoPersonajeBase.activarSonidoPersonaje(sonidoActivado);
    }

    public static boolean comprobarSonidoActivo() {
        return SonidoPersonajeBase.comprobarSonido();
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(0, 0, 0, 0.7f);
        batch.setColor(Color.WHITE);
        super.draw(batch, parentAlpha);
    }
}