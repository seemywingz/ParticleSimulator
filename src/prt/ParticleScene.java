package prt;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import javax.swing.*;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.util.Vector;

/**
 * Created by kevin on 11/10/14.
 */
public class ParticleScene extends JPanel {

    static Vector<Particle> particles = new Vector<Particle>();
    static Vector<ParticleWithGravity> particlesWithGravity = new Vector<ParticleWithGravity>();
    BufferStrategy bs;
    Color bgColor = Color.BLACK;
    boolean hasBuffer = false;
    JLabel info;

    // JBullet Components
    static boolean
            solving,
            reset,
            gravity,
            isGraviton,
            isBoundingBox;
    float massToadd = 1;
    Position graviton;
    static DiscreteDynamicsWorld dynamicsWorld;
    Vector<RigidBody> boundingBox = new Vector<RigidBody>();
    static final float FPS=1f/20f;


    ParticleScene(){
        setIgnoreRepaint(true);
        addMouseListener(mkMouseAdapter());
        addMouseMotionListener(mkMouseAdapter());
        addKeyListener(mkKeyAdapter());
        setFocusable(true);
        requestFocus();
        setBounds(0, 0, ParticleSimulator.frame.getWidth(), ParticleSimulator.frame.getHeight());
        initJBullet();

        mkHUD();

        Utils.startThread(new Logic() {
            @Override
            public void apply() throws Exception {
                if (hasBuffer) {
                    setSize(ParticleSimulator.frame.getSize());
                    if (isGraviton)
                        for (ParticleWithGravity p : particlesWithGravity) {
                            p.setGravity((float) graviton.x, (float) graviton.y);
                        }
                    renderParticles();
                    updateInfo();
                }
            }
        });
        Utils.startThread(new Logic() {
            @Override
            public void apply() throws Exception {

                solving = true;
                dynamicsWorld.stepSimulation(FPS, 0);
                solving = false;

                if (reset) {
                    for (ParticleWithGravity p : particlesWithGravity) {
                        dynamicsWorld.removeRigidBody(p.body);
                    }
                    reset = false;
                    particlesWithGravity.removeAllElements();
                }
            }
        });
    }//..

    protected void updateInfo(){
        int pcnt = particlesWithGravity.size();
        String infoString = "<html><div style='margin-left:10px;'>";
        infoString+= "<p>Particles: "+pcnt+"</p>";

        if(gravity)
            infoString+="<p>Gravity: ON</p>";
        else
            infoString+="<p>Gravity: OFF</p>";

        infoString+="<p>New Particle Mass: "+massToadd+"</p>";

        if(isGraviton)
            infoString+="<p>Graviton: "+graviton.x+", "+graviton.y+"</p>";
        else
            infoString+="<p>Graviton: NONE</p>";

        if(isBoundingBox)
            infoString+="<p>BoundingBox: ON</p>";
        else
            infoString+="<p>BoundingBox: OFF</p>";

        infoString+="</div></html>";
        info.setText(infoString);
    }//..

    protected void renderParticles(){
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.setColor(bgColor);
        g.fillRect(0, 0, ParticleSimulator.frame.getWidth(), ParticleSimulator.frame.getHeight());
        for (Particle p : particles) {
            p.render(g);
        }
        for (ParticleWithGravity m : particlesWithGravity) {
            m.render(g);
        }
        info.paint(g);
        g.dispose();
        bs.show();
        //System.out.println(particles.size());
    }//..
    protected void initJBullet(){
        // JBullet Stuff
        DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(
                collisionConfiguration);
        float bound = 1000;
        Vector3f worldAabbMin = new Vector3f(-bound, -bound, -bound);
        Vector3f worldAabbMax = new Vector3f(bound, bound, bound);
        int maxProxies = 5000;
        AxisSweep3 overlappingPairCache =
                new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);
        BroadphaseInterface broadphase = new DbvtBroadphase();
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        dynamicsWorld = new DiscreteDynamicsWorld(
                dispatcher,overlappingPairCache, solver,
                collisionConfiguration);
        dynamicsWorld.setGravity(new Vector3f(0, 0f, 0));
        //mkBoundingBox();
    }//..

    protected void mkBoundingBox(){
        // floor
        float mass = 0;
        MotionState motionState;
        CollisionShape shape = new BoxShape(new Vector3f(3000,010,3000));


        Transform t = new Transform();
        t.setIdentity();
        t.origin.set(new Vector3f(300, 550, 0));

        motionState = new DefaultMotionState(t);
        RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(mass,motionState,shape);
        RigidBody body = new RigidBody(info);
        //body.setFriction(1f);
       // body.setDamping(.3f,.3f);
        boundingBox.add(body);
        dynamicsWorld.addRigidBody(body);

        // top
         shape = new BoxShape(new Vector3f(3000,010,3000));

         t = new Transform();
        t.setIdentity();
        t.origin.set(new Vector3f(300, 5, 0));

        motionState = new DefaultMotionState(t);
         info = new RigidBodyConstructionInfo(mass,motionState,shape);
        body = new RigidBody(info);
        //body.setFriction(1f);
        //body.setDamping(.3f,.3f);
        boundingBox.add(body);
        dynamicsWorld.addRigidBody(body);

        //left wall
        mass = 0;
        shape = new BoxShape(new Vector3f(3,3000,3000));


        t = new Transform();
        t.setIdentity();
        t.origin.set(new Vector3f(5, 0, 0));

        motionState = new DefaultMotionState(t);
        info = new RigidBodyConstructionInfo(mass,motionState,shape);
        body = new RigidBody(info);
        //body.setFriction(1f);
        //body.setDamping(.3f,.3f);
        boundingBox.add(body);
        dynamicsWorld.addRigidBody(body);

        // right wall
        mass = 0;
        shape = new BoxShape(new Vector3f(3,3000,3000));


        t = new Transform();
        t.setIdentity();
        t.origin.set(new Vector3f(890, 0, 0));

        motionState = new DefaultMotionState(t);
        info = new RigidBodyConstructionInfo(mass,motionState,shape);
        body = new RigidBody(info);
        //body.setFriction(1f);
        //body.setDamping(.3f,.3f);
        boundingBox.add(body);
        dynamicsWorld.addRigidBody(body);
    }//..

    protected KeyAdapter mkKeyAdapter(){
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getExtendedKeyCode() == KeyEvent.getExtendedKeyCodeForChar('c')){
                    System.out.println("\n"+particles.size()+" Particles");
                    System.out.println(particlesWithGravity.size() + " Molecules");
                    System.out.println(dynamicsWorld.getCollisionObjectArray().size() + " Objects");
                }
                if(e.getExtendedKeyCode() == KeyEvent.getExtendedKeyCodeForChar('r')){
                    reset=true;
                }
                if(e.getExtendedKeyCode() == KeyEvent.getExtendedKeyCodeForChar('m')){
                    try {
                        String pm = JOptionPane.showInputDialog("New Particle Mass");
                        massToadd = Float.parseFloat(pm);
                    }catch (Exception ne){
                        massToadd =1;
                    }
                }
                if(e.getExtendedKeyCode() == KeyEvent.getExtendedKeyCodeForChar('f')){
                    toggleBoundingBox();
                }
                if(e.getExtendedKeyCode() == KeyEvent.getExtendedKeyCodeForChar('g')){
                    if(!e.isShiftDown()) {
                        if (!gravity) {
                            gravity = true;
                            dynamicsWorld.setGravity(new Vector3f(0, 10, 0));
                        } else {
                            gravity = false;
                            dynamicsWorld.setGravity(new Vector3f(0, 0, 0));
                        }
                    }else{
                        if(isGraviton){
                            isGraviton=false;
                        }else {
                            isGraviton=true;
                        }
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    System.exit(0);
                }
            }
        };
    }//..

    protected void toggleBoundingBox(){
        if(isBoundingBox){
            isBoundingBox = false;
            ParticleWithGravity.hold();
            for (RigidBody b:boundingBox)
                dynamicsWorld.removeRigidBody(b);
        }else{
            isBoundingBox =true;
            if(boundingBox.size()==0){
                mkBoundingBox();
            }
            ParticleWithGravity.hold();
            for (RigidBody b:boundingBox)
                dynamicsWorld.addRigidBody(b);
        }
    }//..

    protected MouseAdapter mkMouseAdapter(){
        return new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);

                if(SwingUtilities.isMiddleMouseButton(e))
                    particles.add(new Particle(e.getX(),e.getY()));

                if(SwingUtilities.isLeftMouseButton(e)) {
                    particlesWithGravity.add(new ParticleWithGravity(e.getX(), e.getY(),massToadd));
                }

            }//..

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
            }//..

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(SwingUtilities.isLeftMouseButton(e)) {
                    particlesWithGravity.add(new ParticleWithGravity(e.getX(), e.getY(),massToadd));
                }

                int mx = e.getX(),my=e.getY(),
                    hy = 68,h =17;
                if(mx>=10 && mx<=145){
                    if(my >=hy+h && my <=hy+h*2){
                        if (!gravity) {
                            gravity = true;
                            dynamicsWorld.setGravity(new Vector3f(0, 10, 0));
                        } else {
                            gravity = false;
                            dynamicsWorld.setGravity(new Vector3f(0, 0, 0));
                        }
                    }else
                    if(my >=hy+h*2 && my <=hy+h*3) {
                        try {
                            String pm = JOptionPane.showInputDialog("New Particle Mass");
                            massToadd = Float.parseFloat(pm);
                        }catch (Exception ne){
                            massToadd =1;
                        }
                    }else
                    if(my >=hy+h*3 && my <=hy+h*4) {
                        if(isGraviton){
                            isGraviton=false;
                        }else {
                            isGraviton=true;
                        }
                    }
                    if(my >=hy+h*4 && my <=hy+h*5) {
                        toggleBoundingBox();
                    }
                }

                if(SwingUtilities.isRightMouseButton(e)) {
                    graviton = new Position(e.getX(), e.getY());
                }
            }
        };
    }//..

    protected void mkHUD(){

        JFrame frame =ParticleSimulator.frame;
        info = new JLabel();
        info.setBounds(10,10,300,200);

        final JButton gravBtn = new JButton("Gravity: OFF");
        gravBtn.setBounds(10, 260, 100, 20);
        gravBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gravity) {
                    gravity = true;
                    gravBtn.setText("Gravity: ON");
                    dynamicsWorld.setGravity(new Vector3f(0, 10, 0));
                } else {
                    gravity = false;
                    gravBtn.setText("Gravity: OFF");
                    dynamicsWorld.setGravity(new Vector3f(0, 0, 0));
                }
            }
        });

    }//..

    public void setBufferStrategy(BufferStrategy bs){
        this.bs = bs;
        hasBuffer=true;
    }//..

}// ParticleScene
