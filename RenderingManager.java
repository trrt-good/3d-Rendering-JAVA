import java.awt.Graphics;
import java.awt.Color;
import javax.swing.*;
public class RenderingManager 
{
    public static Panel panel;
    public static JFrame window;

    public static int defaultWidth = 1600;
    public static int defaultHeight = 900;

    public static void createWindowAndPanel(String name)
    {
        window = new JFrame(name);
        panel = new Panel();
        window.setSize(defaultWidth, defaultHeight);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().add(panel);

        window.setVisible(true);
    }

    

    private static class Panel extends JPanel
    {
        public Panel()
        {
            setBackground(Color.WHITE);
        }

        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
        }
    }
}


