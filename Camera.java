import javax.swing.*;
import java.awt.event.*;
public class Camera
{
    public static final int TICK_SPEED = 500;
    public static int movementSpeed = 200;
    public static int sensitivity = 15;

    public static double clippingDistance = 5; //TODO: implement this

    public static double fov = 90; //strictly reffers to the horizontal fov as vertical fov is based off screen height 
    public static Vector3 position = new Vector3(0, 0, 0);
    public static double h_orientation = 0;
    public static double v_orientation = 0;

    public static Timer timer = new Timer(1000/TICK_SPEED + 1, new ActionListener()
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

    private static void moveForward(double distanceIn)
    {
        position.add(Vector3.multiply(Vector3.degAngleToVector(h_orientation, v_orientation), distanceIn));
    }

    private static void moveLeft(double distanceIn)
    {
        position.add(Vector3.multiply(Vector3.degAngleToVector(h_orientation-90, 0), distanceIn));
    }

    private static void moveUp(double distanceIn)
    {
        position.add(Vector3.multiply(Vector3.degAngleToVector(h_orientation, v_orientation+90), distanceIn));
    }

    public static Vector3 getDirectionVector()
    {
        return Vector3.degAngleToVector(h_orientation, v_orientation);
    }
}