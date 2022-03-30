import java.util.List;
import java.util.ArrayList;
import java.awt.Color;
public class Main 
{
    public static int tps = 100;
    public static InputManager inputManager = new InputManager();

    public static void main(String [] args)
    {
        new Triangle(new Vector3(15, 0, 100), new Vector3(-5, 0, 130), new Vector3(0, 30, 100), Color.YELLOW, true);
        new Triangle(new Vector3(15, 0, 100), new Vector3(-15, 0, 100), new Vector3(0, 30, 100), Color.BLUE, true);
        new Triangle(new Vector3(50, 0, 100), new Vector3(30, 0, 100), new Vector3(40, 17.32051, 100), Color.MAGENTA, 10);
        
        Camera.timer.start();
        GraphicsManager.startGraphics("3d");
    }

    public static class ObjectManager
    {
        public static List<Triangle> triangles = new ArrayList<Triangle>();
    }
}
