import javax.swing.SwingUtilities;
import java.io.File;
import com.mycompany.proyecto2_plantasvszombies.vista.HilosPartida;
import com.mycompany.proyecto2_plantasvszombies.modelo.*;
import com.mycompany.proyecto2_plantasvszombies.archivos.GestorPartidas;

/** Comprueba trabajadores reales, pausa, carga y ausencia de dobles emisiones. */
public class PruebasHilos {
    private static int comprobaciones;
    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
        comprobaciones++;
    }
    private static void avanzar(MotorCombate m, double segundos) {
        for (int i = 0; i < (int)Math.round(segundos / .05); i++) m.actualizar(.05);
    }
    public static void main(String[] args) throws Exception {
        String[] dificultades = {"Fácil", "Medio", "Difícil"};
        for (int d = 0; d < dificultades.length; d++) {
            final String dificultad = dificultades[d]; final int esperados = 6 - d;
            MotorCombate[] modelo = new MotorCombate[1]; HilosPartida[] hilos = new HilosPartida[1];
            try {
                SwingUtilities.invokeAndWait(() -> {
                    String[][] jardin = {{"VACIO", "VACIO", "VACIO", "VACIO", "VACIO", "VACIO"}};
                    Partida p = new Partida(new Cuenta("Hilos"), new Jardin(jardin), new ConfiguracionNivel(180, 200, jardin), dificultad);
                    modelo[0] = new MotorCombate(p);
                    hilos[0] = new HilosPartida(modelo[0], () -> { }); hilos[0].iniciar();
                    comprobar(hilos[0].getCantidadGeneradoresActivos() == esperados, "Numero de Threads en " + dificultad);
                    avanzar(modelo[0], 15.1);
                    comprobar(modelo[0].getCantidadCerebros() == 0, "El reloj no sustituye a los trabajadores externos");
                });
                Thread.sleep(200);
                SwingUtilities.invokeAndWait(() -> {
                    comprobar(modelo[0].getCantidadCerebros() == esperados, "Cada trabajador produce su icono");
                    modelo[0].getPartida().pausar(); hilos[0].detener();
                    comprobar(hilos[0].getCantidadGeneradoresActivos() == 0, "Pausa detiene trabajadores");
                    avanzar(modelo[0], 15);
                    comprobar(modelo[0].getTiempoTranscurrido() < 15.2, "Pausa congela tiempos");
                });
                Thread.sleep(100);
                SwingUtilities.invokeAndWait(() -> {
                    comprobar(modelo[0].getCantidadCerebros() == esperados, "No se ejecutan notificaciones antiguas");
                    try {
                        File f = new GestorPartidas().guardar(modelo[0], .1);
                        modelo[0] = new GestorPartidas().cargar(f, new Cuenta("Hilos")).getMotor();
                    } catch (Exception e) { throw new RuntimeException(e); }
                    comprobar(modelo[0].getPartida().estaPausada(), "Carga en pausa");
                    modelo[0].getPartida().reanudar();
                    hilos[0] = new HilosPartida(modelo[0], () -> { }); hilos[0].iniciar();
                });
                Thread.sleep(150);
                SwingUtilities.invokeAndWait(() -> {
                    comprobar(modelo[0].getCantidadCerebros() == esperados, "Reanudar no duplica iconos guardados");
                    comprobar(modelo[0].recogerCerebro(0), "Recolectar tras cargar");
                    avanzar(modelo[0], 4);
                });
                Thread.sleep(100);
                SwingUtilities.invokeAndWait(() -> {
                    comprobar(modelo[0].getCantidadCerebros() == esperados - 1, "Generador recogido respeta espera minima");
                    hilos[0].detener(); modelo[0].getPartida().terminar();
                });
            } finally {
                SwingUtilities.invokeAndWait(() -> { if (hilos[0] != null) hilos[0].detener(); });
            }
        }
        Thread.sleep(100);
        Thread[] activos = new Thread[Thread.activeCount() * 2 + 20]; int cantidad = Thread.enumerate(activos);
        for (int i = 0; i < cantidad; i++)
            comprobar(!activos[i].getName().startsWith("Generador-Cerebros-") && !activos[i].getName().equals("Reloj-del-juego"), "Sin trabajadores huerfanos");
        System.out.println("OK: " + comprobaciones + " comprobaciones de concurrencia y reanudación.");
    }
}
