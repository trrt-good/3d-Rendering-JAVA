import javax.swing.JFrame;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Color;
public class GraphicsManager 
{
    public static RenderingPanel renderingPanel;
    public static JFrame mainFrame;

    public static int defaultWidth = 1600;
    public static int defaultHeight = 900;

    public static GameObject gameObject1;
    public static GameObject gameObject2;
    public static GameObject gameObject3;

    public static void startGraphics(String name)
    {
        gameObject1 = new GameObject
        (
            "plane", 
            new Mesh("planeBody.obj", new Vector3(0, 0, 0), new EulerAngle(0, Math.toRadians(0), Math.toRadians(0)), 1, new Color(100, 100, 100), true, true), 
            new Transform(new Vector3())
        );

        gameObject2 = new GameObject
        (
            "High Poly Jet",
            new Mesh("jet.obj", new Vector3(), new EulerAngle(0, 0, 0), 100, new Color(100, 100, 100), true, true), 
            new Transform(new Vector3())
        );

        gameObject3 = new GameObject
        (
            "lowPolyGulfstream", 
            new Mesh("lowPolyGulfstream.obj", new Vector3(0, 0, 0), new EulerAngle(0, Math.toRadians(0), Math.toRadians(0)), 1, new Color(100, 100, 100), true, true), 
            new Transform(new Vector3())
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

        Camera cam = new Camera(new Vector3(0, 0, 0), 10000, 60);
        renderingPanel.setCamera(cam);
        //cam.setFreeControls(renderingPanel, 200, 10);
        cam.setOrbitControls(renderingPanel, gameObject2, 1000, 10);
        renderingPanel.setLighting(new Lighting(new Vector3(-0.5, -1, -0.5), 70, 30));
        renderingPanel.addGameObject(gameObject2);
        renderingPanel.setFog(1000, 3000, new Color(190, 210, 245));
        renderingPanel.startRenderUpdates();
        
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
