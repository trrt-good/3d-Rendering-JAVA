import javax.swing.*;
import java.awt.event.*;
public class Camera
{
    public static final int TICK_SPEED = 200;
    public static int movementSpeed = 50;
    public static int sensitivity = 70;

    public static double h_fov = 70;
    public static double v_fov = 40;
    public static Vector3 position = new Vector3(0, 10, 0);
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
                moveForward((double)movementSpeed/100);
            if (Main.inputManager.backward)
                moveForward(-(double)movementSpeed/100);

            if (Main.inputManager.left)
                moveLeft((double)movementSpeed/100);
            if (Main.inputManager.right)
                moveLeft(-(double)movementSpeed/100);

            if (Main.inputManager.upward)
                moveUp((double)movementSpeed/100);
            if (Main.inputManager.downward)
                moveUp(-(double)movementSpeed/100);
            
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
                h_orientation = clickedHorientation + (double)(Main.inputManager.mouseX-Main.inputManager.R_mouseClickedX)/(sensitivity-101);
                v_orientation = clickedVorientation + (double)(Main.inputManager.mouseY-Main.inputManager.L_mouseClickedY)/(sensitivity-101);
            }
            if (Main.inputManager.R_Down == false && first == false)
                first = true;
        }
    });

    private static void moveForward(double distanceIn)
    {
        position.add(Math.sin(Math.toRadians(90-h_orientation))*distanceIn, Math.sin(Math.toRadians(v_orientation))*distanceIn, Math.cos(Math.toRadians(90-h_orientation))*distanceIn);
    }

    private static void moveLeft(double distanceIn)
    {
        distanceIn*=-1;
        position.add(Math.cos(Math.toRadians(h_orientation-90))*distanceIn, 0, Math.sin(Math.toRadians(h_orientation-90))*distanceIn);
    }

    private static void moveUp(double distanceIn)
    {
        position.add(Math.sin(Math.toRadians(90-h_orientation))*distanceIn*(-1), Math.cos(Math.toRadians(90-v_orientation))*distanceIn, Math.sin(Math.toRadians(v_orientation))*distanceIn);
    }
}