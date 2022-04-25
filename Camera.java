import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JPanel;

public class Camera
{
    private double fov; //strictly reffers to the horizontal fov as vertical fov is based off screen height 
    private Vector3 position;
    private double h_orientation;
    private double v_orientation;
    private double renderPlaneDistance;
    private double viewDistance;

    private OrbitCamController orbitController = null;
    private FreeCamController freeCamController = null;

    private double renderPlaneWidth;

    public Camera(Vector3 positionIn, double viewDistanceIn, double fovIn)
    {
        renderPlaneDistance = 10;
        h_orientation = 0;
        v_orientation = 0;
        position = positionIn;
        viewDistance = viewDistanceIn;
        setFov(fovIn);
    }

    public void lookAt(Vector3 pos)
    {
        h_orientation = (pos.x-position.x < 0)? -Math.toDegrees(Math.atan((pos.z-position.z)/(pos.x-position.x)))-90 : 90-Math.toDegrees(Math.atan((pos.z-position.z)/(pos.x-position.x)));

        v_orientation = Math.toDegrees(Math.atan((pos.y-position.y)/(Math.sqrt((pos.x-position.x)*(pos.x-position.x) + (pos.z-position.z)*(pos.z-position.z)))));
        
        //Vector3(Math.sin(horizontalAng)*Math.cos(verticalAng), Math.sin(verticalAng), Math.cos(horizontalAng)*Math.cos(verticalAng))

        h_orientation%=360;
        v_orientation%=360;
    }

    class FreeCamController implements KeyListener, MouseMotionListener, MouseListener
    {
        private int prevX = 0;
        private int prevY = 0;
        private double sensitivity;
        private double movementSpeed;

        public FreeCamController(double sensitivityIn, double movementSpeedIn)
        {
            sensitivity = sensitivityIn;
            movementSpeed = movementSpeedIn;
        }

        public void mouseDragged(MouseEvent e) 
        {
            h_orientation = h_orientation + (e.getX()-prevX)/(100/sensitivity);
            v_orientation = v_orientation - (e.getY()-prevY)/(100/sensitivity);
            if (h_orientation < 0)
                h_orientation += 360;
            if (v_orientation < 0)
                h_orientation += 360;
            h_orientation%=360;
            v_orientation%=360;

            prevX = e.getX();
            prevY = e.getY();
        }

        public void keyPressed(KeyEvent e) 
        {
            switch (e.getKeyChar()) 
            {
                case 'w':
                    moveForward(movementSpeed);
                    break;
                case 's':
                    moveForward(-movementSpeed);
                    break;
                case 'a':
                    moveLeft(movementSpeed);
                    break;
                case 'd':
                    moveLeft(-movementSpeed);
                    break;
                case 'e':
                    moveUp(movementSpeed);
                    break;
                case 'q':
                    moveUp(-movementSpeed);
                    break;
            }
        }

        public void mousePressed(MouseEvent e) 
        {
            prevX = e.getX();
            prevY = e.getY();
        }

        public void keyReleased(KeyEvent e) {}
        public void mouseClicked(MouseEvent e) {}

        public void mouseMoved(MouseEvent e) {}
        public void keyTyped(KeyEvent e) {}

        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
    }

    class OrbitCamController implements MouseMotionListener, MouseWheelListener, MouseListener
    {
        private int maxDistance = 3000;
        private int minDistance = 300;

        private double distance;

        private int maxAngle = 80;
        private int minAngle = -80;

        private GameObject focusObj;
        private double sensitivity;

        private int prevX = 0;
        private int prevY = 0;

        private Vector3 difference = new Vector3();
        private Vector3 directionUnit = new Vector3();
        
        public OrbitCamController(GameObject focusObjectIn, double startDistanceIn, double sensitivityIn)
        {
            focusObj = focusObjectIn;
            distance = startDistanceIn;
            sensitivity = sensitivityIn;

            position = Vector3.add(focusObj.getTransform().getPosition(), new Vector3(0, 0, -distance));
            directionUnit = Vector3.subtract(position, focusObj.getTransform().getPosition()).getNormalized();
        }

        public void mouseWheelMoved(MouseWheelEvent e) 
        {
            distance = Math.max(minDistance, Math.min(distance + e.getWheelRotation()*30, maxDistance));
            difference = Vector3.multiply(directionUnit, distance);
            updatePosition();
        }

        public void mouseDragged(MouseEvent e) 
        {
            directionUnit = Vector3.rotateAroundYaxis(directionUnit, (e.getX()-prevX)/(2000/sensitivity));
            if (v_orientation > -maxAngle && (e.getY()-prevY)/(200/sensitivity) > 0)
                directionUnit = Vector3.rotateAroundYaxis(Vector3.rotateAroundXaxis(Vector3.rotateAroundYaxis(directionUnit, -h_orientation*0.017453292519943295), (e.getY()-prevY)/(2000/sensitivity)), h_orientation*0.017453292519943295);
            else if (v_orientation < -minAngle && (e.getY()-prevY)/(200/sensitivity) < 0)
                directionUnit = Vector3.rotateAroundYaxis(Vector3.rotateAroundXaxis(Vector3.rotateAroundYaxis(directionUnit, -h_orientation*0.017453292519943295), (e.getY()-prevY)/(2000/sensitivity)), h_orientation*0.017453292519943295);
            difference = Vector3.multiply(directionUnit, distance);
            updatePosition();
            lookAt(focusObj.getTransform().getPosition());
            v_orientation = Math.max(-89, Math.min(89, v_orientation));
            prevX = e.getX();
            prevY = e.getY();
        }

        public void mousePressed(MouseEvent e) 
        {
            prevX = e.getX();
            prevY = e.getY();
        }

        public void updatePosition()
        {
            position = Vector3.add(focusObj.getTransform().getPosition(), difference);
        }

        public void mouseMoved(MouseEvent e) {}
        public void mouseClicked(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
    }

    public void setOrbitControls(JPanel panel, GameObject focusObject, double startDistance, double sensitivity)
    {
        orbitController = new OrbitCamController(focusObject, startDistance, sensitivity);
        panel.addMouseListener(orbitController);
        panel.addMouseMotionListener(orbitController);
        panel.addMouseWheelListener(orbitController);
    }

    public void setFreeControls(JPanel panel, double movementSpeed, double sensitivity)
    {
        freeCamController = new FreeCamController(sensitivity, movementSpeed);
        panel.addKeyListener(freeCamController);
        panel.addMouseListener(freeCamController);
        panel.addMouseMotionListener(freeCamController);
    }

    public double getViewDistance()
    {
        return viewDistance;
    }

    public OrbitCamController getOrbitCamController()
    {
        return orbitController;
    }

    public FreeCamController getFreeCamController()
    {
        return freeCamController;
    }

    public void setFov(double fovIn)
    {
        fov = fovIn;
        renderPlaneWidth = calculateRenderPlaneWidth();
    }

    private void moveForward(double distanceIn)
    {
        position.add(Vector3.multiply(Vector3.angleToVector(h_orientation*0.017453292519943295, v_orientation*0.017453292519943295), distanceIn));
    }

    private void moveLeft(double distanceIn)
    {
        position.add(Vector3.multiply(Vector3.angleToVector(h_orientation*0.017453292519943295-Math.PI/2, 0), distanceIn));
    }

    private void moveUp(double distanceIn)
    {
        position.add(Vector3.multiply(Vector3.angleToVector(h_orientation*0.017453292519943295, v_orientation*0.017453292519943295+Math.PI/2), distanceIn));
    }

    public Vector3 getDirectionVector()
    {
        return Vector3.angleToVector(h_orientation*0.017453292519943295, v_orientation*0.017453292519943295);
    }

    public Plane getRenderPlane()
    {
        Vector3 directionVector = getDirectionVector();
        return new Plane(Vector3.add(Vector3.multiply(directionVector, renderPlaneDistance), position), directionVector);
    }  

    private double calculateRenderPlaneWidth()
    {
        return Math.tan(fov*0.017453292519943295/2)*renderPlaneDistance*2;
    }

    public double getHorientation()
    {
        return h_orientation;
    }

    public double getVorientation()
    {
        return v_orientation;
    }

    public Vector3 getPosition()
    {
        return position;
    }

    public double getRenderPlaneWidth()
    {
        return renderPlaneWidth;
    }
}

