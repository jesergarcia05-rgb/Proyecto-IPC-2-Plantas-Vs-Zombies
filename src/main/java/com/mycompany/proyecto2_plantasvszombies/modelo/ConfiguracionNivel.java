package com.mycompany.proyecto2_plantasvszombies.modelo;

public class ConfiguracionNivel implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private int duracionSegundos;
    private int cerebrosIniciales;
    private String[][] jardin;

    public ConfiguracionNivel(
            int duracionSegundos,
            int cerebrosIniciales,
            String[][] jardin) {

        this.duracionSegundos = duracionSegundos;
        this.cerebrosIniciales = cerebrosIniciales;
        this.jardin = jardin;
    }

    public int getDuracionSegundos() {
        return duracionSegundos;
    }

    public int getCerebrosIniciales() {
        return cerebrosIniciales;
    }

    public String[][] getJardin() {
        return jardin;
    }

    public int getFilas() {
        return jardin.length;
    }

    public int getColumnas() {
        return jardin[0].length;
    }
}