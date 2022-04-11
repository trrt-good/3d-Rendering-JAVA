import java.io.File;
public class Main 
{
    public static File resourcesDirectory = new File("res");
    public static InputManager inputManager = new InputManager();
    public static boolean debugMode = true;

    public static void main(String [] args)
    {    
        GraphicsManager.startGraphics("3d");
    }
}
