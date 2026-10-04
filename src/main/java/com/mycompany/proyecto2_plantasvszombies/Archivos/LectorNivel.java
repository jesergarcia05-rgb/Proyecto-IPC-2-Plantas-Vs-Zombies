package com.mycompany.proyecto2_plantasvszombies.archivos;

import com.mycompany.proyecto2_plantasvszombies.modelo.ConfiguracionNivel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
public class LectorNivel {

    public ConfiguracionNivel leerNivel(File archivo) throws IOException {

        String contenido = leerContenido(archivo);

        int duracionSegundos =
                obtenerEntero(contenido, "duracionSegundos");

        int cerebrosIniciales =
                obtenerEntero(contenido, "cerebrosIniciales");

        String[][] jardin =
                obtenerJardin(contenido);

        validarJardin(jardin);

        return new ConfiguracionNivel(
                duracionSegundos,
                cerebrosIniciales,
                jardin
        );
    }

    private String leerContenido(File archivo) throws IOException {

        BufferedReader lector =
                new BufferedReader(
                        new FileReader(archivo)
                );

        StringBuilder contenido =
                new StringBuilder();

        String linea;

        while ((linea = lector.readLine()) != null) {
            contenido.append(linea.trim());
        }

        lector.close();

        return contenido.toString();
    }

    private int obtenerEntero(
            String contenido,
            String clave) {

        String buscar = "\"" + clave + "\"";

        int posicionClave =
                contenido.indexOf(buscar);

        if (posicionClave == -1) {
            throw new IllegalArgumentException(
                    "No se encontró: " + clave
            );
        }

        int posicionDosPuntos =
                contenido.indexOf(
                        ":",
                        posicionClave
                );

        int posicionComa =
                contenido.indexOf(
                        ",",
                        posicionDosPuntos
                );

        String numero =
                contenido.substring(
                        posicionDosPuntos + 1,
                        posicionComa
                ).trim();

        return Integer.parseInt(numero);
    }

    private String[][] obtenerJardin(
            String contenido) {

        int posicionJardin =
                contenido.indexOf("\"jardin\"");

        if (posicionJardin == -1) {
            throw new IllegalArgumentException(
                    "No se encontró el jardín."
            );
        }

        int inicio =
                contenido.indexOf(
                        "[",
                        posicionJardin
                );

        int fin =
                contenido.lastIndexOf("]");

        if (inicio == -1 || fin == -1) {
            throw new IllegalArgumentException(
                    "Formato de jardín inválido."
            );
        }

        String textoJardin =
                contenido.substring(
                        inicio + 1,
                        fin
                );

        String[] filasTexto =
                textoJardin.split("\\],\\[");

        String[][] jardin =
                new String[filasTexto.length][];

        for (int i = 0;
                i < filasTexto.length;
                i++) {

            String fila =
                    filasTexto[i]
                            .replace("[", "")
                            .replace("]", "")
                            .replace("\"", "");

            String[] casillas =
                    fila.split(",");

            jardin[i] =
                    new String[casillas.length];

            for (int j = 0;
                    j < casillas.length;
                    j++) {

                jardin[i][j] =
                        casillas[j].trim();
            }
        }

        return jardin;
    }

    private void validarJardin(
            String[][] jardin) {

        if (jardin == null
                || jardin.length == 0) {

            throw new IllegalArgumentException(
                    "El jardín está vacío."
            );
        }

        int columnas =
                jardin[0].length;

        if (columnas == 0) {

            throw new IllegalArgumentException(
                    "El jardín no tiene columnas."
            );
        }

        for (int i = 0;
                i < jardin.length;
                i++) {

            if (jardin[i].length
                    != columnas) {

                throw new IllegalArgumentException(
                        "Todos los carriles deben tener "
                        + "la misma cantidad de columnas."
                );
            }

            for (int j = 0;
                    j < jardin[i].length;
                    j++) {

                if (!plantaValida(
                        jardin[i][j])) {

                    throw new IllegalArgumentException(
                            "Planta no válida: "
                            + jardin[i][j]
                    );
                }
            }
        }
    }

    private boolean plantaValida(
            String planta) {

        return planta.equals("VACIO")
                || planta.equals("LANZAGUISANTES")
                || planta.equals("PATATAPUM")
                || planta.equals("HIELAGUISANTES")
                || planta.equals("NUEZ")
                || planta.equals("CARRONIVORA")
                || planta.equals("PINCHOHIERBA")
                || planta.equals("BIPETIDORA");
    }
}