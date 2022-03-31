import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class RenderingPanel extends JPanel implements ActionListener
{
    private Timer timer;
    private Color backgroundColor;
    public RenderingPanel(int fpsIn)
    {
        backgroundColor = Color.WHITE;
        setBackground(backgroundColor);
        timer = new Timer(1000/fpsIn + 1, this);
        timer.start();
    }

    public void paintComponent(Graphics g) //write raster 
    {
        super.paintComponent(g);
        drawTriangles(g);
        g.setColor(Color.BLACK);
    }

    private void drawTriangles(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(
             RenderingHints.KEY_TEXT_ANTIALIASING,
             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);

        for (int i = 0; i < Main.ObjectManager.triangles.size(); i ++)
        {
            Triangle tempTriangle = Main.ObjectManager.triangles.get(i);
            Point p1ScreenCoords = translateToScreenCoords(tempTriangle.point1);
            Point p2ScreenCoords = translateToScreenCoords(tempTriangle.point2);
            Point p3ScreenCoords = translateToScreenCoords(tempTriangle.point3);

            Polygon screenTriangle = new Polygon(new int[]{p1ScreenCoords.x, p2ScreenCoords.x, p3ScreenCoords.x}, new int[]{p1ScreenCoords.y, p2ScreenCoords.y, p3ScreenCoords.y}, 3);

            g2d.setColor(tempTriangle.color);
            
            if (tempTriangle.fill)
                g2d.fillPolygon(screenTriangle);
            else if (tempTriangle.lineThickness <= 1)
                g2d.drawPolygon(screenTriangle);
            else 
            {
                g2d.setStroke(new BasicStroke(tempTriangle.lineThickness));
                g2d.drawPolygon(screenTriangle);
            }
        }
        // Point point = translateToScreenCoords(new Vector3(0, 0, 100));
        // g.fillOval(point.x, point.y, 5, 5);
    }

        
    private Point translateToScreenCoords(Vector3 worldPoint)
    {
        Point screenCoord = new Point();
        Plane renderPlane = new Plane(worldPoint, Camera.mainCamera.getDirectionVector());
        double pixelsPerUnit = getWidth()/(2*Math.tan(Camera.mainCamera.fov*0.017453292519943295/2)*Vector3.distanceToPlane(Camera.mainCamera.position, renderPlane));
        Vector3 camCenterPoint = Vector3.getIntersectionPoint(Camera.mainCamera.getDirectionVector(), Camera.mainCamera.position, renderPlane);
        Vector3 rotatedPoint = Vector3.rotateAroundXaxis(Vector3.rotateAroundYaxis( //rotates the points to only be on the XY plane
            Vector3.subtract(worldPoint, camCenterPoint), //moves the point to be centered around 0,0,0
            -Camera.mainCamera.h_orientation*0.017453292519943295), //amount to be rotated by horizontally
            Camera.mainCamera.v_orientation*0.017453292519943295); //amount to  be rotated by vertically
        screenCoord.x = (int)(getWidth()/2 + rotatedPoint.x*pixelsPerUnit);
        screenCoord.y = (int)(getHeight()/2 - rotatedPoint.y*pixelsPerUnit);
        return screenCoord;
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        repaint();
    }
}


