package prt;

import java.awt.*;

/**
 * Created by kevin on 11/10/14.
 */
public class Particle {

    Color color;
    //Graphics2D g2d;

    float x;
    float y;
    float w;
    float h;
    double dx;
    double dy;
    float eloss = .99f;
    float death = .5f;
    float life;
    float decay = .99f;

    Particle(float x,float y){
        this.x=x;this.y=y;
        w=h=1;
        dx = random(3);
        dy = random(3);
        life=1;//(float)Math.random();
        color = new Color(.3f,0,.3f,life);
    }//..

    Particle(float x,float y,float decay){
        this.x=x;this.y=y;
        w=h=1;
        dx = random(3);
        dy = random(3);
        this.decay=decay;
        life=1;//(float)Math.random();
        color = new Color(.3f,0,.3f,life);
    }//..

    public void render(Graphics2D g){
        constraints();
        g.setColor(color);
        g.fillRect((int)x,(int)y , (int) w, (int) h);
    }//..

    protected double random(int max){
        double rand =  Math.random()*max;
        if((int)(Math.random()*100) < 50)
            rand = -rand;
        return rand;
    }//..


    protected void constraints(){
        color = new Color(.5f, 0, 1f, life);
        life*= decay;
        x+=(dx *= eloss);
        y+=(dy *= eloss);
        dx=x<=0||x>ParticleSimulator.frame.getWidth()?-dx:dx;
        dy=y<=0||y>ParticleSimulator.frame.getHeight()?-dy:dy;
        if(life <= death){
            ParticleScene.particles.remove(this);
        }
    }//..

}// Particle
