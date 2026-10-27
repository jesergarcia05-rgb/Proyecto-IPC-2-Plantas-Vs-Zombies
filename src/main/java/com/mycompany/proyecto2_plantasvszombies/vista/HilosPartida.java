package com.mycompany.proyecto2_plantasvszombies.vista;

import javax.swing.SwingUtilities;
import com.mycompany.proyecto2_plantasvszombies.modelo.MotorCombate;

/**
 * Un reloj y un Thread por generador. Los trabajadores solo esperan/publican;
 * todas las lecturas y escrituras del modelo se realizan en el EDT de Swing.
 * Pausar interrumpe los trabajadores; reanudar crea otros con el estado guardado.
 */
public final class HilosPartida {
    private final MotorCombate motor;
    private final Runnable fotograma;
    private Thread reloj;
    private Thread[] generadores;
    private boolean activo;
    private int sesion;

    public HilosPartida(MotorCombate motor, Runnable fotograma) {
        this.motor = motor; this.fotograma = fotograma;
        generadores = new Thread[motor.getCantidadGeneradores()];
    }
    private void exigirEDT() {
        if (!SwingUtilities.isEventDispatchThread()) throw new IllegalStateException("Controlar hilos desde el EDT.");
    }
    public void iniciar() {
        exigirEDT();
        if (activo) return;
        activo = true;
        int actual = ++sesion;
        motor.usarGeneradoresExternos(true);
        reloj = trabajador("Reloj-del-juego", actual, fotograma);
        for (int i = 0; i < generadores.length; i++) {
            final int indice = i;
            generadores[i] = trabajador("Generador-Cerebros-" + (i + 1), actual,
                    () -> motor.actualizarGenerador(indice));
        }
    }
    private Thread trabajador(String nombre, int actual, Runnable tarea) {
        // La validacion se ejecuta en el EDT, no en el hilo trabajador.
        Runnable entrega = () -> { if (activo && sesion == actual) tarea.run(); };
        Thread hilo = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try { Thread.sleep(40); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
                SwingUtilities.invokeLater(entrega);
            }
        }, nombre);
        hilo.setDaemon(true); hilo.start(); return hilo;
    }
    public void detener() {
        exigirEDT();
        activo = false; sesion++;
        if (reloj != null) { reloj.interrupt(); reloj = null; }
        for (int i = 0; i < generadores.length; i++) {
            if (generadores[i] != null) { generadores[i].interrupt(); generadores[i] = null; }
        }
        motor.usarGeneradoresExternos(false);
    }
    public int getCantidadGeneradoresActivos() {
        exigirEDT();
        int n = 0;
        for (Thread hilo : generadores) if (hilo != null && hilo.isAlive()) n++;
        return n;
    }
}
