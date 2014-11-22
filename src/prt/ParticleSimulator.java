package prt;

import javax.swing.*;
import java.awt.*;

/**
 * Created by kevin on 11/10/14.
 */
public class ParticleSimulator extends JFrame {


    static JFrame frame;
    static ParticleScene ps;

    ParticleSimulator(){
        //setSize(900, 600);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.getSize());
        setLayout(null);
        setIgnoreRepaint(true);
        setLocationRelativeTo(null);
        setTitle("Particle Simulator");
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame = this;
        ps = new ParticleScene();
        add(ps);
        setVisible(true);
        createBufferStrategy(3);
        ps.setBufferStrategy(getBufferStrategy());
    }//..

    public static void main(String[] args) {
        new ParticleSimulator();
    }//..
}// end ParticleSimulator





