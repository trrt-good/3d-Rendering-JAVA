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

    public void paint(Graphics g) //write raster 
    {
        super.paint(g);
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
        //TODO: sort by distance
        Plane renderPlane = new Plane(worldPoint, Vector3.negate(Camera.getDirectionVector()));
        double distanceToRenderPlane = Vector3.distanceToPlane(Camera.position, renderPlane);
        double pixelsPerUnit = getWidth()/(2*Math.tan(Camera.fov*0.017453292519943295/2)*distanceToRenderPlane);

        Vector3 camCenterPoint = Vector3.getIntersectionPoint(Camera.getDirectionVector(), Camera.position, renderPlane);

        Vector3 vectorToPoint = Vector3.subtract(worldPoint, camCenterPoint);
        Vector3 rightVector = Vector3.degAngleToVector(Camera.h_orientation + 90, 0);
        Vector3 downVector = Vector3.degAngleToVector(Camera.h_orientation, Camera.v_orientation-90);

        double angle = Vector3.getAngleBetween(vectorToPoint, rightVector);
        if (Vector3.dotProduct(vectorToPoint, downVector) > 0)
            angle = -angle;
        
        screenCoord.x = (int)(getWidth()/2 + Math.cos(angle)*vectorToPoint.getMagnitude()*pixelsPerUnit);
        screenCoord.y = (int)(getHeight()/2 - Math.sin(angle)*vectorToPoint.getMagnitude()*pixelsPerUnit);
        return screenCoord;
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        repaint();
    }
}


