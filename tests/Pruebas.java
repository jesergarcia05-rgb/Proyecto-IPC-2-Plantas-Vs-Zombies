import com.mycompany.proyecto2_plantasvszombies.archivos.*;
import com.mycompany.proyecto2_plantasvszombies.modelo.*;
import com.mycompany.proyecto2_plantasvszombies.plantas.*;
import com.mycompany.proyecto2_plantasvszombies.zombis.*;
import java.io.*;

/** Ejecutar con -ea; solo usa JDK, arreglos y archivos de un directorio temporal. */
public class Pruebas {
    private static int comprobaciones;
    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
        comprobaciones++;
    }
    private static Partida partida(String dificultad, int duracion, String[][] jardin) {
        return new Partida(new Cuenta("Prueba"), new Jardin(jardin), new ConfiguracionNivel(duracion, 5000, jardin), dificultad);
    }
    private static void avanzar(MotorCombate motor, double segundos) {
        int n = (int)Math.round(segundos / .04);
        for (int i = 0; i < n; i++) motor.actualizar(.04);
    }
    private static String[][] vacio(int filas, int columnas) {
        String[][] a = new String[filas][columnas];
        for (int f = 0; f < filas; f++) for (int c = 0; c < columnas; c++) a[f][c] = "VACIO";
        return a;
    }
    private static int plantas(Jardin jardin) {
        int n = 0;
        for (int f = 0; f < jardin.getFilas(); f++) for (int c = 0; c < jardin.getColumnas(); c++)
            if (!jardin.getCasilla(f, c).estaVacia()) n++;
        return n;
    }
    private static void nivel(String json, boolean valido, int filas, int columnas) throws Exception {
        File archivo = new File("nivel-prueba.json");
        try (FileWriter w = new FileWriter(archivo)) { w.write(json); }
        try {
            var c = new LectorNivel().leerNivel(archivo);
            comprobar(valido, "Debía rechazar " + json);
            comprobar(c.getFilas() == filas && c.getColumnas() == columnas, "Dimensiones");
        } catch (IllegalArgumentException ex) { comprobar(!valido, "JSON válido rechazado: " + json); }
    }
    public static void main(String[] args) throws Exception {
        String prefijo = "{\"duracionSegundos\":180,\"cerebrosIniciales\":150,\"jardin\":";
        nivel(prefijo + "[[\"VACIO\"], [\"NUEZ\"]]}", true, 2, 1);
        nivel(prefijo + "[[\"VACIO\"],\n [\"NUEZ\",\"VACIO\"]]}", false, 0, 0);
        nivel(prefijo + "[]}", false, 0, 0); nivel(prefijo + "[[]]}", false, 0, 0);
        nivel(prefijo + "[[\"OTRA\"]]}", false, 0, 0);
        nivel(prefijo + "[[\"VACIO\"]]} basura", false, 0, 0);
        nivel(prefijo.replace(":180", ":0") + "[[\"VACIO\"]]}", false, 0, 0);
        ZombiCaracono cono = new ZombiCaracono(0, 0); cono.recibirDanio(125);
        comprobar(cono.getEscudoCono() == 0 && cono.getSalud() == 75 && cono.getDanio() == 15, "Escudo/daño caracono");
        ZombiDeportista casco = new ZombiDeportista(0, 0); casco.recibirDanio(170);
        comprobar(casco.getEscudoCasco() == 0 && casco.getSalud() == 80, "Escudo deportista");
        Partida p = partida("Fácil", 700, vacio(1, 3));
        comprobar(p.costoActual(50) == 50, "Precio normal");
        for (int i = 0; i < 300; i++) p.descontarUnSegundo();
        comprobar(p.getBloque() == 4 && p.costoActual(50) == 25, "Primera horda");
        p.invocarZombi(new ZombiComun(0, 0));
        comprobar(p.getCerebrosGastados() == 25 && p.getZombisComprados() == 1, "Contar compras al precio vigente");
        p.agregarZombiGenerado(new Zombidito(0, 0)); comprobar(p.getZombisComprados() == 1, "No contar generados como comprados");
        for (int i = 0; i < 100; i++) p.descontarUnSegundo(); comprobar(p.costoActual(50) == 50, "Fin de primera horda");
        for (int i = 0; i < 200; i++) p.descontarUnSegundo();
        comprobar(p.getBloque() == 7 && p.costoActual(50) == 13 && p.costoActual(350) == 88, "Horda final con redondeo documentado");
        p = partida("Fácil", 180, vacio(1, 3)); MotorCombate m = new MotorCombate(p);
        Zombi z = new ZombiComun(0, 0); p.invocarZombi(z); p.pausar(); avanzar(m, 2);
        comprobar(z.getPosicionX() == 0 && m.getTiempoTranscurrido() == 0, "Pausa combate");
        int dinero = p.getCerebros(); p.agregarCerebros(25); comprobar(p.getCerebros() == dinero, "Pausa economía");
        p.reanudar(); avanzar(m, 6); comprobar(!z.estaVivo() && p.getPodadorasActivadas() == 1, "Podadora");
        p.invocarZombi(new ZombiComun(0, 0)); avanzar(m, 6); comprobar(m.hayVictoria() && p.estaTerminada(), "Victoria segunda llegada");
        Partida medio = partida("Medio", 180, vacio(2, 12)); MotorCombate mm = new MotorCombate(medio);
        avanzar(mm, 9.9); comprobar(plantas(medio.getJardin()) == 0, "Medio espera 10 s");
        avanzar(mm, .2); comprobar(plantas(medio.getJardin()) == 1 && !medio.getJardin().getCasilla(0, 11).estaVacia(), "Medio planta al fondo");
        Partida facil = partida("Fácil", 180, vacio(2, 12)); MotorCombate mf = new MotorCombate(facil);
        avanzar(mf, 20); comprobar(plantas(facil.getJardin()) == 0 && mf.getCantidadGeneradores() == 6 && mm.getCantidadGeneradores() == 5, "Fácil no siembra/generadores");
        Partida dificil = partida("Difícil", 180, vacio(2, 12)); MotorCombate md = new MotorCombate(dificil);
        dificil.agregarZombiGenerado(new ZombiDeportista(0, 0)); avanzar(md, 5.1);
        comprobar(plantas(dificil.getJardin()) == 1 && dificil.getJardin().getCasilla(0, 5).getPlanta() instanceof Hielaguisantes, "Difícil detecta deportista y una acción");
        comprobar(md.getCantidadGeneradores() == 4, "Difícil cuatro procesos");
        Partida economia = partida("Fácil", 180, vacio(1, 12)); MotorCombate me = new MotorCombate(economia);
        while (me.getCantidadCerebros() == 0) avanzar(me, .04);
        int antes = economia.getCerebros(); comprobar(me.recogerCerebro(0) && economia.getCerebros() == antes + 25, "Recoger cerebro");
        economia.pausar(); double tiempo = me.getTiempoTranscurrido(); avanzar(me, 10);
        comprobar(me.getTiempoTranscurrido() == tiempo, "Pausa expiración/generadores");
        economia.reanudar(); avanzar(me, 25); comprobar(economia.getCerebros() == antes + 25, "Expirar no da cerebros");
        // Cada regla dificil debe poder actuar sin quedar bloqueada por las anteriores.
        Partida peligro = partida("Difícil", 180, vacio(1, 12)); MotorCombate mp = new MotorCombate(peligro);
        peligro.agregarZombiGenerado(new ZombiComun(0, 7)); avanzar(mp, 5.1);
        Planta defensa = peligro.getJardin().getCasilla(0, 10).getPlanta();
        comprobar(defensa instanceof Nuez || defensa instanceof Carronivora, "Difícil prioridad de casa");
        String[][] superado = vacio(1, 12); superado[0][0] = "NUEZ";
        Partida atras = partida("Difícil", 180, superado); MotorCombate ma = new MotorCombate(atras);
        atras.agregarZombiGenerado(new ZombiComun(0, 1)); avanzar(ma, 5.1);
        comprobar(atras.getJardin().getCasilla(0, 2).getPlanta() instanceof Bipetidora, "Difícil cubre retaguardia");
        Partida resistente = partida("Difícil", 180, vacio(1, 12)); MotorCombate mr = new MotorCombate(resistente);
        resistente.agregarZombiGenerado(new Zombistein(0, 0)); avanzar(mr, 5.1);
        Planta trampa = resistente.getJardin().getCasilla(0, 2).getPlanta();
        comprobar(trampa instanceof Patatapum || trampa instanceof Carronivora, "Difícil resistente");
        Partida garrocha = partida("Difícil", 180, vacio(1, 12)); MotorCombate mg = new MotorCombate(garrocha);
        garrocha.agregarZombiGenerado(new ZombiGarrocha(0, 0)); avanzar(mg, 5.1);
        comprobar(garrocha.getJardin().getCasilla(0, 5).getPlanta() instanceof Nuez, "Difícil contra garrocha");
        Partida grupo = partida("Difícil", 180, vacio(1, 12)); MotorCombate mgrupo = new MotorCombate(grupo);
        for (int i = 0; i < 4; i++) grupo.agregarZombiGenerado(new ZombiComun(0, 0));
        avanzar(mgrupo, 5.1); comprobar(grupo.getJardin().getCasilla(0, 3).getPlanta() instanceof Pinchohierba, "Difícil contra grupo");
        Partida base = partida("Difícil", 180, vacio(2, 12)); MotorCombate mb = new MotorCombate(base);
        avanzar(mb, 5.1); comprobar(base.getJardin().getCasilla(0, 11).getPlanta() instanceof Lanzaguisantes, "Difícil defensa por defecto");
        Partida pinchos = partida("Fácil", 180, new String[][]{{"VACIO", "PINCHOHIERBA", "VACIO"}});
        MotorCombate msp = new MotorCombate(pinchos); Zombi normal = new ZombiComun(0, 0); pinchos.invocarZombi(normal); avanzar(msp, 3.1);
        comprobar(normal.getSalud() < 100 && !pinchos.getJardin().getCasilla(0, 1).estaVacia(), "Pincho daña y resiste normales");
        Partida aplastar = partida("Fácil", 180, new String[][]{{"VACIO", "PINCHOHIERBA", "VACIO"}});
        MotorCombate map = new MotorCombate(aplastar); aplastar.invocarZombi(new Zombistein(0, 0)); avanzar(map, 4);
        comprobar(aplastar.getJardin().getCasilla(0, 1).estaVacia() && map.getCantidadCerebros() >= 3, "Zombistein aplasta y genera botín");
        Partida mina = partida("Fácil", 180, new String[][]{{"VACIO", "PATATAPUM", "VACIO"}});
        MotorCombate mmine = new MotorCombate(mina); avanzar(mmine, 10.1);
        comprobar(((Patatapum)mina.getJardin().getCasilla(0, 1).getPlanta()).estaActiva(), "Preparación Patatapum");
        Zombi comunMina = new ZombiComun(0, 0); mina.invocarZombi(comunMina); avanzar(mmine, 2);
        comprobar(!comunMina.estaVivo() && mina.getJardin().getCasilla(0, 1).estaVacia(), "Explosión Patatapum");
        Partida salto = partida("Fácil", 180, new String[][]{{"VACIO", "NUEZ", "VACIO", "VACIO"}});
        MotorCombate ms = new MotorCombate(salto); ZombiGarrocha saltador = new ZombiGarrocha(0, 0); salto.invocarZombi(saltador); avanzar(ms, 2);
        comprobar(saltador.haSaltado() && saltador.getPosicionX() < 1 && saltador.getModificadorVelocidad() == 1, "Nuez impide salto y pierde garrocha");
        Partida flotante = partida("Fácil", 180, vacio(1, 3));
        for (int i = 0; i < 77; i++) flotante.descontarUnSegundo();
        flotante.setFraccionReloj(.1); comprobar(flotante.getBloque() == 3, "Antes de frontera fraccionaria");
        flotante.setFraccionReloj(.2); comprobar(flotante.getBloque() == 4, "Horda en frontera exacta de séptimos");
        // Guardado y continuación: efectos, proyectiles, generadores, IA y posiciones idénticos.
        String[][] tablero = vacio(2, 12); tablero[0][4] = "HIELAGUISANTES"; tablero[1][5] = "PATATAPUM";
        Partida original = partida("Medio", 180, tablero); MotorCombate motor = new MotorCombate(original);
        original.invocarZombi(new Zombistein(0, 0)); avanzar(motor, 2.8); original.pausar();
        GestorPartidas gp = new GestorPartidas(); File guardado = gp.guardar(motor, .37);
        Cuenta actual = new Cuenta("Prueba"); var estado = gp.cargar(guardado, actual); MotorCombate copia = estado.getMotor();
        comprobar(copia.getPartida().estaPausada() && copia.getPartida().getCuenta() == actual && estado.getFraccionSegundo() == .37, "Carga pausada y cuenta vigente");
        comprobar(copia.getCantidadProyectiles() == motor.getCantidadProyectiles() && copia.getPartida().getZombi(0).getSalud() == original.getZombi(0).getSalud(), "Proyectiles/vida restaurados");
        original.reanudar(); copia.getPartida().reanudar(); avanzar(motor, 15); avanzar(copia, 15);
        comprobar(copia.getPartida().getZombi(0).getPosicionX() == original.getZombi(0).getPosicionX(), "Continuación posición exacta");
        comprobar(copia.getCantidadCerebros() == motor.getCantidadCerebros() && copia.getCantidadProyectiles() == motor.getCantidadProyectiles()
            && copia.getPlantasDestruidas() == motor.getPlantasDestruidas() && plantas(copia.getPartida().getJardin()) == plantas(original.getJardin()), "Continuación economía/combate/IA exactos");
        original.pausar(); motor.actualizar(0); // El estado de pausa se mantiene.
        try { gp.cargar(guardado, new Cuenta("Otra")); throw new AssertionError("Cuenta ajena"); }
        catch (IOException esperado) { comprobar(true, "Cuenta ajena rechazada"); }
        GestorCuentas gc = new GestorCuentas(); comprobar(gc.crearCuenta("Ana") && gc.crearCuenta("ana"), "Cuentas distinguen mayúsculas");
        comprobar(!gc.crearCuenta("Ana") && !gc.nicknameValido("a/b") && !gc.nicknameValido(""), "Validación nickname");
        Cuenta ana = gc.buscarCuenta("Ana"); ana.getEstadisticas().registrarVictoria(25); ana.getEstadisticas().sumarPlantasDestruidas(8);
        ana.getEstadisticas().sumarCerebrosGastados(150); ana.getEstadisticas().sumarZombisInvocados(3); ana.getEstadisticas().sumarPodadorasActivadas(1);
        comprobar(gc.guardarEstadisticasComprobadas(ana), "Guardar estadísticas");
        var e = new GestorCuentas().buscarCuenta("Ana").getEstadisticas();
        comprobar(e.getPartidasGanadas() == 1 && e.getPlantasDestruidas() == 8 && e.getCerebrosGastados() == 150 && e.getZombisInvocados() == 3
            && e.getPodadorasActivadas() == 1 && e.getMejorTiempoVictoria() == 25 && e.getPorcentajeVictorias() == 100, "Estadísticas persistentes");
        Estadisticas rapida = new Estadisticas(); rapida.registrarVictoria(0); rapida.registrarVictoria(5);
        comprobar(rapida.getMejorTiempoVictoria() == 0, "Conservar victoria de menos de un segundo");
        comprobar(!RutasCuentas.carpeta("Ana").getName().equalsIgnoreCase(RutasCuentas.carpeta("ana").getName()), "Carpetas distintas incluso en Windows");
        System.out.println("OK: " + comprobaciones + " comprobaciones.");
    }
}
