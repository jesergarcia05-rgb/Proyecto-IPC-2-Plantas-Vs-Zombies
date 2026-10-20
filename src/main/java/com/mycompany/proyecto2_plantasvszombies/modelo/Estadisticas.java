package com.mycompany.proyecto2_plantasvszombies.modelo;

public class Estadisticas implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private int partidasJugadas;
    private int partidasGanadas;
    private int partidasPerdidas;
    private int plantasDestruidas;
    private int zombisInvocados;
    private int cerebrosGastados;
    private int mejorTiempoVictoria;
    private int podadorasActivadas;

    public Estadisticas() {
        this(0, 0, 0, 0, 0, 0, 0, 0);
    }

    public Estadisticas(int partidasJugadas, int partidasGanadas, int partidasPerdidas,
                        int plantasDestruidas, int zombisInvocados, int cerebrosGastados,
                        int mejorTiempoVictoria, int podadorasActivadas) {
        this.partidasJugadas = partidasJugadas;
        this.partidasGanadas = partidasGanadas;
        this.partidasPerdidas = partidasPerdidas;
        this.plantasDestruidas = plantasDestruidas;
        this.zombisInvocados = zombisInvocados;
        this.cerebrosGastados = cerebrosGastados;
        this.mejorTiempoVictoria = mejorTiempoVictoria;
        this.podadorasActivadas = podadorasActivadas;
    }

    public void registrarVictoria(int tiempo) {
        partidasJugadas++;
        partidasGanadas++;
        if (partidasGanadas == 1 || tiempo < mejorTiempoVictoria) {
            mejorTiempoVictoria = tiempo;
        }
    }

    public void registrarDerrota() {
        partidasJugadas++;
        partidasPerdidas++;
    }

    public void sumarPlantasDestruidas(int cantidad) { plantasDestruidas += cantidad; }
    public void sumarZombisInvocados(int cantidad) { zombisInvocados += cantidad; }
    public void sumarCerebrosGastados(int cantidad) { cerebrosGastados += cantidad; }
    public void sumarPodadorasActivadas(int cantidad) { podadorasActivadas += cantidad; }

    public double getPorcentajeVictorias() {
        if (partidasJugadas == 0) return 0;
        return (partidasGanadas * 100.0) / partidasJugadas;
    }

    public double getPromedioPlantasDestruidas() {
        if (partidasJugadas == 0) return 0;
        return (double) plantasDestruidas / partidasJugadas;
    }

    public int getPartidasJugadas() { return partidasJugadas; }
    public int getPartidasGanadas() { return partidasGanadas; }
    public int getPartidasPerdidas() { return partidasPerdidas; }
    public int getPlantasDestruidas() { return plantasDestruidas; }
    public int getZombisInvocados() { return zombisInvocados; }
    public int getCerebrosGastados() { return cerebrosGastados; }
    public int getMejorTiempoVictoria() { return mejorTiempoVictoria; }
    public int getPodadorasActivadas() { return podadorasActivadas; }
}
