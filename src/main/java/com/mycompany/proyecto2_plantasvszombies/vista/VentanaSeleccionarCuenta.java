package com.mycompany.proyecto2_plantasvszombies.vista;

import com.mycompany.proyecto2_plantasvszombies.archivos.GestorCuentas;
import com.mycompany.proyecto2_plantasvszombies.modelo.Cuenta;

public class VentanaSeleccionarCuenta extends javax.swing.JFrame {

private GestorCuentas gestorCuentas;

public VentanaSeleccionarCuenta(GestorCuentas gestorCuentas) {
     initComponents();
    this.gestorCuentas = gestorCuentas;

    cargarCuentas();
    cargarFondo();

    setSize(400, 650);
    setResizable(false);
    setLocationRelativeTo(null);

    configurarTeclado();

    java.awt.EventQueue.invokeLater(() -> {
        cmbCuentas.requestFocusInWindow();
    });
}

private void configurarTeclado() {

    javax.swing.InputMap inputMap =
            getRootPane().getInputMap(
                    javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);

    javax.swing.ActionMap actionMap =
            getRootPane().getActionMap();


    // IZQUIERDA
    inputMap.put(
            javax.swing.KeyStroke.getKeyStroke("LEFT"),
            "izquierda");

    actionMap.put(
            "izquierda",
            new javax.swing.AbstractAction() {
                @Override
                public void actionPerformed(
                        java.awt.event.ActionEvent e) {

                    if (jButton2.hasFocus()) {
                        jButton1.requestFocusInWindow();

                    } else if (jButton1.hasFocus()) {
                        jButton2.requestFocusInWindow();
                    }
                }
            });


    // DERECHA
    inputMap.put(
            javax.swing.KeyStroke.getKeyStroke("RIGHT"),
            "derecha");

    actionMap.put(
            "derecha",
            new javax.swing.AbstractAction() {
                @Override
                public void actionPerformed(
                        java.awt.event.ActionEvent e) {

                    if (cmbCuentas.hasFocus()) {
                        jButton1.requestFocusInWindow();

                    } else if (jButton1.hasFocus()) {
                        jButton2.requestFocusInWindow();

                    } else if (jButton2.hasFocus()) {
                        jButton1.requestFocusInWindow();
                    }
                }
            });


    // ARRIBA
    inputMap.put(
            javax.swing.KeyStroke.getKeyStroke("UP"),
            "arriba");

    actionMap.put(
            "arriba",
            new javax.swing.AbstractAction() {
                @Override
                public void actionPerformed(
                        java.awt.event.ActionEvent e) {

                    if (jButton1.hasFocus()
                            || jButton2.hasFocus()) {

                        cmbCuentas.requestFocusInWindow();
                    }
                }
            });


    // ABAJO
    inputMap.put(
            javax.swing.KeyStroke.getKeyStroke("DOWN"),
            "abajo");

    actionMap.put(
            "abajo",
            new javax.swing.AbstractAction() {
                @Override
                public void actionPerformed(
                        java.awt.event.ActionEvent e) {

                    if (jButton1.hasFocus()) {
                        jButton2.requestFocusInWindow();

                    } else if (jButton2.hasFocus()) {
                        jButton1.requestFocusInWindow();
                    }
                }
            });


    // ENTER
    inputMap.put(
            javax.swing.KeyStroke.getKeyStroke("ENTER"),
            "enter");

    actionMap.put(
            "enter",
            new javax.swing.AbstractAction() {
                @Override
                public void actionPerformed(
                        java.awt.event.ActionEvent e) {

                    if (cmbCuentas.hasFocus()) {
                        jButton1.doClick();

                    } else if (jButton1.hasFocus()) {
                        jButton1.doClick();

                    } else if (jButton2.hasFocus()) {
                        jButton2.doClick();
                    }
                }
            });
}

private void cargarFondo() {
    java.net.URL ruta = getClass().getResource(
        "/Imagenes/plants-vs-zombies-background-7zlrq9f4c85dcwxr.jpg"
    );

    if (ruta != null) {
        jLabel1.setIcon(new javax.swing.ImageIcon(ruta));
    }
}
private void cargarCuentas() {

    cmbCuentas.removeAllItems();

    Cuenta[] cuentas = gestorCuentas.getCuentas();
    int cantidad = gestorCuentas.getCantidadCuentas();

    for (int i = 0; i < cantidad; i++) {
        cmbCuentas.addItem(cuentas[i].getNickname());
    }
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        cmbCuentas = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        jButton1.setText("Continuar ");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(120, 280, 90, 30);

        jButton2.setText("Cancelar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(230, 280, 90, 30);

        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Seleccionar Cuenta");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(160, 180, 130, 25);

        cmbCuentas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbCuentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCuentasActionPerformed(evt);
            }
        });
        getContentPane().add(cmbCuentas);
        cmbCuentas.setBounds(130, 230, 180, 30);

        jLabel1.setIcon(Recursos.icono("plants-vs-zombies-background-7zlrq9f4c85dcwxr.jpg")); // NOI18N
        jLabel1.setText("jLabel1");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 0, 380, 620);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbCuentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCuentasActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_cmbCuentasActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
         VentanaInicio ventana =
            new VentanaInicio(gestorCuentas);

    ventana.setLocationRelativeTo(null);
    ventana.setVisible(true);

    this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        String nicknameSeleccionado =
            (String) cmbCuentas.getSelectedItem();

    if (nicknameSeleccionado != null) {

        Cuenta cuentaSeleccionada =
                gestorCuentas.buscarCuenta(nicknameSeleccionado);

        if (cuentaSeleccionada != null) {

            VentanaMenuPrincipal ventana =
                    new VentanaMenuPrincipal(gestorCuentas, cuentaSeleccionada);

            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);

            this.dispose();
        }

    } else {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                "No hay ninguna cuenta seleccionada"
        );
    }
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cmbCuentas;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
