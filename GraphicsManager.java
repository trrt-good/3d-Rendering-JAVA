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

    public static void startGraphics(String name)
    {
        gameObject1 = new GameObject(new Vector3(0, 0, 0), new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)), 1, "cat.obj", new Color(50, 50, 50));
        gameObject1.shading = true;
        // gameObject2 = new GameObject(new Vector3(0, 0, 0), "cat.obj", new Color(200, 200, 200), new EulerAngle(0, Math.toRadians(0), Math.toRadians(0)), 1, true);
        // gameObject2.shading = true;
        System.out.println("Creating graphics... ");
        long start = System.nanoTime();

        mainFrame = new JFrame(name);
        //mainFrame.addWindowListener(Main.inputManager);
        mainFrame.setSize(defaultWidth, defaultHeight);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
        mainFrame.addWindowListener(new WindowCloseListener());

        renderingPanel = new RenderingPanel(1600, 900);
        mainFrame.getContentPane().add(renderingPanel);
        renderingPanel.setVisible(true);

        renderingPanel.setCamera(new Camera(gameObject1, 10000, 5, 50, 60));
        renderingPanel.setLighting(new Lighting(new Vector3(1, -1, 1), 70, 50));
        renderingPanel.addGameObject(gameObject1);
        //renderingPanel.setFog(1000, 3000, new Color(190, 210, 245));
        renderingPanel.startRenderUpdates();
        // renderingPanel.addGameObject(gameObject2);
        
        System.out.println("finished creating graphics in " + (System.nanoTime()-start)/1000000 + "ms");
    }

    
}

class WindowCloseListener implements WindowListener
    {

        @Override
        public void windowOpened(WindowEvent e) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void windowClosing(WindowEvent e) {
            TimingHelper.printSummary();
        }

        @Override
        public void windowClosed(WindowEvent e) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void windowIconified(WindowEvent e) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void windowActivated(WindowEvent e) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            // TODO Auto-generated method stub
            
        }

    }
