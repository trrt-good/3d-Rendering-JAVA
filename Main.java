import java.util.List;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;
public class Main 
{
    public static int tps = 100;
    public static Camera camera = new Camera();
    // Timer timer = new Timer(1000/100 + 1, new ActionListener() 
    // {
    //     @Override
    //     public void actionPerformed(ActionEvent e) 
    //     {
            
    //     }
    // });

    public static void main(String [] args)
    {
        new Triangle(new Vector3(100, 0, 15), new Vector3(130, 0, -20), new Vector3(100, 30, 0), Color.YELLOW, true);
        new Triangle(new Vector3(100, 0, 15), new Vector3(100, 0, -15), new Vector3(100, 30, 0), Color.BLUE, true);
        new Triangle(new Vector3(100, 0, 50), new Vector3(100, 0, 30), new Vector3(100, 30, 40), Color.MAGENTA, false);
        
        RenderingManager.startRendering("Test");
    }

    public static class ObjectManager
    {
        public static List<Triangle> triangles = new ArrayList<Triangle>();
    }
}
