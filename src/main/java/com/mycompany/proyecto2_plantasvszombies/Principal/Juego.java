package com.mycompany.proyecto2_plantasvszombies.principal;

import com.mycompany.proyecto2_plantasvszombies.archivos.GestorCuentas;
import com.mycompany.proyecto2_plantasvszombies.vista.VentanaInicio;

public class Juego {

    private GestorCuentas gestorCuentas;

    public Juego() {
        gestorCuentas = new GestorCuentas();
    }

    public void iniciar() {

        VentanaInicio ventana =
                new VentanaInicio(gestorCuentas);

        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}