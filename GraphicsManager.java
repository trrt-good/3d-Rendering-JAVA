import javax.swing.JFrame;
public class GraphicsManager 
{
    public static RenderingPanel renderingPanel;
    public static JFrame mainFrame;

    public static int defaultWidth = 1600;
    public static int defaultHeight = 900;

    public static void startGraphics(String name)
    {
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
        renderingPanel.mainLight.updateAllLighting();
        System.out.println("finished creating graphics in " + (System.nanoTime()-start)/1000000 + "ms");
    }
}
