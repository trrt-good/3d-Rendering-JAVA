package src;
import javax.swing.JFrame;

import src.gameObject.*;
import src.graphics.*;
import src.primitives.*;
import src.testing.TimingHelper;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Color;
public class GraphicsManager 
{
    public static RenderingPanel renderingPanel;
    public static JFrame mainFrame;

    public static int defaultWidth = 1600;
    public static int defaultHeight = 900;

    public static GameObject car;
    public static GameObject jet;
    public static GameObject sphere;
    public static GameObject cat;
    public static GameObject triangleTest; 

    public static void startGraphics(String name)
    {
        cat = new GameObject
        (
            "cat", 
            new Mesh("cat.obj", new Color(100, 100, 100), new Vector3(0, -100, 0), Quaternion.toQuaternion(0, 0, 0), 1, true, true), 
            Vector3.zero()
        );

        // triangleTest = new GameObject
        // (
        //     "test",
        //     new Mesh("testing.obj", new Color(100, 100, 100), new Vector3(0, -50, 0), new EulerAngle(0, 0, 0), 10, true, true), 
        //     new Transform(Vector3.ZERO)
        // );

        // car = new GameObject
        // (
        //     "car", 
        //     new Mesh("car.obj", "carTexture.png", new Vector3(0, 0, 0), Quaternion.toQuaternion(0, 0, 0), 10, true, true), 
        //     Vector3.zero()
        // );

        // jet = new GameObject
        // (
        //     "High Poly Jet",
        //     new Mesh("jet.obj", new Color(100, 100, 100), Vector3.zero(), Quaternion.toQuaternion(0, 0, 0), 100, true, true), 
        //     Vector3.zero()
        // );

        sphere = new GameObject
        (
            "shere", 
            new Mesh("sphere.obj", new Color(100, 100, 100), new Vector3(0, 0, 0), Quaternion.toQuaternion(0, 0, 0), 20, true, true), 
            Vector3.zero()
        );

        System.out.println("Creating graphics... ");
        long start = System.nanoTime();

        mainFrame = new JFrame(name);
        mainFrame.setSize(defaultWidth, defaultHeight);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
        mainFrame.addWindowListener(new WindowCloseListener());

        renderingPanel = new RenderingPanel(1600, 900);
        mainFrame.getContentPane().add(renderingPanel);
        renderingPanel.setVisible(true);
        //renderingPanel.setFPSlimit(150);

        Camera cam = new Camera(new Vector3(0, 0, 0), 10000, 100, 60);
        renderingPanel.setCamera(cam);
        renderingPanel.addGameObject(sphere);
        cam.setOrbitControls(renderingPanel, sphere, 1000, 10);
        //cam.setFreeControls(renderingPanel, 200, 10);
        renderingPanel.setLighting(new Lighting(new Vector3(0, -1, 0), 70, 30));
        renderingPanel.setFog(1000, 3000, 190, 210, 245);
        //renderingPanel.setFPSlimit(150);
        renderingPanel.start();
        System.out.println("finished creating graphics in " + (System.nanoTime()-start)/1000000 + "ms");
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
