package com.mycompany.proyecto2_plantasvszombies.vista;

import com.mycompany.proyecto2_plantasvszombies.archivos.*;
import com.mycompany.proyecto2_plantasvszombies.modelo.*;
import com.mycompany.proyecto2_plantasvszombies.zombis.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;

/** Un hilo publica fotogramas; todo el estado mutable se modifica en el EDT. */
public class VentanaPartida extends JFrame {
    private final GestorCuentas gestor;
    private final Cuenta cuenta;
    private final Partida partida;
    private final MotorCombate motor;
    private final PanelJardinFondo tablero = new PanelJardinFondo();
    private final JLabel cerebros = new JLabel(), reloj = new JLabel(), horda = new JLabel();
    private final JButton[] catalogo = new JButton[6];
    private final JButton pausa = new JButton("Pausar / Reanudar");
    private final String[] nombres = {"Común", "Caracono", "Garrocha", "Deportista", "Bailón", "Zombistein"};
    private final int[] costos = {50, 100, 100, 150, 150, 350};
    private int seleccionado = -1, ultimoBloque;
    private Thread hilo;
    private long instante;
    private double fraccion;
    private File archivoOrigen;
    private final JDialog dialogoPausa;
    private boolean resultadoRegistrado;

    public VentanaPartida(Cuenta cuenta, ConfiguracionNivel c, Jardin jardin, String dificultad) {
        this(new GestorCuentas(), cuenta, new MotorCombate(new Partida(cuenta, jardin, c, dificultad)), 0, null);
    }
    public VentanaPartida(GestorCuentas gestor, Cuenta cuenta, MotorCombate motor, double fraccion, File origen) {
        super("Zombis vs. Plantas - " + cuenta.getNickname());
        this.gestor = gestor; this.cuenta = cuenta; this.motor = motor; this.partida = motor.getPartida();
        this.fraccion = fraccion; this.archivoOrigen = origen; this.ultimoBloque = partida.getBloque();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        tablero.setJardin(partida.getJardin()); tablero.setPartida(partida); tablero.setMotorCombate(motor);
        tablero.setSeleccionCarril(fila -> invocar(fila));
        tablero.setPreferredSize(new Dimension(970, Math.max(330, Math.min(620, partida.getJardin().getFilas() * 90))));
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 8));
        arriba.add(cerebros); arriba.add(reloj); arriba.add(new JLabel(partida.getDificultad())); arriba.add(horda); arriba.add(pausa);
        JPanel abajo = new JPanel(new GridLayout(1, 6, 5, 5));
        for (int i = 0; i < catalogo.length; i++) {
            final int tipo = i; catalogo[i] = new JButton();
            String[] archivos = {"zombi.png", "caracono.png", "garrocha.png", "deportista.png", "bailon.png", "zombistein.png"};
            catalogo[i].setIcon(Recursos.iconoEscalado(archivos[i], 64, 70));
            catalogo[i].setHorizontalTextPosition(SwingConstants.CENTER);
            catalogo[i].setVerticalTextPosition(SwingConstants.BOTTOM);
            catalogo[i].addActionListener(e -> {
                if (partida.estaPausada() || partida.estaTerminada()) return;
                seleccionado = seleccionado == tipo ? -1 : tipo; actualizarPanel();
            }); abajo.add(catalogo[i]);
        }
        add(arriba, BorderLayout.NORTH); add(tablero, BorderLayout.CENTER); add(abajo, BorderLayout.SOUTH);
        pausa.addActionListener(e -> abrirPausa());
        dialogoPausa = new JDialog(this, "Partida pausada", false);
        dialogoPausa.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        JPanel opciones = new JPanel(new GridLayout(0, 1, 10, 10)); opciones.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        opciones.add(new JLabel("El reloj, combate, cerebros y defensa están detenidos."));
        JButton reanudar = new JButton("Reanudar"), guardar = new JButton("Guardar y salir");
        opciones.add(reanudar); opciones.add(guardar); dialogoPausa.add(opciones); dialogoPausa.pack();
        reanudar.addActionListener(e -> { partida.reanudar(); instante = System.nanoTime(); dialogoPausa.setVisible(false); actualizarPanel(); });
        guardar.addActionListener(e -> guardarYSalir());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent e) {
                iniciarHilo(); if (partida.estaPausada()) abrirPausa();
            }
            public void windowClosing(java.awt.event.WindowEvent e) { abrirPausa(); }
            public void windowClosed(java.awt.event.WindowEvent e) { detenerHilo(); dialogoPausa.dispose(); }
        });
        actualizarPanel(); pack(); setLocationRelativeTo(null);
    }
    private void iniciarHilo() {
        if (hilo != null) return;
        instante = System.nanoTime();
        hilo = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try { Thread.sleep(40); }
                catch (InterruptedException ex) { Thread.currentThread().interrupt(); return; }
                SwingUtilities.invokeLater(() -> fotograma());
            }
        }, "Reloj-del-juego"); hilo.setDaemon(true); hilo.start();
    }
    private void detenerHilo() { if (hilo != null) { hilo.interrupt(); hilo = null; } }
    private void fotograma() {
        if (!isDisplayable() || partida.estaTerminada()) return;
        long ahora = System.nanoTime(); double dt = Math.max(0, (ahora - instante) / 1_000_000_000.0); instante = ahora;
        if (partida.estaPausada()) return;
        // Limitar TODOS los relojes por igual evita que la simulacion y el contador diverjan.
        dt = Math.min(dt, 0.15);
        fraccion += dt;
        while (fraccion >= 1 && partida.getTiempoRestante() > 0) { fraccion--; partida.descontarUnSegundo(); }
        partida.setFraccionReloj(fraccion);
        if (partida.getTiempoRestante() == 0) { finalizar(false); return; }
        motor.actualizar(dt); actualizarPanel(); tablero.repaint();
        if (motor.hayVictoria()) finalizar(true);
    }
    private void actualizarPanel() {
        cerebros.setText("Cerebros: " + partida.getCerebros());
        int t = partida.getTiempoRestante(); reloj.setText(String.format("Tiempo: %02d:%02d", t / 60, t % 60));
        int bloque = partida.getBloque();
        horda.setText(bloque == 4 ? "¡HORDA! -50%" : bloque == 7 ? "¡HORDA FINAL! -75%" : "Bloque " + bloque + "/7");
        if (bloque != ultimoBloque && (bloque == 4 || bloque == 7)) Toolkit.getDefaultToolkit().beep();
        ultimoBloque = bloque;
        boolean activa = !partida.estaPausada() && !partida.estaTerminada();
        for (int i = 0; i < catalogo.length; i++) {
            catalogo[i].setText("<html><center>" + (i == seleccionado ? "✓ " : "") + nombres[i] + "<br>" + partida.costoActual(costos[i]) + " cerebros</center></html>");
            catalogo[i].setEnabled(activa);
        }
        tablero.setCursor(Cursor.getPredefinedCursor(seleccionado >= 0 && activa ? Cursor.CROSSHAIR_CURSOR : Cursor.DEFAULT_CURSOR));
    }
    private Zombi crear(int tipo, int fila) {
        switch (tipo) {
            case 0: return new ZombiComun(fila, 0); case 1: return new ZombiCaracono(fila, 0);
            case 2: return new ZombiGarrocha(fila, 0); case 3: return new ZombiDeportista(fila, 0);
            case 4: return new ZombiBailon(fila, 0); case 5: return new Zombistein(fila, 0);
            default: return null;
        }
    }
    private void invocar(int fila) {
        if (partida.estaPausada() || partida.estaTerminada()) return;
        if (seleccionado < 0) { JOptionPane.showMessageDialog(this, "Selecciona primero un zombi."); return; }
        Zombi z = crear(seleccionado, fila);
        if (!partida.invocarZombi(z)) {
            // El dialogo modal no debe dejar avanzar el juego por debajo.
            partida.pausar(); actualizarPanel();
            JOptionPane.showMessageDialog(this, "Cerebros insuficientes: necesitas " + partida.costoActual(z.getCosto()) + ".");
            partida.reanudar(); instante = System.nanoTime();
        } else seleccionado = -1;
        actualizarPanel(); tablero.repaint();
    }
    private void abrirPausa() {
        if (partida.estaTerminada()) return;
        partida.pausar(); actualizarPanel(); dialogoPausa.setLocationRelativeTo(this); dialogoPausa.setVisible(true);
    }
    private void guardarYSalir() {
        try {
            File nuevo = new GestorPartidas().guardar(motor, fraccion);
            // El archivo viejo solo se retira despues de confirmar el nuevo.
            if (archivoOrigen != null && !archivoOrigen.equals(nuevo) && !archivoOrigen.delete())
                JOptionPane.showMessageDialog(this, "Guardado nuevo correcto. No se pudo eliminar la copia anterior.");
            detenerHilo(); dialogoPausa.dispose(); dispose(); new VentanaMenuPrincipal(gestor, cuenta).setVisible(true);
        } catch (java.io.IOException ex) { JOptionPane.showMessageDialog(dialogoPausa, "No se pudo guardar: " + ex.getMessage()); }
    }
    private void finalizar(boolean victoria) {
        if (resultadoRegistrado) return;
        resultadoRegistrado = true; partida.terminar(); detenerHilo(); actualizarPanel();
        Estadisticas e = cuenta.getEstadisticas(); int tiempo = partida.getDuracionTotal() - partida.getTiempoRestante();
        if (victoria) e.registrarVictoria(tiempo); else e.registrarDerrota();
        e.sumarPlantasDestruidas(motor.getPlantasDestruidas()); e.sumarZombisInvocados(partida.getZombisComprados());
        e.sumarCerebrosGastados(partida.getCerebrosGastados()); e.sumarPodadorasActivadas(partida.getPodadorasActivadas());
        if (!gestor.guardarEstadisticasComprobadas(cuenta))
            JOptionPane.showMessageDialog(this, "No se pudieron guardar las estadísticas en disco.");
        if (archivoOrigen != null && archivoOrigen.exists() && !archivoOrigen.delete())
            JOptionPane.showMessageDialog(this, "No se pudo retirar el guardado completado.");
        JOptionPane.showMessageDialog(this, (victoria ? "¡Victoria!" : "Derrota por tiempo agotado")
            + "\nTiempo: " + tiempo + " s\nPlantas destruidas: " + motor.getPlantasDestruidas()
            + "\nZombis invocados: " + partida.getZombisComprados() + "\nCerebros gastados: " + partida.getCerebrosGastados()
            + "\nPodadoras activadas: " + partida.getPodadorasActivadas());
        dispose(); new VentanaMenuPrincipal(gestor, cuenta).setVisible(true);
    }
}
