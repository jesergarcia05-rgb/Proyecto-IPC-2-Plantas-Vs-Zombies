package com.mycompany.proyecto2_plantasvszombies.vista;

import javax.swing.*;
import java.awt.*;

/** Escala el fondo conservando su proporcion y sin alterar el archivo de imagen. */
public class PanelFondo extends JPanel {
    private final Image imagen;
    public PanelFondo(String nombre, LayoutManager layout) {
        super(layout); ImageIcon icono = Recursos.icono(nombre);
        imagen = icono.getIconWidth() > 0 ? icono.getImage() : null;
    }
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagen == null) return;
        double escala = Math.max((double)getWidth() / imagen.getWidth(this), (double)getHeight() / imagen.getHeight(this));
        int w = (int)Math.ceil(imagen.getWidth(this) * escala), h = (int)Math.ceil(imagen.getHeight(this) * escala);
        g.drawImage(imagen, (getWidth() - w) / 2, (getHeight() - h) / 2, w, h, this);
        g.setColor(new Color(0, 0, 0, 65)); g.fillRect(0, 0, getWidth(), getHeight());
    }
}
