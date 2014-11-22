package prt;

import sun.audio.AudioPlayer;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;


interface Logic {
    public abstract void apply() throws Exception;
}// end interface Logic

public class Utils {

    //Class c = getClass();

    protected static Clip makeClip(Class c,String soundFile){
        Clip clip = null;
        try{
            AudioInputStream ais = AudioSystem.getAudioInputStream(c.getClass().getResource(soundFile));
            DataLine.Info lineInfo = new DataLine.Info(Clip.class, ais.getFormat());
            clip = (Clip) AudioSystem.getLine(lineInfo);
            //clip = AudioSystem.getClip();
            clip.open(ais);

        }catch (Exception e){
            e.printStackTrace();
        }
        return clip;
        //clip.loop(2);
        //Clip theme = AudioSystem.getClip();
    }//..

    public static JLabel makeGraphic(Class c,String image,int x,int y,int w, int h){
        try {
            ImageIcon img;
            img = new ImageIcon(ImageIO.read(c.getResourceAsStream(image)));
            img = Utils.scaleImageIcon(img, w, h);
            JLabel graphic = new JLabel(img);
            graphic.setBounds(x, y, w, h);
            return graphic;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }//..

    public static Vector<ImageIcon>  makeSprites(Class c,String spriteFile,int imgWidth, int imgHeight,int scaleWidth,int scaleHeight,int rowcnt, int colcnt) {
        BufferedImage i;
        ImageIcon img;
        Vector<ImageIcon> sprites = new Vector<ImageIcon>();

        try {
            i = ImageIO.read(c.getClass().getResourceAsStream(spriteFile));
            for (int row=0;row<rowcnt;row++) {
                for (int col=0;col<colcnt;col++) {
                    img = new ImageIcon(i.getSubimage((col * imgWidth), (row * imgHeight), imgWidth, imgHeight));
                    img = Utils.scaleImageIcon(img, scaleWidth, scaleHeight);
                    sprites.add(img);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sprites;
    }//..

    public static ImageIcon scaleImageIcon(ImageIcon icon, int w, int h){
        Image img = icon.getImage() ;
        return new ImageIcon(  img.getScaledInstance( w, h,  java.awt.Image.SCALE_SMOOTH )  );
    }//..

    protected static void startThread(final Logic logic){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        logic.apply();
                        Thread.sleep(20);
                    }catch (Exception e){}
                }
            }
        }).start();
    }//..

    public static void playSound(Class c,String path){
        AudioPlayer.player.start(c.getResourceAsStream(path));
    }//..
    public static void wait(int mils){
        try {
            Thread.sleep(mils);
        }catch (Exception e){}
    }//..

}// end Class Utils
