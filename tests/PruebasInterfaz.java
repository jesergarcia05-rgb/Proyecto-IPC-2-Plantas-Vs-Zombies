import com.mycompany.proyecto2_plantasvszombies.archivos.*;
import com.mycompany.proyecto2_plantasvszombies.modelo.*;
import com.mycompany.proyecto2_plantasvszombies.vista.*;
import com.mycompany.proyecto2_plantasvszombies.zombis.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class PruebasInterfaz {
    private static VentanaPartida ventana;
    private static MotorCombate motor;
    private static GestorCuentas gestor;
    private static Cuenta cuenta;
    private static JButton boton(Container raiz, String texto) {
        for (Component c : raiz.getComponents()) {
            if (c instanceof JButton && ((JButton)c).getText().contains(texto)) return (JButton)c;
            if (c instanceof Container) { JButton b = boton((Container)c, texto); if (b != null) return b; }
        }
        return null;
    }
    private static JDialog dialogo(String titulo) {
        for (Window w : Window.getWindows()) if (w instanceof JDialog && ((JDialog)w).getTitle().equals(titulo) && w.isShowing()) return (JDialog)w;
        return null;
    }
    private static Point punto(PanelJardinFondo panel, double columna, double fila) {
        try {
            var metodo = PanelJardinFondo.class.getDeclaredMethod("calcularAreaCesped"); metodo.setAccessible(true);
            Object area = metodo.invoke(panel);
            String[] nombres = {"x", "y", "anchoCasilla", "altoCasilla"}; double[] valores = new double[4];
            for (int i = 0; i < 4; i++) { var campo = area.getClass().getDeclaredField(nombres[i]); campo.setAccessible(true); valores[i] = campo.getDouble(area); }
            return new Point((int)(valores[0] + columna * valores[2]), (int)(valores[1] + fila * valores[3]));
        } catch (ReflectiveOperationException e) { throw new AssertionError(e); }
    }
    private static void check(boolean condicion, String mensaje) { if (!condicion) throw new AssertionError(mensaje); }
    public static void main(String[] args) throws Exception {
        try {
            SwingUtilities.invokeAndWait(() -> {
                gestor = new GestorCuentas(); gestor.crearCuenta("Interfaz"); cuenta = gestor.buscarCuenta("Interfaz");
                String[][] jardin = {{"VACIO", "VACIO", "NUEZ", "VACIO"}, {"VACIO", "PATATAPUM", "VACIO", "VACIO"},
                    {"VACIO", "LANZAGUISANTES", "VACIO", "CARRONIVORA"}, {"PINCHOHIERBA", "VACIO", "HIELAGUISANTES", "BIPETIDORA"}};
                Partida p = new Partida(cuenta, new Jardin(jardin), new ConfiguracionNivel(180, 500, jardin), "Medio");
                motor = new MotorCombate(p); p.pausar();
                ventana = new VentanaPartida(gestor, cuenta, motor, .25, null); ventana.setVisible(true);
            });
            Thread.sleep(250);
            SwingUtilities.invokeAndWait(() -> {
                JDialog d = dialogo("Partida pausada"); check(d != null && motor.getPartida().estaPausada(), "Carga en pausa visible");
                boton(d, "Reanudar").doClick(); check(!motor.getPartida().estaPausada(), "Reanudar");
            });
            Thread.sleep(350);
            SwingUtilities.invokeAndWait(() -> {
                check(motor.getTiempoTranscurrido() > 0, "Hilo mueve simulación");
                boton(ventana, "Común").doClick();
                PanelJardinFondo panel = null;
                for (Component c : ventana.getContentPane().getComponents()) if (c instanceof PanelJardinFondo) panel = (PanelJardinFondo)c;
                check(panel != null, "Tablero en ventana");
                Point entrada = punto(panel, .2, .5);
                panel.dispatchEvent(new MouseEvent(panel, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0,
                    entrada.x, entrada.y, 1, false));
                check(motor.getPartida().getZombisComprados() == 1 && motor.getPartida().getCerebros() == 450, "Invocar por clic y descontar");
                boton(ventana, "Pausar / Reanudar").doClick(); check(motor.getPartida().estaPausada(), "Botón pausa");
                // Pintado real de jardín, zombis, escudos, plantas y cerebros.
                motor.getPartida().reanudar();
                motor.getPartida().invocarZombi(new ZombiComun(0, 0));
                motor.getPartida().invocarZombi(new ZombiGarrocha(1, 0));
                for (int i = 0; i < 300; i++) motor.actualizar(.04);
                while (motor.getCantidadCerebros() == 0) motor.actualizar(.04);
                var cerebro = motor.getCerebro(motor.getCantidadCerebros() - 1);
                int saldo = motor.getPartida().getCerebros(), compras = motor.getPartida().getZombisComprados();
                // Clic real sobre el icono superior, con seleccion activa: recoge sin comprar.
                Point icono = punto(panel, cerebro.getX(), cerebro.getY());
                int x = icono.x, y = icono.y;
                panel.dispatchEvent(new MouseEvent(panel, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, x, y, 1, false));
                check(motor.getPartida().getCerebros() == saldo + 25 && motor.getPartida().getZombisComprados() == compras, "Clic cerebro no invoca");
                motor.getPartida().pausar();
                java.awt.image.BufferedImage imagen = new java.awt.image.BufferedImage(ventana.getWidth(), ventana.getHeight(), java.awt.image.BufferedImage.TYPE_INT_RGB);
                ventana.paint(imagen.getGraphics());
                try { javax.imageio.ImageIO.write(imagen, "png", new File("captura-interfaz.png")); }
                catch (java.io.IOException e) { throw new RuntimeException(e); }
                boton(dialogo("Partida pausada"), "Guardar y salir").doClick();
                check(!ventana.isDisplayable() && new GestorPartidas().listar(cuenta).length == 1, "Guardar sale al menú");
                boolean menu = false;
                for (Frame f : Frame.getFrames()) if (f instanceof VentanaMenuPrincipal && f.isShowing()) menu = true;
                check(menu, "Menú después de guardar");
            });
            System.out.println("OK: interfaz, hilo, pausa, pintado, guardado y regreso al menú.");
        } finally {
            SwingUtilities.invokeAndWait(() -> { for (Window w : Window.getWindows()) w.dispose(); });
        }
    }
}
