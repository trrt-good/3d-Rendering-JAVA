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
        window.addKeyListener(Main.inputManager);
        window.addMouseListener(Main.inputManager);
        window.addMouseMotionListener(Main.inputManager);
        window.setVisible(true);
    }

    private static Vector3 point = new Vector3(0, 0, 100);
    private static void drawTriangles(Graphics g)
    {
        // for (int i = 0; i < Main.ObjectManager.triangles.size(); i ++)
        // {
        //     Triangle tempTriangle = Main.ObjectManager.triangles.get(i);
        //     Point p1ScreenCoords = translateToScreenCoords(tempTriangle.point1);
        //     Point p2ScreenCoords = translateToScreenCoords(tempTriangle.point2);
        //     Point p3ScreenCoords = translateToScreenCoords(tempTriangle.point3);

        //     Polygon screenTriangle = new Polygon(new int[]{p1ScreenCoords.x, p2ScreenCoords.x, p3ScreenCoords.x}, new int[]{p1ScreenCoords.y, p2ScreenCoords.y, p3ScreenCoords.y}, 3);

        //     g.setColor(tempTriangle.color);
            
        //     if (tempTriangle.fill)
        //         g.fillPolygon(screenTriangle);
        //     else
        //         g.drawPolygon(screenTriangle);
        // }
        
        g.fillOval(translateToScreenCoords(point).x, translateToScreenCoords(point).y, 10, 10);
    }

        
    private static Point translateToScreenCoords(Vector3 worldCoords)
    {
        Point screenCoord = new Point();

        double wX = worldCoords.x;
        double wZ = worldCoords.z;
        double wY = worldCoords.y;

        double cX = Camera.position.x;
        double cY = Camera.position.y;
        double cZ = Camera.position.z;
        double cHo = Math.toRadians(Camera.h_orientation);
        double cVo = Math.toRadians(Camera.v_orientation);
        double cVf = Math.toRadians(Camera.v_fov);
        double vHf = Math.toRadians(Camera.h_fov);

        double xzD = Math.sqrt((wX-cX)*(wX-cX) + (wZ-cZ)*(wZ-cZ));
        double hPA = cHo-Math.atan((wX-cX)/(wY-cY));
        
        double hPPD = xzD*(1.0/Math.cos(cVo)-((xzD*Math.tan(cVo))*(xzD*Math.tan(cVo)-wY))/(xzD*(1/Math.cos(cVo))));

        double dTPP = Math.cos(hPA)*hPPD;
        double hVAmt = Math.tan(vHf/2)*dTPP*2;
        double hpDOP = Math.tan(hPA)*dTPP + hVAmt/2;

        screenCoord.x = (int)(panel.getWidth()*(hpDOP/hVAmt));

        double vPA = cVo-Math.atan(wY/xzD);
        double vVAmt = Math.tan(cVf/2)*dTPP*2;
        double vpDOP = Math.tan(vPA) * dTPP + vVAmt/2;

        screenCoord.y = (int)(panel.getWidth()*(vpDOP/vVAmt));
        //screenCoord.y = panel.getWidth()/2 + (int)(Math.random()*10);

        // Vector3 h_offsetCamPos = new Vector3((-Math.sin(Math.toRadians(90+Camera.h_orientation)))*Math.cos(Math.toRadians(90+Camera.v_orientation)) * Math.atan((worldCoords.y - Camera.position.y)/(Math.sqrt(Math.pow(worldCoords.x - Camera.position.x, 2) + Math.pow(worldCoords.z - Camera.position.z, 2))))-Math.toRadians(Camera.v_orientation)*Math.sin(Math.atan((worldCoords.y - Camera.position.y)/(Math.sqrt(Math.pow(worldCoords.x - Camera.position.x, 2) + Math.pow(worldCoords.z - Camera.position.z, 2))))-Math.toRadians(Camera.v_orientation)) + Camera.position.x, 0, (-Math.cos(Math.toRadians(90+Camera.h_orientation))*Math.cos(Math.toRadians(90+Camera.v_orientation))) + Camera.position.z);
        // double h_pointAngle = Math.atan((worldCoords.z - h_offsetCamPos.z)/(worldCoords.x - h_offsetCamPos.x))-Math.toRadians(Camera.h_orientation);
        // double h_pointDistance = Math.sqrt(Math.pow(worldCoords.x - h_offsetCamPos.x, 2) + Math.pow(worldCoords.z - h_offsetCamPos.z, 2));
        // screenCoord.x = (int)(window.getWidth()*((Math.tan(Math.toRadians(Camera.h_fov/2))*h_pointDistance*Math.cos(h_pointAngle)-h_pointDistance*Math.sin(h_pointAngle))/(2*Math.tan(Math.toRadians(Camera.h_fov/2))*h_pointDistance*Math.cos(h_pointAngle))));

        //     //calculate the y screen coordinate
        // Vector3 v_offsetCamPos = new Vector3((-Math.cos(Math.toRadians(Camera.h_orientation-90))*Math.sqrt(Math.pow(worldCoords.x - Camera.position.x, 2) + Math.pow(worldCoords.z - Camera.position.z, 2)))*Math.sin(Math.atan((worldCoords.z - Camera.position.z)/(worldCoords.x - Camera.position.x))-Math.toRadians(Camera.h_orientation)) + Camera.position.x, 0,
        // Camera.position.z + (-Math.sin(Math.toRadians(Camera.h_orientation-90))*Math.sqrt(Math.pow(worldCoords.x - Camera.position.x, 2) + Math.pow(worldCoords.z - Camera.position.z, 2))*Math.sin(Math.atan((worldCoords.z - Camera.position.z)/(worldCoords.x - Camera.position.x))-Math.toRadians(Camera.h_orientation))));
        // double v_pointAngle = Math.atan((worldCoords.y - Camera.position.y)/(Math.sqrt(Math.pow(worldCoords.x - v_offsetCamPos.x, 2) + Math.pow(worldCoords.z - v_offsetCamPos.z, 2))))-Math.toRadians(Camera.v_orientation);
        // double v_pointDistance = Math.sqrt((Math.pow(worldCoords.x - v_offsetCamPos.x, 2) + Math.pow(worldCoords.z - v_offsetCamPos.z, 2) + (worldCoords.y-Camera.position.y)*(worldCoords.y-Camera.position.y)));
        // screenCoord.y = (int)(window.getHeight()*((Math.tan(Math.toRadians(Camera.v_fov/2))*v_pointDistance*Math.cos(v_pointAngle)-v_pointDistance*Math.sin(v_pointAngle))/(2*Math.tan(Math.toRadians(Camera.v_fov/2))*v_pointDistance*Math.cos(v_pointAngle))));
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

        public void paint(Graphics g) //write raster 
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


