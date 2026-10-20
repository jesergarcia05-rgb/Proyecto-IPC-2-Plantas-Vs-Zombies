package com.mycompany.proyecto2_plantasvszombies.archivos;

import java.io.*;
import com.mycompany.proyecto2_plantasvszombies.modelo.*;

/** Guarda el grafo de datos completo; no serializa Swing ni hilos activos. */
public class GestorPartidas {
    public static class Estado implements Serializable {
        private static final long serialVersionUID = 1L;
        private final int version = 1;
        private final MotorCombate motor;
        private final double fraccionSegundo;
        public Estado(MotorCombate motor, double fraccionSegundo) {
            this.motor = motor; this.fraccionSegundo = fraccionSegundo;
        }
        public MotorCombate getMotor() { return motor; }
        public double getFraccionSegundo() { return fraccionSegundo; }
    }
    public File guardar(MotorCombate motor, double fraccionSegundo) throws IOException {
        Partida p = motor.getPartida();
        if (!p.estaPausada() || p.estaTerminada()) throw new IOException("Solo se guardan partidas pausadas y activas.");
        File carpeta = carpeta(p.getCuenta());
        if (!carpeta.isDirectory() && !carpeta.mkdirs()) throw new IOException("No se pudo crear la carpeta de partidas.");
        String fecha = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new java.util.Date());
        File destino = new File(carpeta, fecha + ".bin");
        int sufijo = 1;
        while (destino.exists()) destino = new File(carpeta, fecha + "-" + sufijo++ + ".bin");
        File temporal = File.createTempFile("guardando-", ".tmp", carpeta);
        try {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(temporal))) {
                out.writeObject(new Estado(motor, fraccionSegundo));
            }
            if (!temporal.renameTo(destino)) throw new IOException("No se pudo completar el guardado.");
            return destino;
        } finally { if (temporal.exists()) temporal.delete(); }
    }
    public Estado cargar(File archivo, Cuenta cuenta) throws IOException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            in.setObjectInputFilter(info -> {
                if (info.depth() > 64 || info.references() > 1_000_000 || info.arrayLength() > 1_000_000
                        || info.streamBytes() > 64_000_000) return ObjectInputFilter.Status.REJECTED;
                Class<?> tipo = info.serialClass();
                if (tipo == null) return ObjectInputFilter.Status.UNDECIDED;
                while (tipo.isArray()) tipo = tipo.getComponentType();
                String nombre = tipo.getName();
                return tipo.isPrimitive() || nombre.equals("java.lang.String") || nombre.equals("java.util.Random")
                    || nombre.startsWith("com.mycompany.proyecto2_plantasvszombies.")
                    ? ObjectInputFilter.Status.ALLOWED : ObjectInputFilter.Status.REJECTED;
            });
            Object objeto = in.readObject();
            if (!(objeto instanceof Estado)) throw new IOException("Archivo de partida no válido.");
            Estado estado = (Estado) objeto;
            if (estado.version != 1 || estado.motor == null || estado.motor.getPartida().estaTerminada())
                throw new IOException("Versión o estado no válido.");
            estado.motor.getPartida().vincularCuenta(cuenta);
            estado.motor.getPartida().pausar();
            return estado;
        } catch (ClassNotFoundException | IllegalArgumentException e) { throw new IOException("No se pudo recuperar la partida.", e); }
    }
    public File[] listar(Cuenta cuenta) {
        File[] archivos = carpeta(cuenta).listFiles((dir, nombre) -> nombre.endsWith(".bin"));
        if (archivos == null) return new File[0];
        // Insercion sobre arreglo nativo: mas reciente primero.
        for (int i = 1; i < archivos.length; i++) {
            File actual = archivos[i]; int j = i - 1;
            while (j >= 0 && archivos[j].getName().compareTo(actual.getName()) < 0) { archivos[j + 1] = archivos[j]; j--; }
            archivos[j + 1] = actual;
        }
        return archivos;
    }
    private File carpeta(Cuenta cuenta) { return new File(RutasCuentas.carpeta(cuenta.getNickname()), "partidas"); }
}
