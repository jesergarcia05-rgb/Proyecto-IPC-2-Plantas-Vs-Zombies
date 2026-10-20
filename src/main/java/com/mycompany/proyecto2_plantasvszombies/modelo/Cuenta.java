package com.mycompany.proyecto2_plantasvszombies.modelo;

public class Cuenta implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String nickname;
    private Estadisticas estadisticas;

    public Cuenta(String nickname) {
        this.nickname = nickname;
        this.estadisticas = new Estadisticas();
    }

    public Cuenta(String nickname, Estadisticas estadisticas) {
        this.nickname = nickname;
        this.estadisticas = estadisticas;
    }

    public String getNickname() {
        return nickname;
    }

    public Estadisticas getEstadisticas() {
        return estadisticas;
    }
}