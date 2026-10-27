import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.*;
import javax.swing.*;
import com.mycompany.proyecto2_plantasvszombies.vista.*;
import com.mycompany.proyecto2_plantasvszombies.modelo.*;
import com.mycompany.proyecto2_plantasvszombies.archivos.*;

/** Datos de demostración aislados. Ejecutar desde una carpeta temporal. */
public class CapturarManuales {
    private static File destino;
    private static Object campo(Object o, String nombre) {
        try { Field f=o.getClass().getDeclaredField(nombre);f.setAccessible(true);return f.get(o); }
        catch(Exception e){throw new RuntimeException(e);}
    }
    private static void llamar(Object o,String nombre) {
        try { Method m=o.getClass().getDeclaredMethod(nombre);m.setAccessible(true);m.invoke(o); }
        catch(Exception e){throw new RuntimeException(e);}
    }
    private static JButton boton(Container c,String texto) {
        for(Component x:c.getComponents()) {
            if(x instanceof JButton && ((JButton)x).getText()!=null && ((JButton)x).getText().trim().contains(texto))return (JButton)x;
            if(x instanceof Container){JButton b=boton((Container)x,texto);if(b!=null)return b;}
        }
        return null;
    }
    private static <T> T componente(Container c,Class<T> tipo) {
        if(tipo.isInstance(c))return tipo.cast(c);
        for(Component x:c.getComponents())if(x instanceof Container){T t=componente((Container)x,tipo);if(t!=null)return t;}
        return null;
    }
    private static <T extends Window> T esperar(Class<T> tipo)throws Exception {
        for(int i=0;i<200;i++) {
            Window[] encontrada=new Window[1];
            SwingUtilities.invokeAndWait(()->{for(Window w:Window.getWindows())if(tipo.isInstance(w)&&w.isShowing())encontrada[0]=w;});
            if(encontrada[0]!=null)return tipo.cast(encontrada[0]);Thread.sleep(50);
        }
        throw new AssertionError("No apareció "+tipo.getSimpleName());
    }
    private static void foto(Window w,String nombre) {
        BufferedImage im=new BufferedImage(w.getWidth(),w.getHeight(),BufferedImage.TYPE_INT_RGB);
        Graphics2D g=im.createGraphics();w.paint(g);g.dispose();
        try{javax.imageio.ImageIO.write(im,"png",new File(destino,nombre+".png"));}
        catch(IOException e){throw new RuntimeException(e);}
        System.out.println("Captura: "+nombre);
    }
    private static void fotoEDT(Window w,String nombre)throws Exception{SwingUtilities.invokeAndWait(()->foto(w,nombre));}
    private static void click(Window w,String texto)throws Exception {SwingUtilities.invokeAndWait(()->boton(w,texto).doClick());}
    private static void clickModal(Window w,String texto){SwingUtilities.invokeLater(()->boton(w,texto).doClick());}
    private static void cerrarMensaje(JDialog d)throws Exception {
        SwingUtilities.invokeAndWait(()->{JOptionPane p=componente(d,JOptionPane.class);p.setValue(JOptionPane.OK_OPTION);d.dispose();});
    }
    private static void seleccionarNivel(VentanaNuevaPartida nueva,File nivel,boolean capturar)throws Exception {
        clickModal(nueva,"Elegir nivel");JDialog d=esperar(JDialog.class);
        SwingUtilities.invokeAndWait(()->{JFileChooser selector=componente(d,JFileChooser.class);selector.setSelectedFile(nivel);if(capturar)foto(d,"05-explorador");selector.approveSelection();});
        if(capturar)fotoEDT(nueva,"06-configuracion");
        click(nueva,"Iniciar");
    }
    public static void main(String[] args)throws Exception {
        destino=new File(args[0]);destino.mkdirs();File nivel=new File(args[1]);
        try {
            GestorCuentas gestor=new GestorCuentas();
            SwingUtilities.invokeAndWait(()->new VentanaInicio(gestor).setVisible(true));
            VentanaInicio inicio=esperar(VentanaInicio.class);fotoEDT(inicio,"01-inicio");click(inicio,"Crear");
            VentanaCrearCuenta crear=esperar(VentanaCrearCuenta.class);
            SwingUtilities.invokeAndWait(()->((JTextField)campo(crear,"jTextField1")).setText("DemoIPC1"));fotoEDT(crear,"02-crear-cuenta");
            clickModal(crear,"Crear");JDialog creado=esperar(JDialog.class);cerrarMensaje(creado);
            VentanaMenuPrincipal menu=esperar(VentanaMenuPrincipal.class);fotoEDT(menu,"04-menu");
            // Seleccion de una cuenta ya creada: mismo flujo que al reiniciar la aplicacion.
            SwingUtilities.invokeAndWait(()->{menu.dispose();new VentanaInicio(gestor).setVisible(true);});
            click(esperar(VentanaInicio.class),"Seleccionar");
            VentanaSeleccionarCuenta cuentas=esperar(VentanaSeleccionarCuenta.class);fotoEDT(cuentas,"03-seleccionar-cuenta");
            SwingUtilities.invokeAndWait(()->((JButton)campo(cuentas,"jButton1")).doClick());
            VentanaMenuPrincipal principal=esperar(VentanaMenuPrincipal.class);click(principal,"Nueva partida");
            VentanaNuevaPartida nueva=esperar(VentanaNuevaPartida.class);seleccionarNivel(nueva,nivel,true);
            VentanaPartida juego=esperar(VentanaPartida.class);Thread.sleep(200);fotoEDT(juego,"07-partida");
            // Compra real por los mismos eventos que usa el jugador.
            SwingUtilities.invokeAndWait(()->{
                boton(juego,"Común").doClick();
                PanelJardinFondo panel=(PanelJardinFondo)campo(juego,"tablero");
                try {
                    Method m=panel.getClass().getDeclaredMethod("calcularAreaCesped");m.setAccessible(true);Object a=m.invoke(panel);
                    Field x=a.getClass().getDeclaredField("x"), y=a.getClass().getDeclaredField("y"), w=a.getClass().getDeclaredField("anchoCasilla"), h=a.getClass().getDeclaredField("altoCasilla");
                    for(Field f:new Field[]{x,y,w,h})f.setAccessible(true);
                    int xx=(int)(x.getDouble(a)+w.getDouble(a)*.2), yy=(int)(y.getDouble(a)+h.getDouble(a)*.5);
                    panel.dispatchEvent(new java.awt.event.MouseEvent(panel,java.awt.event.MouseEvent.MOUSE_CLICKED,System.currentTimeMillis(),0,xx,yy,1,false));
                }catch(Exception e){throw new RuntimeException(e);}
            });
            for(int i=0;i<400;i++){
                boolean[] listo={false};SwingUtilities.invokeAndWait(()->listo[0]=((MotorCombate)campo(juego,"motor")).getCantidadCerebros()>0);
                if(listo[0])break;Thread.sleep(50);
                if(i==399)throw new AssertionError("No aparecieron cerebros de hilos reales");
            }
            fotoEDT(juego,"08-combate-cerebros");click(juego,"Pausar / Reanudar");
            JDialog pausa=esperar(JDialog.class);fotoEDT(pausa,"09-pausa");
            int[] tiempoGuardado={0};SwingUtilities.invokeAndWait(()->tiempoGuardado[0]=((Partida)campo(juego,"partida")).getTiempoRestante());
            click(pausa,"Guardar y salir");VentanaMenuPrincipal guardado=esperar(VentanaMenuPrincipal.class);
            clickModal(guardado,"Reanudar partida");JDialog lista=esperar(JDialog.class);fotoEDT(lista,"10-lista-guardados");
            cerrarMensaje(lista);VentanaPartida cargado=esperar(VentanaPartida.class);JDialog pausaCargada=esperar(JDialog.class);
            SwingUtilities.invokeAndWait(()->{Partida p=(Partida)campo(cargado,"partida");if(!p.estaPausada()||p.getTiempoRestante()!=tiempoGuardado[0])throw new AssertionError("Carga exacta en pausa");});
            fotoEDT(cargado,"11-tablero-restaurado");fotoEDT(pausaCargada,"12-reanudacion-pausada");
            click(pausaCargada,"Reanudar");Thread.sleep(300);click(cargado,"Pausar / Reanudar");click(esperar(JDialog.class),"Guardar y salir");
            VentanaMenuPrincipal trasCarga=esperar(VentanaMenuPrincipal.class);click(trasCarga,"Nueva partida");
            File corto=new File("nivel-demostracion.json");
            try(FileWriter w=new FileWriter(corto)){w.write("{\"duracionSegundos\":3,\"cerebrosIniciales\":150,\"jardin\":[[\"VACIO\",\"NUEZ\",\"VACIO\",\"LANZAGUISANTES\"]]}");}
            seleccionarNivel(esperar(VentanaNuevaPartida.class),corto,false);
            JDialog resultado=esperar(JDialog.class);fotoEDT(resultado,"13-resultado");cerrarMensaje(resultado);
            VentanaMenuPrincipal fin=esperar(VentanaMenuPrincipal.class);
            // Historicos de demostracion para mostrar los tres rankings con varios jugadores.
            GestorCuentas gc=new GestorCuentas();
            String[] nombres={"Luna","Sol","Nube"};
            for(int i=0;i<nombres.length;i++){
                gc.crearCuenta(nombres[i]);Cuenta c=gc.buscarCuenta(nombres[i]);
                for(int j=0;j<=i;j++)c.getEstadisticas().registrarVictoria(20+i*10);
                c.getEstadisticas().sumarPlantasDestruidas(10+i*8);gc.guardarEstadisticas(c);
            }
            clickModal(fin,"Estadísticas");JDialog estadisticas=esperar(JDialog.class);fotoEDT(estadisticas,"14-estadisticas");
            SwingUtilities.invokeAndWait(()->componente(estadisticas,JTabbedPane.class).setSelectedIndex(1));fotoEDT(estadisticas,"15-ranking");cerrarMensaje(estadisticas);
            System.out.println("OK: flujo real crear/seleccionar/nueva partida/generar/guardar/cargar/derrota/estadisticas.");
        } finally {SwingUtilities.invokeAndWait(()->{for(Window w:Window.getWindows())w.dispose();});}
    }
}
