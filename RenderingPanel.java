import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class RenderingPanel extends JPanel implements ActionListener
{
    Timer timer;
    public RenderingPanel(int fpsIn)
    {
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

        
    private Point translateToScreenCoords(Vector3 worldPoint)
    {
        Point screenCoord = new Point();

        Plane perpendicularRenderPlane = new Plane(worldPoint, Vector3.negate(Camera.getDirectionVector()));
        Vector3 topLeftFovVector = Vector3.degAngleToVector(Camera.h_orientation-Camera.h_fov/2, Camera.v_orientation+Camera.v_fov/2);
        Vector3 bottomRightFovVector = Vector3.degAngleToVector(Camera.h_orientation+Camera.h_fov/2, Camera.v_orientation-Camera.v_fov/2);

        Vector3 topLeftCoord = Vector3.getIntersectionPoint(topLeftFovVector, Camera.position, perpendicularRenderPlane);
        Vector3 bottomRightCoord = Vector3.getIntersectionPoint(bottomRightFovVector, Camera.position, perpendicularRenderPlane);

        

        //screenCoord.y = panel.getWidth()/2 + (int)(Math.random()*10);

        // Vector3 h_offsetCamPos = new Vector3((-Math.sin(Math.toRadians(90+Camera.h_orientation)))*Math.cos(Math.toRadians(90+Camera.v_orientation)) * Math.atan((worldPoint.y - Camera.position.y)/(Math.sqrt(Math.pow(worldPoint.x - Camera.position.x, 2) + Math.pow(worldPoint.z - Camera.position.z, 2))))-Math.toRadians(Camera.v_orientation)*Math.sin(Math.atan((worldPoint.y - Camera.position.y)/(Math.sqrt(Math.pow(worldPoint.x - Camera.position.x, 2) + Math.pow(worldPoint.z - Camera.position.z, 2))))-Math.toRadians(Camera.v_orientation)) + Camera.position.x, 0, (-Math.cos(Math.toRadians(90+Camera.h_orientation))*Math.cos(Math.toRadians(90+Camera.v_orientation))) + Camera.position.z);
        // double h_pointAngle = Math.atan((worldPoint.z - h_offsetCamPos.z)/(worldPoint.x - h_offsetCamPos.x))-Math.toRadians(Camera.h_orientation);
        // double h_pointDistance = Math.sqrt(Math.pow(worldPoint.x - h_offsetCamPos.x, 2) + Math.pow(worldPoint.z - h_offsetCamPos.z, 2));
        // screenCoord.x = (int)(window.getWidth()*((Math.tan(Math.toRadians(Camera.h_fov/2))*h_pointDistance*Math.cos(h_pointAngle)-h_pointDistance*Math.sin(h_pointAngle))/(2*Math.tan(Math.toRadians(Camera.h_fov/2))*h_pointDistance*Math.cos(h_pointAngle))));

        //     //calculate the y screen coordinate
        // Vector3 v_offsetCamPos = new Vector3((-Math.cos(Math.toRadians(Camera.h_orientation-90))*Math.sqrt(Math.pow(worldPoint.x - Camera.position.x, 2) + Math.pow(worldPoint.z - Camera.position.z, 2)))*Math.sin(Math.atan((worldPoint.z - Camera.position.z)/(worldPoint.x - Camera.position.x))-Math.toRadians(Camera.h_orientation)) + Camera.position.x, 0,
        // Camera.position.z + (-Math.sin(Math.toRadians(Camera.h_orientation-90))*Math.sqrt(Math.pow(worldPoint.x - Camera.position.x, 2) + Math.pow(worldPoint.z - Camera.position.z, 2))*Math.sin(Math.atan((worldPoint.z - Camera.position.z)/(worldPoint.x - Camera.position.x))-Math.toRadians(Camera.h_orientation))));
        // double v_pointAngle = Math.atan((worldPoint.y - Camera.position.y)/(Math.sqrt(Math.pow(worldPoint.x - v_offsetCamPos.x, 2) + Math.pow(worldPoint.z - v_offsetCamPos.z, 2))))-Math.toRadians(Camera.v_orientation);
        // double v_pointDistance = Math.sqrt((Math.pow(worldPoint.x - v_offsetCamPos.x, 2) + Math.pow(worldPoint.z - v_offsetCamPos.z, 2) + (worldPoint.y-Camera.position.y)*(worldPoint.y-Camera.position.y)));
        // screenCoord.y = (int)(window.getHeight()*((Math.tan(Math.toRadians(Camera.v_fov/2))*v_pointDistance*Math.cos(v_pointAngle)-v_pointDistance*Math.sin(v_pointAngle))/(2*Math.tan(Math.toRadians(Camera.v_fov/2))*v_pointDistance*Math.cos(v_pointAngle))));
        return screenCoord;
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        repaint();
    }
}


