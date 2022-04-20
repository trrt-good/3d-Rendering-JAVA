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

        private int maxAngle = 80;
        private int minAngle = -80;

        private GameObject focusObj;
        private double startDistance;
        private double sensitivity;

        private int prevX = 0;
        private int prevY = 0;
        
        public OrbitCamController(GameObject focusObjectIn, double startDistanceIn, double sensitivityIn)
        {
            focusObj = focusObjectIn;
            startDistance = startDistanceIn;
            sensitivity = sensitivityIn;

            position = Vector3.add(focusObj.getPosition(), new Vector3(0, 0, -startDistance));
        }

        public void mouseWheelMoved(MouseWheelEvent e) 
        {
            if (Vector3.subtract(position, focusObj.getPosition()).getSqrMagnitude() > minDistance*minDistance && e.getWheelRotation()<0)
                moveForward(-e.getWheelRotation()*30);
            else if (Vector3.subtract(position, focusObj.getPosition()).getSqrMagnitude() < maxDistance*maxDistance && e.getWheelRotation()>0)
                moveForward(-e.getWheelRotation()*30);
        }

        public void mouseDragged(MouseEvent e) 
        {
            position = Vector3.add(Vector3.rotateAroundYaxis(Vector3.subtract(position, focusObj.getPosition()), (e.getX()-prevX)/(2000/sensitivity)) , focusObj.getPosition());
            if (v_orientation > -maxAngle && (e.getY()-prevY)/(200/sensitivity) > 0)
                position = Vector3.add(Vector3.rotateAroundYaxis(Vector3.rotateAroundXaxis(Vector3.rotateAroundYaxis(Vector3.subtract(position, focusObj.getPosition()), -h_orientation*0.017453292519943295), (e.getY()-prevY)/(2000/sensitivity)), h_orientation*0.017453292519943295) , focusObj.getPosition());
            else if (v_orientation < -minAngle && (e.getY()-prevY)/(200/sensitivity) < 0)
                position = Vector3.add(Vector3.rotateAroundYaxis(Vector3.rotateAroundXaxis(Vector3.rotateAroundYaxis(Vector3.subtract(position, focusObj.getPosition()), -h_orientation*0.017453292519943295), (e.getY()-prevY)/(2000/sensitivity)), h_orientation*0.017453292519943295) , focusObj.getPosition());

            lookAt(focusObj.getPosition());
            v_orientation = Math.max(-89, Math.min(89, v_orientation));
            prevX = e.getX();
            prevY = e.getY();
        }

        public void mousePressed(MouseEvent e) 
        {
            prevX = e.getX();
            prevY = e.getY();
        }

        public void mouseMoved(MouseEvent e) {}
        public void mouseClicked(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
    }

    public void setOrbitControls(JPanel panel, GameObject focusObject, double startDistance, double sensitivity)
    {
        OrbitCamController controller = new OrbitCamController(focusObject, startDistance, sensitivity);
        panel.addMouseListener(controller);
        panel.addMouseMotionListener(controller);
        panel.addMouseWheelListener(controller);
    }

    public void setFreeControls(JPanel panel, double movementSpeed, double sensitivity)
    {
        FreeCamController controller = new FreeCamController(sensitivity, movementSpeed);
        panel.addKeyListener(controller);
        panel.addMouseListener(controller);
        panel.addMouseMotionListener(controller);
    }

    public double getViewDistance()
    {
        return viewDistance;
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

