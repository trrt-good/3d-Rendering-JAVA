import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Camera
{
    private double fov; //strictly reffers to the horizontal fov as vertical fov is based off screen height 
    private Vector3 position;
    private double h_orientation;
    private double v_orientation;
    private double renderPlaneDistance;
    private double viewDistance;

    private GameObject focusObject;

    private double renderPlaneWidth;

    private FreeCamController controller;

    public Camera(GameObject focus, double viewDistanceIn, double sensitivityIn, double movementSpeedIn, double fovIn)
    {
        renderPlaneDistance = 10;
        controller = new FreeCamController(sensitivityIn, movementSpeedIn);
        h_orientation = 0;
        v_orientation = 0;
        focusObject = focus;
        position = new Vector3(0, 10, -1000);
        viewDistance = viewDistanceIn;
        setFov(fovIn);
    }

    public void lookAt(Vector3 pos)
    {
        h_orientation = Math.toDegrees(Math.atan((pos.x-position.x)/(pos.z-position.z)));
        v_orientation = Math.toDegrees(Math.atan((pos.y-position.y)/(Math.sqrt((pos.x-position.x)*(pos.x-position.x) + (pos.z-position.z)*(pos.z-position.z)))));
        if (h_orientation < 0)
            h_orientation += 360;
        if (v_orientation < 0)
            h_orientation += 360;
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

        @Override
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

        @Override
        public void mouseMoved(MouseEvent e) {}

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
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

        @Override
        public void keyReleased(KeyEvent e) {}

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {
            prevX = e.getX();
            prevY = e.getY();
        }

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}
        
    }

    public FreeCamController getController()
    {
        return controller;
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

    public void setFocus(GameObject gameObject)
    {
        focusObject = gameObject;
    }

    public GameObject getFocusObj()
    {
        return focusObject;
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

