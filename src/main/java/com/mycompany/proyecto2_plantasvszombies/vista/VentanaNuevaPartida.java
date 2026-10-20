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
        JPanel panel = new PanelFondo("Fondo4.jpg", new GridBagLayout());
        JButton elegir = new JButton("Elegir nivel JSON"), iniciar = new JButton("Iniciar"), cancelar = new JButton("Volver al menú");
        for (JButton boton : new JButton[]{elegir, iniciar, cancelar}) {
            boton.setPreferredSize(new Dimension(170, 40));
            boton.setFont(boton.getFont().deriveFont(Font.PLAIN, 12f));
        }
        dificultad.setPreferredSize(new Dimension(120, 28));
        seleccionado.setHorizontalAlignment(SwingConstants.CENTER);
        seleccionado.setPreferredSize(new Dimension(360, 24));
        Component[] controles = {new JLabel("Nueva partida", SwingConstants.CENTER), seleccionado,
            elegir, new JLabel("Dificultad"), dificultad, iniciar, cancelar};
        GridBagConstraints posicion = new GridBagConstraints();
        posicion.gridx = 0;
        for (int i = 0; i < controles.length; i++) {
            posicion.gridy = i;
            posicion.insets = new Insets(0, 0, i == controles.length - 1 ? 0 : (i == 0 ? 25 : 12), 0);
            panel.add(controles[i], posicion);
        }
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
            seleccionado.setToolTipText(archivo.getAbsolutePath());
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
