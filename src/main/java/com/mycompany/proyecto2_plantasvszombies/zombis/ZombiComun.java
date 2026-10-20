
package com.mycompany.proyecto2_plantasvszombies.zombis;

public class ZombiComun extends Zombi {

    public ZombiComun(int fila, double posicionX) {

        super(
            100,  // Salud
            15,   // Daño
            50,   // Costo
            1.0,  // Velocidad
            fila,
            posicionX
        );
    }
}
