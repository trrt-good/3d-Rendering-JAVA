import java.util.List;
import java.util.ArrayList;
import java.awt.Color;
import java.io.File;
public class Main 
{
    public static File resourcesDirectory = new File("res");
    public static InputManager inputManager = new InputManager();
    public static GameObject gameObject;

    public static void main(String [] args)
    {
        gameObject = new GameObject(new Vector3(0, 0, 0), "cat.obj", new Color(0, 0, 0), new EulerAngle(0, Math.toRadians(0), Math.toRadians(0)), 1, false);
        gameObject.shading = true;
        GraphicsManager.startGraphics("3d");
        

        // Triangle t1 = new Triangle(null, new Vector3(-20, 0, 100), new Vector3(20, 0, 100), new Vector3(0, 30, 100), new Color(150, 150, 150));
        // Triangle t2 = new Triangle(null, new Vector3(-50, 0, 100), new Vector3(-10, 0, 100), new Vector3(-30, 30, 110), new Color(150, 150, 150));
        // Triangle t3 = new Triangle(null, new Vector3(10, 0, 100), new Vector3(50, 0, 100), new Vector3(30, 30, 90), new Color(150, 150, 150));
        // t1.calculateLightingColor(GraphicsManager.renderingPanel.mainLight);
        // t2.calculateLightingColor(GraphicsManager.renderingPanel.mainLight);
        // t3.calculateLightingColor(GraphicsManager.renderingPanel.mainLight);
    }

    public static class ObjectManager
    {
        public static List<Triangle> triangles = new ArrayList<Triangle>();
        public static List<GameObject> gameObjects = new ArrayList<GameObject>();
    }
}
