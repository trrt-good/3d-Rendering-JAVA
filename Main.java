import java.util.List;
import java.util.ArrayList;
import java.awt.Color;
public class Main 
{
    public static int tps = 100;
    public static InputManager inputManager = new InputManager();

    public static void main(String [] args)
    {
        new Triangle(new Vector3(-10, 0, 100), new Vector3(10, 0, 100), new Vector3(0, 20, 100), Color.YELLOW, true);
        new Triangle(new Vector3(-30, 0, 100), new Vector3(-10, 0, 100), new Vector3(-20, 20, 100), Color.BLUE, true);
        
        Camera.timer.start();
        RenderingManager.startRendering("Test");
    }

    public static class ObjectManager
    {
        public static List<Triangle> triangles = new ArrayList<Triangle>();
    }
}
