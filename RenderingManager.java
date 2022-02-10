import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class RenderingManager 
{
    public static Panel panel;
    public static JFrame window;

    public static int defaultWidth = 1600;
    public static int defaultHeight = 900;

    public static int FPS = 200;

    public static void startRendering(String name)
    {
        window = new JFrame(name);
        panel = new Panel();
        window.setSize(defaultWidth, defaultHeight);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().add(panel);
        window.addKeyListener(Main.camera);
        window.setVisible(true);
    }

    private static void drawTriangles(Graphics g)
    {
        for (int i = 0; i < Main.ObjectManager.triangles.size(); i ++)
        {
            Triangle tempTriangle = Main.ObjectManager.triangles.get(i);
            Point p1ScreenCoords = translateToScreenCoords(tempTriangle.point1);
            Point p2ScreenCoords = translateToScreenCoords(tempTriangle.point2);
            Point p3ScreenCoords = translateToScreenCoords(tempTriangle.point3);

            Polygon screenTriangle = new Polygon(new int[]{p1ScreenCoords.x, p2ScreenCoords.x, p3ScreenCoords.x}, new int[]{p1ScreenCoords.y, p2ScreenCoords.y, p3ScreenCoords.y}, 3);

            g.setColor(tempTriangle.color);
            
            if (tempTriangle.fill)
                g.fillPolygon(screenTriangle);
            else
                g.drawPolygon(screenTriangle);
        }
    }

    private static Point translateToScreenCoords(Vector3 worldCoords)
    {
        Point screenCoord = new Point();

        //calculate the x screen coordinate
        double h_pointDistance = Math.sqrt(Math.pow(worldCoords.x - Main.camera.position.x, 2) + Math.pow(worldCoords.z - Main.camera.position.z, 2));
        double h_pointAngle = Math.atan((worldCoords.z - Main.camera.position.z)/(worldCoords.x - Main.camera.position.x))-Main.camera.h_orientation;
        screenCoord.x = (int)(window.getWidth()*((Math.tan(Math.toRadians(Main.camera.h_fov/2))*h_pointDistance*Math.cos(h_pointAngle)-h_pointDistance*Math.sin(h_pointAngle))/(2*Math.tan(Math.toRadians(Main.camera.h_fov/2))*h_pointDistance*Math.cos(h_pointAngle))));

        //calculate the y screen coordinate
        double v_pointDistance = Math.sqrt(h_pointDistance*h_pointDistance + (worldCoords.y-Main.camera.position.y)*(worldCoords.y-Main.camera.position.y));
        double v_pointAngle = Math.atan((worldCoords.y - Main.camera.position.y)/(h_pointDistance))-Main.camera.v_orientation;
        screenCoord.y = (int)(window.getHeight()*((Math.tan(Math.toRadians(Main.camera.v_fov/2))*v_pointDistance*Math.cos(v_pointAngle)-v_pointDistance*Math.sin(v_pointAngle))/(2*Math.tan(Math.toRadians(Main.camera.v_fov/2))*v_pointDistance*Math.cos(v_pointAngle))));
        return screenCoord;
    }

    private static class Panel extends JPanel implements ActionListener
    {
        Timer timer;
        public Panel()
        {
            setBackground(Color.WHITE);
            timer = new Timer(1000/FPS + 1, this);
            timer.start();
        }

        public void paint(Graphics g)
        {
            super.paint(g);
            drawTriangles(g);
            g.setColor(Color.BLACK);
        }

        @Override
        public void actionPerformed(ActionEvent e) 
        {
            repaint();
        }
    }
}


