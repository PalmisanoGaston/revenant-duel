package gui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import java.util.List;

public class InfoPersonaje extends WidgetGroup {
    private Label labelInfo;
    private Image imagenPersonaje;
    private String nombre;
    private int vidaActual;
    private int vidaMax;
    private final List<SkillIcon> skillIcons;

    public InfoPersonaje(String nombre, int vidaMax, Texture textura, Skin skin, boolean esHeroe,
                         List<SkillIcon> skillIcons) {
        this.nombre = nombre;
        this.vidaMax = vidaMax;
        this.vidaActual = vidaMax;
        this.skillIcons = skillIcons;

        this.imagenPersonaje = new Image(textura);
        imagenPersonaje.setSize(48, 48);
        this.labelInfo = new Label(nombre + ": " + vidaActual + "/" + vidaMax, skin);

        Table table = new Table();
        table.defaults().pad(5);

        if (esHeroe) {
            table.add(imagenPersonaje).size(imagenPersonaje.getWidth(), imagenPersonaje.getHeight());
            table.add(labelInfo);
        } else {
            table.add(labelInfo);
            table.add(imagenPersonaje).size(imagenPersonaje.getWidth(), imagenPersonaje.getHeight());
        }

        this.setSize(table.getPrefWidth(), table.getPrefHeight());

        Table skillTable = new Table();
        skillTable.defaults().pad(5);
        if (skillIcons != null) {
            for (SkillIcon skillIcon : skillIcons) {
                skillTable.add(skillIcon.getImage()).size(32, 32);
            }
        }

        table.row();
        table.add(skillTable)
                .colspan(2)
                .padTop(10f)
                .expandX()
                .center();

        this.addActor(table);
    }

    public void modificarInfo(int vida, int vidaMax) {
        this.vidaMax = vidaMax;
        this.vidaActual = Math.max(0, Math.min(vida, vidaMax));
        this.labelInfo.setText(nombre + ": " + vidaActual + "/" + vidaMax);
    }

    public void actualizarCooldowns(String cooldownsStr) {
        if (skillIcons == null || skillIcons.isEmpty()) {
            return;
        }
        String[] parts = cooldownsStr != null ? cooldownsStr.split(",") : new String[0];
        for (int i = 0; i < skillIcons.size(); i++) {
            boolean enCooldown = false;
            if (i < parts.length) {
                try {
                    float restante = Float.parseFloat(parts[i]);
                    enCooldown = restante > 0.01f;
                } catch (NumberFormatException ignored) {
                    // Ignore invalid values
                }
            }
            skillIcons.get(i).setOnCooldown(enCooldown);
        }
    }
}
