import com.mycompany.proyecto2_plantasvszombies.vista.*;
import com.mycompany.proyecto2_plantasvszombies.modelo.*;
import com.mycompany.proyecto2_plantasvszombies.zombis.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.*;
import javax.swing.SwingUtilities;

public class PruebasVisuales {
    private static int comprobaciones;
    private static Object campo(Object o,String n)throws Exception{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);return f.get(o);}
    private static Object sprite(PanelJardinFondo p,Zombi z)throws Exception{Method m=p.getClass().getDeclaredMethod("spriteZombi",Zombi.class);m.setAccessible(true);return m.invoke(p,z);}
    private static void verificar(boolean b,String s){if(!b)throw new AssertionError(s);comprobaciones++;}
    public static void main(String[] args)throws Exception {
        SwingUtilities.invokeAndWait(()->{
            try {
                String[][] a=new String[8][8];for(int f=0;f<8;f++)for(int c=0;c<8;c++)a[f][c]="VACIO";
                Partida p=new Partida(new Cuenta("Visual"),new Jardin(a),new ConfiguracionNivel(180,2000,a),"Fácil");MotorCombate m=new MotorCombate(p);
                PanelJardinFondo panel=new PanelJardinFondo();panel.setJardin(p.getJardin());panel.setPartida(p);panel.setMotorCombate(m);panel.setSize(1160,800);
                ZombiComun comun=new ZombiComun(0,1.5), daniado=new ZombiComun(0,4.5);daniado.recibirDanio(51);
                p.agregarZombiGenerado(comun);p.agregarZombiGenerado(daniado);
                verificar(sprite(panel,daniado)==campo(panel,"comunSinBrazo"),"Común cambia imagen al 50%");
                ZombiCaracono cono=new ZombiCaracono(1,1.5), sinCono=new ZombiCaracono(1,4.5);sinCono.recibirDanio(100);
                p.agregarZombiGenerado(cono);p.agregarZombiGenerado(sinCono);
                verificar(sprite(panel,cono)!=sprite(panel,sinCono),"Cono desaparece");
                ZombiCaracono conoDaniado=new ZombiCaracono(0,0);conoDaniado.recibirDanio(151);
                verificar(sprite(panel,conoDaniado)==campo(panel,"comunSinBrazo"),"Sin cono y sin brazo");
                ZombiDeportista casco=new ZombiDeportista(2,1.5), sinCasco=new ZombiDeportista(2,4.5);sinCasco.recibirDanio(150);
                p.agregarZombiGenerado(casco);p.agregarZombiGenerado(sinCasco);
                verificar(sprite(panel,sinCasco)==campo(panel,"cascoSinCasco"),"Casco desaparece realmente");
                ZombiGarrocha garrocha=new ZombiGarrocha(3,1.5), sinGarrocha=new ZombiGarrocha(3,4.5);sinGarrocha.realizarSalto();
                p.agregarZombiGenerado(garrocha);p.agregarZombiGenerado(sinGarrocha);
                verificar(sprite(panel,sinGarrocha)==campo(panel,"garrochaSinGarrocha"),"Garrocha desaparece");
                ZombiGarrocha brazoConPalo=new ZombiGarrocha(4,1.5), brazoSinPalo=new ZombiGarrocha(4,4.5);
                brazoConPalo.recibirDanio(61);brazoSinPalo.recibirDanio(61);brazoSinPalo.realizarSalto();
                p.agregarZombiGenerado(brazoConPalo);p.agregarZombiGenerado(brazoSinPalo);
                verificar(sprite(panel,brazoConPalo)==campo(panel,"garrochaConGarrochaSinBrazo"),"Brazo y arma son estados independientes");
                verificar(sprite(panel,brazoSinPalo)==campo(panel,"garrochaSinBrazo"),"Sin brazo después de saltar");
                Zombistein gigante=new Zombistein(5,1.5), sinHijo=new Zombistein(5,4.5);sinHijo.recibirDanio(150);
                p.agregarZombiGenerado(gigante);p.agregarZombiGenerado(sinHijo);
                ZombiBailon bailon=new ZombiBailon(6,1.5);ZombiBailarin bailarin=new ZombiBailarin(6,4.5);
                p.agregarZombiGenerado(bailon);p.agregarZombiGenerado(bailarin);
                verificar(sprite(panel,bailarin)!=sprite(panel,comun),"Bailarín con apariencia propia");
                Zombidito pequeno=new Zombidito(7,4.5);p.agregarZombiGenerado(pequeno);p.agregarZombiGenerado(new ZombiComun(7,1.5));
                verificar(sprite(panel,pequeno)==campo(panel,"imagenZombidito"),"Sprite propio de Zombidito");
                m.actualizar(.05);
                verificar(sinHijo.haLanzadoZombidito() && sprite(panel,sinHijo)==campo(panel,"giganteSinZombidito"),"Gigante cambia despues de lanzar");
                Zombi hijo=p.getZombi(p.getCantidadZombis()-1);
                verificar(hijo instanceof Zombidito && m.estaEnVuelo(hijo),"Lanzamiento crea un hijo en vuelo");
                double antes=m.posicionVisual(hijo);m.actualizar(.15);m.actualizar(.15);
                verificar(m.alturaVuelo(hijo)>0 && m.posicionVisual(hijo)!=antes,"Trayectoria animada en arco");
                p.pausar();double altura=m.alturaVuelo(hijo);m.actualizar(.1);
                verificar(m.alturaVuelo(hijo)==altura,"Vuelo detenido durante pausa");
                BufferedImage foto=new BufferedImage(1160,800,BufferedImage.TYPE_INT_RGB);Graphics2D g=foto.createGraphics();panel.paint(g);g.dispose();
                if(args.length>0)javax.imageio.ImageIO.write(foto,"png",new File(args[0]));
                p.reanudar();for(int i=0;i<20;i++)m.actualizar(.05);
                verificar(!m.estaEnVuelo(hijo),"Aterriza y vuelve al combate");
                // Detecta recursos ausentes: los metodos devolverian null si faltara alguno.
                for(String n:new String[]{"comunSinBrazo","cascoSinCasco","garrochaSinGarrocha","garrochaSinBrazo","garrochaConGarrochaSinBrazo","giganteSinZombidito","imagenBailarin","imagenZombidito"})
                    verificar(campo(panel,n) instanceof BufferedImage,"Recurso obligatorio: "+n);
            }catch(Exception e){throw new RuntimeException(e);}
        });
        System.out.println("OK: "+comprobaciones+" comprobaciones visuales y de lanzamiento.");
    }
}
