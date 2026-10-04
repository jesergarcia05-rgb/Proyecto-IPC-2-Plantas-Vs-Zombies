package com.mycompany.proyecto2_plantasvszombies.modelo;

public class Cuenta {

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