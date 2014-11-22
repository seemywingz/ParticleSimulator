package prt;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import javax.vecmath.Vector3f;
import java.awt.*;

/**
 * Created by kevin on 11/11/14.
 */
public class ParticleWithGravity extends Particle {

    // Physics Variables
    protected double
            gravity = 15,
            time = .2;
    float rad,r,g,b,a;

    //JBullet Variables
    Transform t;
    RigidBody body;
    Vector3f p = new Vector3f(0,0,0);
    float mass;

    static Color[] col={Color.BLUE,Color.red,Color.CYAN,Color.DARK_GRAY,
    Color.YELLOW,Color.green,Color.gray};

    boolean pulled=false;

    ParticleWithGravity(float x, float y,float mass) {
        super(x, y);
        this.mass=mass;
        r = 01f; g = 0.1f; b = 0.1f; a=.7f;
        color = new Color(r,g,b,a);
        w=1;
        rad=w*.5f;
        initJBullet();
        //color=col[(int)(Math.random()*col.length)];
    }//..

    @Override
    public void render(Graphics2D g2d) {
        body.setActivationState(1);
        t = body.getWorldTransform(t);
        g2d.setColor(color);
        g2d.fillRect((int) (t.origin.x - rad), (int) (t.origin.y), (int) (w), (int) (w));
    }//..  */

    protected void initJBullet(){
        MotionState motionState;
        CollisionShape shape = new SphereShape(rad);

        t = new Transform();
        t.setIdentity();
        t.origin.set(new Vector3f(x,y,0));
        Vector3f inertia = new Vector3f(0,0,0);
        shape.calculateLocalInertia(mass,inertia);

        motionState = new DefaultMotionState(t);
        RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(mass,motionState,shape,inertia);
        body = new RigidBody(info);
        body.setFriction(.1f);
        body.setDamping(0.005f, .001f);
        //body.setAngularFactor(0);
        hold();
        ParticleScene.dynamicsWorld.addRigidBody(body);
    }//..

    public void setGravity(float x,float y){
        float tx = t.origin.x,ty = t.origin.y, z = t.origin.z;
        body.setActivationState(1);
        body.applyForce(new Vector3f(x-tx, y-ty, z), new Vector3f(x,y,0));
    }//..

    @Override
    protected void constraints() {
        if(Math.abs(dx)>.0002 || Math.abs(dy)>.0002 && !pulled) {
            x += dx;
            dy += gravity * time;
            y += dy * time + .5 * gravity * time * time;

            if (x <= 0) {
                x = 0;
                dx = -dx * eloss;
            }
            if (x > ParticleSimulator.frame.getWidth()) {
                x = ParticleSimulator.frame.getWidth();
                dx = -dx * eloss;
            }
            if (y <= 5) {
                y = 5;
                dy = -dy * eloss;
            }
            int floor = ParticleSimulator.frame.getHeight()-5;
            if (y > floor) {
                y = floor;
                dy = -dy * eloss;
                dx = dx * eloss;
            }
        }
    }//..


    protected void pull(double targetX, double targetY){
        double dist = Position.calcDistance(new Position(targetX,targetY),new Position(x,y));
        //x =
    }//..

    protected static void hold(){
        while (ParticleScene.solving){Utils.wait(1);}
    }//...
}// Molecule
