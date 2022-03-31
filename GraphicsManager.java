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
        renderingPanel = new RenderingPanel(FPS);
        mainFrame.setSize(defaultWidth, defaultHeight);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.getContentPane().add(renderingPanel);
        mainFrame.addKeyListener(Main.inputManager);
        mainFrame.addMouseListener(Main.inputManager);
        mainFrame.addMouseMotionListener(Main.inputManager);
        mainFrame.setVisible(true);
    }
}
