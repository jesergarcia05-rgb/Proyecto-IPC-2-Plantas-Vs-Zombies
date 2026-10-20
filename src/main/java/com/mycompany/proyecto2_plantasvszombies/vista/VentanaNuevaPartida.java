package com.mycompany.proyecto2_plantasvszombies.vista;

import com.mycompany.proyecto2_plantasvszombies.archivos.*;
import com.mycompany.proyecto2_plantasvszombies.modelo.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;

/** Usa exclusivamente componentes y layouts del JDK. */
public class VentanaNuevaPartida extends JFrame {
    private final GestorCuentas gestor;
    private final Cuenta cuenta;
    private File archivo;
    private final JLabel seleccionado = new JLabel("Selecciona un archivo JSON");
    private final JComboBox<String> dificultad = new JComboBox<>(new String[]{"Fácil", "Medio", "Difícil"});
    public VentanaNuevaPartida(GestorCuentas gestor, Cuenta cuenta) {
        super("Nueva partida - " + cuenta.getNickname()); this.gestor = gestor; this.cuenta = cuenta;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel panel = new PanelFondo("Fondo4.jpg", new GridLayout(0, 1, 8, 12)); panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        JButton elegir = new JButton("Elegir nivel JSON"), iniciar = new JButton("Iniciar"), cancelar = new JButton("Volver al menú");
        panel.add(new JLabel("Nueva partida", SwingConstants.CENTER)); panel.add(seleccionado); panel.add(elegir);
        panel.add(new JLabel("Dificultad")); panel.add(dificultad); panel.add(iniciar); panel.add(cancelar);
        elegir.addActionListener(e -> seleccionar()); iniciar.addActionListener(e -> iniciar());
        cancelar.addActionListener(e -> volver());
        addWindowListener(new java.awt.event.WindowAdapter() { public void windowClosing(java.awt.event.WindowEvent e) { volver(); } });
        for (Component c : panel.getComponents()) if (c instanceof JLabel) { c.setForeground(Color.WHITE); c.setFont(c.getFont().deriveFont(Font.BOLD, 15f)); }
        panel.setPreferredSize(new Dimension(600, 450));
        add(panel); pack(); setMinimumSize(new Dimension(420, 350)); setLocationRelativeTo(null);
    }
    private void seleccionar() {
        JFileChooser selector = new JFileChooser(new File("Niveles"));
        selector.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Nivel JSON", "json"));
        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            archivo = selector.getSelectedFile(); seleccionado.setText(archivo.getName());
        }
    }
    private void iniciar() {
        if (archivo == null) { JOptionPane.showMessageDialog(this, "Selecciona un nivel."); return; }
        try {
            ConfiguracionNivel c = new LectorNivel().leerNivel(archivo);
            VentanaPartida v = new VentanaPartida(gestor, cuenta, new MotorCombate(
                new Partida(cuenta, new Jardin(c.getJardin()), c, (String)dificultad.getSelectedItem())), 0, null);
            v.setVisible(true); dispose();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Nivel inválido: " + ex.getMessage()); }
    }
    private void volver() { new VentanaMenuPrincipal(gestor, cuenta).setVisible(true); dispose(); }
}
