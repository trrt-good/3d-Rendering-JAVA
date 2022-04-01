import javax.swing.JFrame;
import javax.swing.JPanel;
public class GraphicsManager 
{
    public static JPanel renderingPanel;
    public static JFrame mainFrame;

    public static int defaultWidth = 1600;
    public static int defaultHeight = 900;

    public static final int FPS = 1000;

    public static void startGraphics(String name)
    {
        mainFrame = new JFrame(name);

        mainFrame.setSize(defaultWidth, defaultHeight);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);

        renderingPanel = new RenderingPanel(FPS);
        mainFrame.getContentPane().add(renderingPanel);
        renderingPanel.addKeyListener(Main.inputManager);
        renderingPanel.addMouseListener(Main.inputManager);
        renderingPanel.addMouseMotionListener(Main.inputManager);
        renderingPanel.setVisible(true);
    }
}
