import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.*;
import com.mycompany.proyecto2_plantasvszombies.vista.*;
import com.mycompany.proyecto2_plantasvszombies.modelo.*;
import com.mycompany.proyecto2_plantasvszombies.archivos.*;

/** Victoria real mediante dos compras y el reloj de la aplicacion, sin acelerar el motor. */
public class PruebasPartidaCompleta {
    private static VentanaPartida ventana;
    private static Partida partida;
    private static Object campo(Object o,String n){try{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);return f.get(o);}catch(Exception e){throw new RuntimeException(e);}}
    private static JButton boton(Container c){for(Component x:c.getComponents()){if(x instanceof JButton && ((JButton)x).getText().contains("Común"))return (JButton)x;if(x instanceof Container){JButton b=boton((Container)x);if(b!=null)return b;}}return null;}
    private static void invocar()throws Exception{SwingUtilities.invokeAndWait(()->{
        boton(ventana).doClick();PanelJardinFondo p=(PanelJardinFondo)campo(ventana,"tablero");
        try{Method m=p.getClass().getDeclaredMethod("calcularAreaCesped");m.setAccessible(true);Object a=m.invoke(p);
            double[] v=new double[4];String[] n={"x","y","anchoCasilla","altoCasilla"};for(int i=0;i<4;i++){Field f=a.getClass().getDeclaredField(n[i]);f.setAccessible(true);v[i]=f.getDouble(a);}
            p.dispatchEvent(new MouseEvent(p,MouseEvent.MOUSE_CLICKED,System.currentTimeMillis(),0,(int)(v[0]+v[2]*.2),(int)(v[1]+v[3]*.5),1,false));
        }catch(Exception e){throw new RuntimeException(e);}
    });}
    public static void main(String[] args)throws Exception{
        try{
            SwingUtilities.invokeAndWait(()->{
                GestorCuentas gc=new GestorCuentas();gc.crearCuenta("VictoriaUI");Cuenta c=gc.buscarCuenta("VictoriaUI");
                String[][] a={{"VACIO","VACIO"}};partida=new Partida(c,new Jardin(a),new ConfiguracionNivel(20,100,a),"Fácil");
                ventana=new VentanaPartida(gc,c,new MotorCombate(partida),0,null);ventana.setVisible(true);
            });
            Thread.sleep(150);invocar();boolean podadora=false;
            for(int i=0;i<200;i++){boolean[] b={false};SwingUtilities.invokeAndWait(()->b[0]=partida.getPodadorasActivadas()==1);if(b[0]){podadora=true;break;}Thread.sleep(50);}
            if(!podadora)throw new AssertionError("No activo podadora");invocar();
            JDialog[] resumen={null};
            for(int i=0;i<200;i++){
                SwingUtilities.invokeAndWait(()->{for(Window w:Window.getWindows())if(w instanceof JDialog&&w.isShowing())resumen[0]=(JDialog)w;});
                if(resumen[0]!=null)break;Thread.sleep(50);
            }
            if(resumen[0]==null)throw new AssertionError("No mostro resumen");
            SwingUtilities.invokeAndWait(()->{
                if(!partida.estaTerminada()||partida.getCuenta().getEstadisticas().getPartidasGanadas()!=1||partida.getZombisComprados()!=2||partida.getCerebrosGastados()!=100)throw new AssertionError("Victoria o contadores");
                if(args.length>0)try{JDialog d=resumen[0];BufferedImage im=new BufferedImage(d.getWidth(),d.getHeight(),BufferedImage.TYPE_INT_RGB);Graphics2D g=im.createGraphics();d.paint(g);g.dispose();javax.imageio.ImageIO.write(im,"png",new File(args[0]));}catch(Exception e){throw new RuntimeException(e);}
                resumen[0].dispose();
            });
            if(new GestorCuentas().buscarCuenta("VictoriaUI").getEstadisticas().getPartidasGanadas()!=1)throw new AssertionError("Victoria no persistida");
            System.out.println("OK: partida completa por clics: primera llegada/podadora, segunda llegada/victoria, resumen y persistencia.");
        }finally{SwingUtilities.invokeAndWait(()->{for(Window w:Window.getWindows())w.dispose();});}
    }
}
