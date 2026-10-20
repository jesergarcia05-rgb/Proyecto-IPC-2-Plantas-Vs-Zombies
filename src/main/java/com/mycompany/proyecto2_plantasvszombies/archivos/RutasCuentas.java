package com.mycompany.proyecto2_plantasvszombies.archivos;

import java.io.File;

/** Los nombres codificados evitan confundir Ana/ana en discos de Windows. */
public final class RutasCuentas {
    private RutasCuentas() { }
    public static File carpeta(String nickname) {
        StringBuilder nombre = new StringBuilder("u-");
        for (int i = 0; i < nickname.length(); i++) {
            String hex = Integer.toHexString(nickname.charAt(i));
            for (int k = hex.length(); k < 4; k++) nombre.append('0');
            nombre.append(hex);
        }
        File codificada = new File("cuentas", nombre.toString());
        if (codificada.exists()) return codificada;
        // Conserva cuentas existentes de la version anterior sin renombrarlas.
        File[] anteriores = new File("cuentas").listFiles();
        if (anteriores != null) for (File anterior : anteriores)
            if (anterior.isDirectory() && anterior.getName().equals(nickname)) return anterior;
        return codificada;
    }
    public static String nickname(File carpeta) {
        String nombre = carpeta.getName();
        if (!nombre.startsWith("u-")) return nombre;
        if ((nombre.length() - 2) % 4 != 0) return "";
        StringBuilder nickname = new StringBuilder();
        try {
            for (int i = 2; i < nombre.length(); i += 4)
                nickname.append((char)Integer.parseInt(nombre.substring(i, i + 4), 16));
        } catch (NumberFormatException e) { return ""; }
        return nickname.toString();
    }
}
