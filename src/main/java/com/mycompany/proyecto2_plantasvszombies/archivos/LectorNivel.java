package com.mycompany.proyecto2_plantasvszombies.archivos;

import com.mycompany.proyecto2_plantasvszombies.modelo.ConfiguracionNivel;
import java.io.*;

/** Analizador del formato exacto del enunciado. Acepta espacios entre tokens. */
public class LectorNivel {
    public ConfiguracionNivel leerNivel(File archivo) throws IOException {
        StringBuilder texto = new StringBuilder();
        try (Reader r = new InputStreamReader(new FileInputStream(archivo), java.nio.charset.StandardCharsets.UTF_8)) {
            char[] buffer = new char[4096]; int n;
            while ((n = r.read(buffer)) != -1) {
                if (texto.length() + n > 4_000_000) throw new IOException("Nivel demasiado grande.");
                texto.append(buffer, 0, n);
            }
        }
        return new Analizador(texto.toString()).leer();
    }
    private static class Analizador {
        private final String texto; private int posicion;
        Analizador(String texto) { this.texto = texto; }
        private void espacios() {
            while (posicion < texto.length() && Character.isWhitespace(texto.charAt(posicion))) posicion++;
        }
        private boolean tomar(char c) {
            espacios();
            if (posicion < texto.length() && texto.charAt(posicion) == c) { posicion++; return true; }
            return false;
        }
        private void exigir(char c) { if (!tomar(c)) throw error("Se esperaba '" + c + "'"); }
        private IllegalArgumentException error(String mensaje) {
            return new IllegalArgumentException(mensaje + " en posición " + posicion + ".");
        }
        private String cadena() {
            exigir('"'); int inicio = posicion;
            while (posicion < texto.length() && texto.charAt(posicion) != '"') {
                char c = texto.charAt(posicion++);
                if (c == '\\' || c < 32) throw error("Cadena no admitida");
            }
            if (posicion >= texto.length()) throw error("Cadena sin cerrar");
            String s = texto.substring(inicio, posicion); posicion++; return s;
        }
        private void clave(String esperada) {
            if (!cadena().equals(esperada)) throw error("Se esperaba la clave " + esperada);
            exigir(':');
        }
        private int entero() {
            espacios(); int inicio = posicion;
            if (posicion < texto.length() && texto.charAt(posicion) == '-') posicion++;
            while (posicion < texto.length() && texto.charAt(posicion) >= '0' && texto.charAt(posicion) <= '9') posicion++;
            try { return Integer.parseInt(texto.substring(inicio, posicion)); }
            catch (NumberFormatException e) { throw error("Entero inválido"); }
        }
        ConfiguracionNivel leer() {
            exigir('{'); clave("duracionSegundos"); int duracion = entero(); exigir(',');
            clave("cerebrosIniciales"); int cerebros = entero(); exigir(','); clave("jardin"); exigir('[');
            if (duracion <= 0 || cerebros < 0) throw error("Duración o cerebros inválidos");
            String[][] filas = new String[8][]; int cantidad = 0, columnas = -1;
            do {
                exigir('['); String[] fila = new String[8]; int n = 0;
                do {
                    String planta = cadena();
                    if (!valida(planta)) throw error("Planta no válida: " + planta);
                    if (n == fila.length) { String[] a = new String[n * 2]; System.arraycopy(fila, 0, a, 0, n); fila = a; }
                    fila[n++] = planta;
                } while (tomar(','));
                exigir(']');
                if (columnas != -1 && n != columnas) throw error("Los carriles deben tener igual cantidad de columnas");
                columnas = n;
                String[] exacta = new String[n]; System.arraycopy(fila, 0, exacta, 0, n);
                if (cantidad == filas.length) { String[][] a = new String[cantidad * 2][]; System.arraycopy(filas, 0, a, 0, cantidad); filas = a; }
                filas[cantidad++] = exacta;
            } while (tomar(','));
            exigir(']'); exigir('}'); espacios();
            if (posicion != texto.length()) throw error("Contenido adicional");
            String[][] jardin = new String[cantidad][]; System.arraycopy(filas, 0, jardin, 0, cantidad);
            return new ConfiguracionNivel(duracion, cerebros, jardin);
        }
        private boolean valida(String s) {
            return s.equals("VACIO") || s.equals("LANZAGUISANTES") || s.equals("PATATAPUM")
                || s.equals("HIELAGUISANTES") || s.equals("NUEZ") || s.equals("CARRONIVORA")
                || s.equals("PINCHOHIERBA") || s.equals("BIPETIDORA");
        }
    }
}
