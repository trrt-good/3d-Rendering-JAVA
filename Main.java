import java.util.List;
import java.util.ArrayList;
import java.awt.Color;
import java.io.File;
public class Main 
{
    public static File resourcesDirectory = new File("res");
    public static InputManager inputManager = new InputManager();

    public static GameObject gameObject = new GameObject("cat.obj", new EulerAngle(0, 0, 0), false);

    public static void main(String [] args)
    {
        Camera.mainCamera = new Camera();
        Camera.mainCamera.timer.start();
        GraphicsManager.startGraphics("3d");
    }

    public static class ObjectManager
    {
        public static List<Triangle> triangles = new ArrayList<Triangle>();
    }
}
