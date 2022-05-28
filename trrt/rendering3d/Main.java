package trrt.rendering3d;
import java.io.File;
import trrt.rendering3d.gameObject.*;
import trrt.rendering3d.graphics.*;
import trrt.rendering3d.primitives.*;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import trrt.testing.*;
public class Main 
{
    public static final File RES = new File("res");
    public static final File GAMEOBJECT_DIRECTORY = new File(RES, "gameObjectFiles");

    private static RenderingPanel renderingPanel;
    private static JFrame mainFrame;

    private static int defaultWidth = 1600;
    private static int defaultHeight = 900;

    private static GameObject testGameObject;

    public static void main(String [] args)
    {   
        startGraphics();
    }

    public static void startGraphics()
    {
        testGameObject = new GameObject
        (
            "suzanne", 
            new Mesh("suzanne.obj", new Color(100, 100, 100), new Vector3(0, 0, 0), Quaternion.toQuaternion(0, 0, 0), 100, true), 
            Vector3.ZERO
        );

        mainFrame = new JFrame("Main");
        mainFrame.setSize(defaultWidth, defaultHeight);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
        mainFrame.addWindowListener(new WindowCloseListener());

        renderingPanel = new RenderingPanel(1600, 900, Color.BLACK);
        mainFrame.getContentPane().add(renderingPanel);
        renderingPanel.setVisible(true);

        Camera cam = new Camera(new Vector3(0, 0, 0), 3000, 100, 60);
        renderingPanel.setCamera(cam);
        renderingPanel.addGameObject(testGameObject);
        cam.setOrbitControls(renderingPanel, testGameObject, 10, 30);
        renderingPanel.setLighting(new Lighting(new Vector3(0, -1, 0), 70, 30));
        renderingPanel.setFog(1000, 3000, new Color(190, 210, 245));
        renderingPanel.start();
    }
}

class WindowCloseListener implements WindowListener
{
    @Override
    public void windowClosing(WindowEvent e) 
    {
        TimingHelper.printSummary();
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