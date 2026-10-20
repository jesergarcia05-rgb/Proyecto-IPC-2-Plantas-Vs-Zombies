package com.mycompany.proyecto2_plantasvszombies.vista;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public final class Recursos {
    private Recursos() { }
    public static ImageIcon icono(String nombre) {
        String[] carpetas = {"Imagenes", "src/main/resources/Imagenes", "src/main/Resources/Imagenes"};
        for (String carpeta : carpetas) {
            File archivo = new File(carpeta, nombre);
            if (archivo.isFile()) return new ImageIcon(archivo.getPath());
        }
        java.net.URL url = Recursos.class.getResource("/Imagenes/" + nombre);
        return url == null ? new ImageIcon() : new ImageIcon(url);
    }
    public static BufferedImage imagen(String nombre) {
        ImageIcon icono = icono(nombre);
        if (icono.getIconWidth() <= 0 || icono.getIconHeight() <= 0) return null;
        BufferedImage imagen = new BufferedImage(icono.getIconWidth(), icono.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagen.createGraphics(); g.drawImage(icono.getImage(), 0, 0, null); g.dispose();
        return imagen;
    }
    public static ImageIcon iconoEscalado(String nombre, int ancho, int alto) {
        ImageIcon original = icono(nombre);
        if (original.getIconWidth() <= 0) return null;
        double escala = Math.min((double)ancho / original.getIconWidth(), (double)alto / original.getIconHeight());
        return new ImageIcon(original.getImage().getScaledInstance(Math.max(1, (int)(original.getIconWidth() * escala)),
            Math.max(1, (int)(original.getIconHeight() * escala)), Image.SCALE_SMOOTH));
    }
}
