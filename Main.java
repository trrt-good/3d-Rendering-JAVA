import java.util.List;
import java.util.ArrayList;
import java.awt.Color;
public class Main 
{
    public static int tps = 100;
    public static InputManager inputManager = new InputManager();

    public static void main(String [] args)
    {
        new Triangle(new Vector3(15, 0, 100), new Vector3(0, 0, 130), new Vector3(0, 30, 100), new Color(100, 100, 200), true);
        new Triangle(new Vector3(15, 0, 100), new Vector3(-15, 0, 100), new Vector3(0, 30, 100), new Color(150, 150, 255), true);
        
        new Triangle(new Vector3(50, 0, 100), new Vector3(30, 0, 100), new Vector3(40, 17.32051, 100), Color.MAGENTA);
        
        Camera.mainCamera.timer.start();
        GraphicsManager.startGraphics("3d");

        System.out.println(Vector3.rotateAroundYaxis(new Vector3(43, 12, 54), 3));
    }

    public static class ObjectManager
    {
        public static List<Triangle> triangles = new ArrayList<Triangle>();
    }
}
