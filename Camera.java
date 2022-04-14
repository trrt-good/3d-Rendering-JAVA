import javax.swing.*;
import java.awt.event.*;
import java.awt.Point;
public class Camera
{
    public static final int ORBITCAM = 0;
    public static final int FREECAM = 1;
    public static final int FIXEDCAM = 2;

    private double fov; //strictly reffers to the horizontal fov as vertical fov is based off screen height 
    private Vector3 position;
    private double h_orientation;
    private double v_orientation;

    private double renderPlaneDistance;
    private double viewDistance;

    private GameObject focusObject;

    private double renderPlaneWidth;

    public Camera(int cameraType, double viewDistanceIn, double fovIn)
    {
        switch (cameraType) {
            case ORBITCAM:
                position = new Vector3();
                h_orientation = 0;
                v_orientation = 0;
                break;
            case FREECAM:

                h_orientation = 0;
                v_orientation = 0;
                break;
            case FIXEDCAM:
                h_orientation = 0;
                v_orientation = 0;
                break;
            
        }

        fov = fovIn;
        position = new Vector3(0, 0, 0);
        h_orientation = 0;
        v_orientation = 0;

        renderPlaneDistance = 10;
        viewDistance = viewDistanceIn;
        renderPlaneWidth = calculateRenderPlaneWidth();
    }

    class OrbitController implements MouseMotionListener, MouseListener, MouseWheelListener
    {
        private Point pressedPoint = new Point();
        private double camDistance;

        @Override
        public void mouseDragged(MouseEvent e)
        {
            if (focusObject != null)
            {

            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) 
        {
            pressedPoint = e.getPoint();
        }

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) 
        {

        }

    }

    public double getViewDistance()
    {
        return viewDistance;
    }

    public void setFov(double fovIn)
    {
        renderPlaneWidth = calculateRenderPlaneWidth();
        fov = fovIn;
    }

    public void setFocus(GameObject gameObject)
    {
        focusObject = gameObject;
    }

    private void moveForward(double distanceIn)
    {
        position.add(Vector3.multiply(Vector3.degAngleToVector(h_orientation, v_orientation), distanceIn));
    }

    private void moveLeft(double distanceIn)
    {
        position.add(Vector3.multiply(Vector3.degAngleToVector(h_orientation-90, 0), distanceIn));
    }

    private void moveUp(double distanceIn)
    {
        position.add(Vector3.multiply(Vector3.degAngleToVector(h_orientation, v_orientation+90), distanceIn));
    }

    public Vector3 getDirectionVector()
    {
        return Vector3.degAngleToVector(h_orientation, v_orientation);
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

