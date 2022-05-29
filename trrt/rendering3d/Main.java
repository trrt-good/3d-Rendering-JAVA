package trrt.rendering3d;
import java.io.File;
import trrt.rendering3d.gameObject.*;
import trrt.rendering3d.graphics.*;
import trrt.rendering3d.primitives.*;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
public class Main 
{
    public static final File GAMEOBJECT_DIRECTORY = new File("trrt\\rendering3d\\res", "gameObjectFiles");

    private static RenderingPanel renderingPanel;
    private static JFrame mainFrame;

    private final static int DEFAULT_WIDTH = 1600;
    private final static int DEFAULT_HEIGHT = 900;

    private static GameObject testGameObject;

    private static Camera cam;

    public static void main(String [] args)
    {   
        startGraphics();

        if (args.length > 0)
        {
            double scale = 1.0;
            File imageFile = null;
            if (args.length > 1)
            {
                try
                {
                    scale = Double.parseDouble(args[1]);
                } 
                catch (NumberFormatException e)
                {
                    imageFile = new File(args[1]);
                    if (args.length > 2)
                    {
                        try
                        {
                            scale = Double.parseDouble(args[2]);
                        } 
                        catch (NumberFormatException e1)
                        { 
                            System.out.println("use the last parameter to specify scale!");
                        }
                    }
                }
            }

            if (imageFile == null)
                testGameObject = new GameObject
                (
                    args[0].substring(0, args[0].length()-4), 
                    new Mesh(new File(args[0]), Color.GRAY, null, Quaternion.IDENTITY, scale, true), 
                    Vector3.ZERO
                );
            else
                testGameObject = new GameObject
                (
                    args[0].substring(0, args[0].length()-4), 
                    new Mesh(new File(args[0]), imageFile, null, Quaternion.IDENTITY, scale, true), 
                    Vector3.ZERO
                );
        }
        else
        {
            testGameObject = new GameObject
            (
                "testObj", 
                new DemoCube(), 
                Vector3.ZERO
            );
        }

        renderingPanel.addGameObject(testGameObject);
        cam.setOrbitControls(renderingPanel, testGameObject, 10, 30);
    }

    public static void startGraphics()
    {
        mainFrame = new JFrame("Main");
        mainFrame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
        mainFrame.addWindowListener(new WindowCloseListener());

        renderingPanel = new RenderingPanel(1600, 900, Color.BLACK);
        mainFrame.getContentPane().add(renderingPanel);
        renderingPanel.setVisible(true);

        cam = new Camera(Vector3.ZERO, 3000, 100, 60);
        renderingPanel.setCamera(cam);
        renderingPanel.setLighting(new Lighting(new Vector3(0.3, -1, 0.5), 70, 60));
        renderingPanel.start();
    }
}

class WindowCloseListener implements WindowListener
{
    @Override
    public void windowClosing(WindowEvent e) 
    {
        RenderingPanel.printPreformanceSummary();
    }
    
    @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
}

class DemoCube extends Mesh
{
    public DemoCube()
    {
        super(true);

        Vector3 v1 = new Vector3(-50, -51, -50);
        Vector3 v2 = new Vector3(50, -50, -50);
        Vector3 v3 = new Vector3(50, -50, 50);
        Vector3 v4 = new Vector3(-50, -50, 50);

        Vector3 v5 = new Vector3(-50, 50, -50);
        Vector3 v6 = new Vector3(50, 50, -50);
        Vector3 v7 = new Vector3(50, 50, 50);
        Vector3 v8 = new Vector3(-50, 50, 50);

        getTriangles().add(new Triangle(this, v3, v1, v2, Color.LIGHT_GRAY));
        getTriangles().add(new Triangle(this, v4, v1, v3, Color.LIGHT_GRAY));

        getTriangles().add(new Triangle(this, v5, v7, v6, Color.LIGHT_GRAY));
        getTriangles().add(new Triangle(this, v5, v8, v7, Color.LIGHT_GRAY));

        getTriangles().add(new Triangle(this, v1, v6, v2, Color.LIGHT_GRAY));
        getTriangles().add(new Triangle(this, v1, v5, v6, Color.LIGHT_GRAY));

        getTriangles().add(new Triangle(this, v2, v7, v3, Color.LIGHT_GRAY));
        getTriangles().add(new Triangle(this, v2, v6, v7, Color.LIGHT_GRAY));

        getTriangles().add(new Triangle(this, v3, v8, v4, Color.LIGHT_GRAY));
        getTriangles().add(new Triangle(this, v3, v7, v8, Color.LIGHT_GRAY));

        getTriangles().add(new Triangle(this, v4, v5, v1, Color.LIGHT_GRAY));
        getTriangles().add(new Triangle(this, v4, v8, v5, Color.LIGHT_GRAY));
    }
}