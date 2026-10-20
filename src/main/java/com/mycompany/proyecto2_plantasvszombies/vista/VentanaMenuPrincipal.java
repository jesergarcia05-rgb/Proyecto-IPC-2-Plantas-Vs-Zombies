package com.mycompany.proyecto2_plantasvszombies.vista;

import com.mycompany.proyecto2_plantasvszombies.archivos.*;
import com.mycompany.proyecto2_plantasvszombies.modelo.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class VentanaMenuPrincipal extends JFrame {
    private final GestorCuentas gestor;
    private final Cuenta cuenta;
    public VentanaMenuPrincipal(GestorCuentas gestor, Cuenta cuenta) {
        super("Zombis vs. Plantas - " + cuenta.getNickname()); this.gestor = gestor; this.cuenta = cuenta;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel panel = new PanelFondo("Fondo2.jpg", new GridLayout(0, 1, 12, 12)); panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        panel.add(new JLabel("Cuenta: " + cuenta.getNickname(), SwingConstants.CENTER));
        JButton nueva = new JButton("Nueva partida"), reanudar = new JButton("Reanudar partida guardada"), estadisticas = new JButton("Estadísticas y ranking"), salir = new JButton("Salir");
        panel.add(nueva); panel.add(reanudar); panel.add(estadisticas); panel.add(salir);
        for (JButton boton : new JButton[]{nueva, reanudar, estadisticas, salir}) {
            boton.setBackground(new Color(250, 245, 219)); boton.setForeground(new Color(35, 65, 30));
            boton.setFont(boton.getFont().deriveFont(Font.BOLD, 15f));
        }
        ((JLabel)panel.getComponent(0)).setForeground(Color.WHITE);
        panel.setPreferredSize(new Dimension(640, 430));
        nueva.addActionListener(e -> { new VentanaNuevaPartida(gestor, cuenta).setVisible(true); dispose(); });
        reanudar.addActionListener(e -> reanudar()); estadisticas.addActionListener(e -> estadisticas()); salir.addActionListener(e -> dispose());
        add(panel); pack(); setMinimumSize(new Dimension(440, 320)); setLocationRelativeTo(null);
    }
    private void reanudar() {
        GestorPartidas partidas = new GestorPartidas(); File[] archivos = partidas.listar(cuenta);
        if (archivos.length == 0) { JOptionPane.showMessageDialog(this, "No tienes partidas guardadas."); return; }
        String[] nombres = new String[archivos.length];
        for (int i = 0; i < archivos.length; i++) nombres[i] = archivos[i].getName();
        JList<String> lista = new JList<>(nombres); lista.setSelectedIndex(0); lista.setVisibleRowCount(8);
        if (JOptionPane.showConfirmDialog(this, new JScrollPane(lista), "Selecciona una partida", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        int i = lista.getSelectedIndex(); if (i < 0) return;
        try {
            GestorPartidas.Estado estado = partidas.cargar(archivos[i], cuenta);
            new VentanaPartida(gestor, cuenta, estado.getMotor(), estado.getFraccionSegundo(), archivos[i]).setVisible(true); dispose();
        } catch (java.io.IOException ex) { JOptionPane.showMessageDialog(this, "No se pudo cargar: " + ex.getMessage()); }
    }
    private void estadisticas() {
        Estadisticas e = cuenta.getEstadisticas();
        String resumen = "Cuenta: " + cuenta.getNickname() + "\n\nPartidas jugadas: " + e.getPartidasJugadas()
            + "\nGanadas: " + e.getPartidasGanadas() + "\nPerdidas: " + e.getPartidasPerdidas()
            + "\nPlantas destruidas: " + e.getPlantasDestruidas() + "\nZombis invocados: " + e.getZombisInvocados()
            + "\nCerebros gastados: " + e.getCerebrosGastados() + "\nPodadoras activadas: " + e.getPodadorasActivadas()
            + "\nMejor victoria: " + (e.getPartidasGanadas() == 0 ? "Sin victorias" : e.getMejorTiempoVictoria() + " s")
            + String.format("\nPorcentaje de victorias: %.2f%%\nPlantas por partida: %.2f", e.getPorcentajeVictorias(), e.getPromedioPlantasDestruidas());
        String ranking = ranking(0, "Top 5 invasores más rápidos") + "\n" + ranking(1, "Top 5 mayores destructores") + "\n" + ranking(2, "Top 5 más exitosos");
        JTabbedPane tabs = new JTabbedPane(); tabs.addTab("Mi cuenta", texto(resumen)); tabs.addTab("Ranking global", texto(ranking));
        tabs.setPreferredSize(new Dimension(540, 460)); JOptionPane.showMessageDialog(this, tabs, "Sala de la fama", JOptionPane.PLAIN_MESSAGE);
    }
    private JScrollPane texto(String valor) { JTextArea t = new JTextArea(valor); t.setEditable(false); t.setMargin(new Insets(12, 12, 12, 12)); return new JScrollPane(t); }
    private String ranking(int tipo, String titulo) {
        GestorCuentas actualizado = new GestorCuentas(); Cuenta[] todas = actualizado.getCuentas();
        Cuenta[] ordenadas = new Cuenta[actualizado.getCantidadCuentas()]; int n = 0;
        for (int i = 0; i < actualizado.getCantidadCuentas(); i++) {
            Cuenta c = todas[i].getNickname().equals(cuenta.getNickname()) ? cuenta : todas[i];
            if (tipo != 0 || c.getEstadisticas().getPartidasGanadas() > 0) ordenadas[n++] = c;
        }
        for (int i = 1; i < n; i++) {
            Cuenta c = ordenadas[i]; int j = i - 1;
            while (j >= 0 && comparar(c, ordenadas[j], tipo) < 0) { ordenadas[j + 1] = ordenadas[j]; j--; }
            ordenadas[j + 1] = c;
        }
        StringBuilder s = new StringBuilder(titulo).append('\n');
        if (n == 0) s.append("Sin registros\n");
        for (int i = 0; i < Math.min(5, n); i++) s.append(i + 1).append(". ").append(ordenadas[i].getNickname())
            .append(" — ").append(valor(ordenadas[i], tipo)).append(tipo == 0 ? " s" : "").append('\n');
        return s.toString();
    }
    private int valor(Cuenta c, int tipo) { Estadisticas e = c.getEstadisticas(); return tipo == 0 ? e.getMejorTiempoVictoria() : tipo == 1 ? e.getPlantasDestruidas() : e.getPartidasGanadas(); }
    private int comparar(Cuenta a, Cuenta b, int tipo) {
        int c = tipo == 0 ? Integer.compare(valor(a, tipo), valor(b, tipo)) : Integer.compare(valor(b, tipo), valor(a, tipo));
        return c == 0 ? a.getNickname().compareTo(b.getNickname()) : c;
    }
}
