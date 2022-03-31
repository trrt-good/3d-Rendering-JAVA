import javax.swing.*;
import java.awt.event.*;
public class Camera
{
    public static Camera mainCamera = new Camera();

    public final int TICK_SPEED = 500;
    public int movementSpeed = 200;
    public int sensitivity = 15;

    public double nearClippingPlaneDistance = 5; //TODO: implement these
    public double renderPlaneDistance = 10;
    public double farClippingPlaneDistance = 1000;

    public double fov = 60; //strictly reffers to the horizontal fov as vertical fov is based off screen height 
    public Vector3 position = new Vector3(0, 0, 0);
    public double h_orientation = 0;
    public double v_orientation = 0;

    public Timer timer = new Timer(1000/TICK_SPEED + 1, new ActionListener()
    {
        boolean first = true;
        double clickedHorientation = 0;
        double clickedVorientation = 0;

        @Override
        public void actionPerformed(ActionEvent e) 
        {
            if (Main.inputManager.forward)
                moveForward(movementSpeed/100.0);
            if (Main.inputManager.backward)
                moveForward(-movementSpeed/100.0);

            if (Main.inputManager.left)
                moveLeft(movementSpeed/100.0);
            if (Main.inputManager.right)
                moveLeft(-movementSpeed/100.0);

            if (Main.inputManager.upward)
                moveUp(movementSpeed/100.0);
            if (Main.inputManager.downward)
                moveUp(-movementSpeed/100.0);
            
            if (Main.inputManager.R_Down && first)
            {
                first = false;
                clickedHorientation = h_orientation;
                clickedVorientation = v_orientation;
            }
            if (Main.inputManager.R_Down)
            {
                if (sensitivity > 100)  
                    sensitivity = 100;
                if (sensitivity < 1)
                    sensitivity = 1;
                h_orientation = clickedHorientation + (double)(Main.inputManager.mouseX-Main.inputManager.R_mouseClickedX)/(200.0/sensitivity);
                v_orientation = clickedVorientation + (double)(Main.inputManager.mouseY-Main.inputManager.R_mouseClickedY)/(-200.0/sensitivity);
                h_orientation%=360;
                if (v_orientation >= 90)
                    v_orientation = 89;
                if (v_orientation <= -90)
                    v_orientation = -89;
                v_orientation%=360;
            }
            if (Main.inputManager.R_Down == false && first == false)
                first = true;
        }
    });

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

    public Plane getNearClippingPlane()
    {
        Vector3 directionVector = getDirectionVector();
        return new Plane(Vector3.add(Vector3.multiply(directionVector, nearClippingPlaneDistance), position), directionVector);
    }    

    public Plane getFarClippingPlane()
    {
        Vector3 directionVector = getDirectionVector();
        return new Plane(Vector3.add(Vector3.multiply(directionVector, farClippingPlaneDistance), position), directionVector);
    }    
}