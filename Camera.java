import javax.swing.*;
import java.awt.event.*;
public class Camera
{
    public static Camera mainCamera;

    public final int TICK_SPEED = 500;
    public int movementSpeed;
    public int sensitivity;

    public double fov; //strictly reffers to the horizontal fov as vertical fov is based off screen height 
    public Vector3 position;
    public double h_orientation;
    public double v_orientation;

    public double nearClippingPlaneDistance; 
    public double renderPlaneDistance;
    public double farClippingPlaneDistance;

    public double renderPlaneWidth;

    public Camera()
    {
        System.out.print("\tcamera... ");
        long start = System.nanoTime();
        movementSpeed = 1000;
        sensitivity = 15;

        fov = 60; //strictly reffers to the horizontal fov as vertical fov is based off screen height 
        position = new Vector3(0, 0, -100);
        h_orientation = 0;
        v_orientation = 0;

        nearClippingPlaneDistance = 5; 
        renderPlaneDistance = 10;
        farClippingPlaneDistance = 1000;
        renderPlaneWidth = getRenderPlaneWidth();
        System.out.println("finished in " + (System.nanoTime()-start)/1000000.0 + "ms");
    }

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

    private double getRenderPlaneWidth()
    {
        return Math.tan(fov*0.017453292519943295/2)*renderPlaneDistance*2;
    }
}