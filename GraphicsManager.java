import javax.swing.JFrame;

import java.awt.Color;
public class GraphicsManager 
{
    public static RenderingPanel renderingPanel;
    public static JFrame mainFrame;

    public static int defaultWidth = 1920;
    public static int defaultHeight = 1080;

    public static GameObject gameObject1;
    public static GameObject gameObject2;

    public static void startGraphics(String name)
    {
        gameObject1 = new GameObject(new Vector3(0, 0, 0), "cat.obj", new Color(50, 50, 50), new EulerAngle(0, Math.toRadians(0), Math.toRadians(0)), 1, false);
        gameObject1.shading = true;
        // gameObject2 = new GameObject(new Vector3(0, 0, 0), "cat.obj", new Color(200, 200, 200), new EulerAngle(0, Math.toRadians(0), Math.toRadians(0)), 1, true);
        // gameObject2.shading = true;
        System.out.println("Creating graphics... ");
        long start = System.nanoTime();
        mainFrame = new JFrame(name);

        mainFrame.setSize(defaultWidth, defaultHeight);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);

        renderingPanel = new RenderingPanel(false);
        mainFrame.getContentPane().add(renderingPanel);
        renderingPanel.addKeyListener(Main.inputManager);
        renderingPanel.addMouseListener(Main.inputManager);
        renderingPanel.addMouseMotionListener(Main.inputManager);
        renderingPanel.setVisible(true);
        
        renderingPanel.setCamera(new Camera());
        renderingPanel.setLighting(new Lighting(new Vector3(1, -1, 1), 70, 50));
        renderingPanel.addGameObject(gameObject1);
        renderingPanel.setFog(1000, 3000, new Color(190, 210, 245));
        // renderingPanel.addGameObject(gameObject2);
        
        System.out.println("finished creating graphics in " + (System.nanoTime()-start)/1000000 + "ms");
    }
}
