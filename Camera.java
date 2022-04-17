import javax.management.MalformedObjectNameException;
import javax.net.ssl.HostnameVerifier;
import javax.swing.*;
import javax.swing.plaf.synth.SynthPasswordFieldUI;

import java.awt.event.*;
import java.awt.Point;
public class Camera
{
    private double fov; //strictly reffers to the horizontal fov as vertical fov is based off screen height 
    private Vector3 position;
    private double h_orientation;
    private double v_orientation;
    private double renderPlaneDistance;
    private double viewDistance;
    private double sensitivity;

    private GameObject focusObject;

    private double renderPlaneWidth;

    private CameraController controller;

    public Camera(GameObject focus, double viewDistanceIn, double sensitivityIn, double fovIn)
    {
        renderPlaneDistance = 10;
        controller = new CameraController();
        h_orientation = 0;
        v_orientation = 0;
        focusObject = focus;
        position = new Vector3(0, 10, -1000);
        viewDistance = viewDistanceIn;
        sensitivity = sensitivityIn;
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

    class CameraController implements MouseMotionListener, MouseListener, MouseWheelListener
    {
        private int prevX = 0;
        private int prevY = 0;
        private int deltaX = 0;
        private int deltaY = 0;
        private double camDistance;

        @Override
        public void mouseDragged(MouseEvent e)
        {
            deltaX = e.getX()-prevX; 
            deltaY = e.getY()-prevY;
            prevX = e.getX();
            prevY = e.getY();
            if (focusObject != null)
            {
                position = Vector3.add(Vector3.rotateAroundYaxis(Vector3.subtract(position, focusObject.getPosition()), ((deltaX)/10000.0)*sensitivity), focusObject.getPosition()); 
                lookAt(focusObject.getPosition());
                System.out.println(h_orientation + "  " + v_orientation);
            }
            else
            {
                System.out.println("Camera must have a focus object");
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) 
        {
            prevX = e.getX();
            prevY = e.getY();
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
            if (e.getScrollAmount() > 0)
            {
                camDistance += e.getScrollAmount();
            }
        }
    }

    public CameraController getController()
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
        position.add(Vector3.multiply(Vector3.angleToVector(h_orientation*0.017453292519943295-Math.PI/4, 0), distanceIn));
    }

    private void moveUp(double distanceIn)
    {
        position.add(Vector3.multiply(Vector3.angleToVector(h_orientation*0.017453292519943295, v_orientation*0.017453292519943295+Math.PI/4), distanceIn));
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

