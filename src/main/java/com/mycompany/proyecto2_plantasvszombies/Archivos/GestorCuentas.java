package com.mycompany.proyecto2_plantasvszombies.archivos;

import com.mycompany.proyecto2_plantasvszombies.modelo.Cuenta;
import com.mycompany.proyecto2_plantasvszombies.modelo.Estadisticas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GestorCuentas {

    private Cuenta[] cuentas;
    private int cantidadCuentas;

    public GestorCuentas() {
        cuentas = new Cuenta[100];
        cantidadCuentas = 0;
        cargarCuentas();
    }

    public boolean nicknameValido(String nickname) {
        if (nickname == null || nickname.length() < 1 || nickname.length() > 12) {
            return false;
        }

        for (int i = 0; i < nickname.length(); i++) {
            if (!Character.isLetterOrDigit(nickname.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public Cuenta buscarCuenta(String nickname) {
        for (int i = 0; i < cantidadCuentas; i++) {
            if (cuentas[i].getNickname().equals(nickname)) {
                return cuentas[i];
            }
        }
        return null;
    }

    public boolean crearCuenta(String nickname) {
        if (!nicknameValido(nickname) || buscarCuenta(nickname) != null) {
            return false;
        }

        if (cantidadCuentas >= cuentas.length) {
            return false;
        }

        if (!crearArchivosCuenta(nickname)) {
            return false;
        }

        cuentas[cantidadCuentas] = new Cuenta(nickname);
        cantidadCuentas++;
        return true;
    }

    private boolean crearArchivosCuenta(String nickname) {
        File carpetaCuenta = new File("cuentas/" + nickname);
        File carpetaPartidas = new File(carpetaCuenta, "partidas");
        File archivoEstadisticas = new File(carpetaCuenta, "estadisticas.txt");

        try {
            if (!carpetaPartidas.exists() && !carpetaPartidas.mkdirs()) {
                return false;
            }

            if (archivoEstadisticas.createNewFile()) {
                escribirEstadisticas(archivoEstadisticas, new Estadisticas());
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error al crear los archivos de la cuenta.");
            return false;
        }
    }

    private void cargarCuentas() {
        File carpetaCuentas = new File("cuentas");

        if (!carpetaCuentas.exists()) {
            carpetaCuentas.mkdirs();
            return;
        }

        File[] carpetas = carpetaCuentas.listFiles();
        if (carpetas == null) return;

        for (int i = 0; i < carpetas.length && cantidadCuentas < cuentas.length; i++) {
            if (carpetas[i].isDirectory()) {
                String nickname = carpetas[i].getName();

                if (nicknameValido(nickname)) {
                    Estadisticas estadisticas = cargarEstadisticas(nickname);
                    cuentas[cantidadCuentas] = new Cuenta(nickname, estadisticas);
                    cantidadCuentas++;
                }
            }
        }
    }

    private Estadisticas cargarEstadisticas(String nickname) {
        File archivo = new File("cuentas/" + nickname + "/estadisticas.txt");

        if (!archivo.exists()) {
            return new Estadisticas();
        }

        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            int partidasJugadas = leerValor(lector.readLine());
            int partidasGanadas = leerValor(lector.readLine());
            int partidasPerdidas = leerValor(lector.readLine());
            int plantasDestruidas = leerValor(lector.readLine());
            int zombisInvocados = leerValor(lector.readLine());
            int cerebrosGastados = leerValor(lector.readLine());
            int mejorTiempoVictoria = leerValor(lector.readLine());
            int podadorasActivadas = leerValor(lector.readLine());

            return new Estadisticas(partidasJugadas, partidasGanadas, partidasPerdidas,
                    plantasDestruidas, zombisInvocados, cerebrosGastados,
                    mejorTiempoVictoria, podadorasActivadas);
        } catch (IOException | NumberFormatException e) {
            System.out.println("No se pudieron cargar las estadísticas de " + nickname + ".");
            return new Estadisticas();
        }
    }

    private int leerValor(String linea) throws NumberFormatException {
        if (linea == null) return 0;
        int posicion = linea.lastIndexOf(':');
        if (posicion == -1) return 0;
        return Integer.parseInt(linea.substring(posicion + 1).trim());
    }

    public void guardarEstadisticas(Cuenta cuenta) {
        if (cuenta == null) return;

        File archivo = new File("cuentas/" + cuenta.getNickname() + "/estadisticas.txt");
        try {
            escribirEstadisticas(archivo, cuenta.getEstadisticas());
        } catch (IOException e) {
            System.out.println("Error al guardar las estadísticas.");
        }
    }

    private void escribirEstadisticas(File archivo, Estadisticas e) throws IOException {
        try (FileWriter escritor = new FileWriter(archivo)) {
            escritor.write("Partidas Jugadas: " + e.getPartidasJugadas() + "\n");
            escritor.write("Partidas Ganadas: " + e.getPartidasGanadas() + "\n");
            escritor.write("Partidas Perdidas: " + e.getPartidasPerdidas() + "\n");
            escritor.write("Plantas Destruidas: " + e.getPlantasDestruidas() + "\n");
            escritor.write("Zombis Invocados: " + e.getZombisInvocados() + "\n");
            escritor.write("Cerebros Gastados: " + e.getCerebrosGastados() + "\n");
            escritor.write("Mejor Tiempo Victoria: " + e.getMejorTiempoVictoria() + "\n");
            escritor.write("Podadoras Activadas: " + e.getPodadorasActivadas() + "\n");
        }
    }

    public Cuenta[] getCuentas() { return cuentas; }
    public int getCantidadCuentas() { return cantidadCuentas; }
}
