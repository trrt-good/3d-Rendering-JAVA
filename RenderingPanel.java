import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
public class RenderingPanel extends JPanel implements ActionListener
{
    private Timer timer;
    private Color backgroundColor;

    private Plane renderPlane;
    private Plane farClippingPlane;

    public Vector3 lightDirectionVector;
    public double lightingIntensity; 

    public RenderingPanel(int fpsIn)
    {
        backgroundColor = Color.WHITE;
        setBackground(backgroundColor);

        lightDirectionVector = new Vector3(0, -1, 0);
        lightingIntensity = 5;

        farClippingPlane = Camera.mainCamera.getNearClippingPlane();
        renderPlane = Camera.mainCamera.getRenderPlane();

        timer = new Timer(1000/fpsIn + 1, this);
        timer.start();
    }

    public void paintComponent(Graphics g) 
    {
        requestFocusInWindow();
        super.paintComponent(g);
        drawTriangles(g);
    }

    private void drawTriangles(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;
        RenderingHints rh = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);

        renderPlane = Camera.mainCamera.getRenderPlane();
        farClippingPlane = Camera.mainCamera.getFarClippingPlane();

        orderTriangles();

        for (int i = 0; i < Main.ObjectManager.triangles.size(); i ++)
        {
            renderTriangle(g2d, Main.ObjectManager.triangles.get(i));
        }
    }

    private void orderTriangles()
    {
        boolean changed = false;
        while (!changed)
        {
            for (int i = 0; i < Main.ObjectManager.triangles.size()-1; i++)
            {
                if (Vector3.subtract(Camera.mainCamera.position, Vector3.centerOfTriangle(Main.ObjectManager.triangles.get(i))).getMagnitude() < Vector3.subtract(Camera.mainCamera.position, Vector3.centerOfTriangle(Main.ObjectManager.triangles.get(i+1))).getMagnitude())
                {
                    Triangle closerTriangle = Main.ObjectManager.triangles.get(i);
                    Main.ObjectManager.triangles.set(i, Main.ObjectManager.triangles.get(i+1));
                    Main.ObjectManager.triangles.set(i + 1, closerTriangle);
                    changed = true;
                }
            }
        }
    }

    private void renderTriangle(Graphics2D g2d, Triangle triangle)
    {
        Point p1ScreenCoords = new Point();
        Point p2ScreenCoords = new Point();
        Point p3ScreenCoords = new Point();
        boolean shouldDrawTriangle = false;

        Vector3 tempPoint1 = new Vector3(triangle.point1);
        Vector3 tempPoint2 = new Vector3(triangle.point2);
        Vector3 tempPoint3 = new Vector3(triangle.point3);

        if (Vector3.dotProduct(renderPlane.normal, Vector3.subtract(tempPoint1, renderPlane.pointOnPlane)) > 0 && Vector3.dotProduct(renderPlane.normal, Vector3.subtract(tempPoint2, renderPlane.pointOnPlane)) > 0 && Vector3.dotProduct(renderPlane.normal, Vector3.subtract(tempPoint3, renderPlane.pointOnPlane)) > 0)
        {
            tempPoint1 = Vector3.getIntersectionPoint(Vector3.subtract(tempPoint1, Camera.mainCamera.position), Camera.mainCamera.position, renderPlane);
            double pixelsPerUnit = getWidth()/Camera.mainCamera.renderPlaneWidth;
            Vector3 camCenterPoint = Vector3.getIntersectionPoint(Camera.mainCamera.getDirectionVector(), Camera.mainCamera.position, renderPlane);
            Vector3 rotatedPoint = Vector3.rotateAroundXaxis(Vector3.rotateAroundYaxis( //rotates the points to only be on the XY plane
                Vector3.subtract(tempPoint1, camCenterPoint), //moves the point to be centered around 0,0,0
                -Camera.mainCamera.h_orientation*0.017453292519943295), //amount to be rotated by horizontally
                Camera.mainCamera.v_orientation*0.017453292519943295); //amount to  be rotated by vertically
            if ((Math.abs(rotatedPoint.x) < Camera.mainCamera.renderPlaneWidth/2*1.2 && Math.abs(rotatedPoint.y) < Camera.mainCamera.renderPlaneWidth*((double)GraphicsManager.renderingPanel.getHeight()/(double)GraphicsManager.renderingPanel.getWidth())/2*1.2))
                shouldDrawTriangle = true;
            p1ScreenCoords.x = (int)(getWidth()/2 + rotatedPoint.x*pixelsPerUnit);
            p1ScreenCoords.y = (int)(getHeight()/2 - rotatedPoint.y*pixelsPerUnit);
    
            tempPoint2 = Vector3.getIntersectionPoint(Vector3.subtract(tempPoint2, Camera.mainCamera.position), Camera.mainCamera.position, renderPlane);
            rotatedPoint = Vector3.rotateAroundXaxis(Vector3.rotateAroundYaxis( //rotates the points to only be on the XY plane
                Vector3.subtract(tempPoint2, camCenterPoint), //moves the point to be centered around 0,0,0
                -Camera.mainCamera.h_orientation*0.017453292519943295), //amount to be rotated by horizontally
                Camera.mainCamera.v_orientation*0.017453292519943295); //amount to  be rotated by vertically
            if ((Math.abs(rotatedPoint.x) < Camera.mainCamera.renderPlaneWidth/2*1.2 && Math.abs(rotatedPoint.y) < Camera.mainCamera.renderPlaneWidth*((double)GraphicsManager.renderingPanel.getHeight()/GraphicsManager.renderingPanel.getWidth())/2*1.2))
                shouldDrawTriangle = true;
            p2ScreenCoords.x = (int)(getWidth()/2 + rotatedPoint.x*pixelsPerUnit);
            p2ScreenCoords.y = (int)(getHeight()/2 - rotatedPoint.y*pixelsPerUnit);
    
            tempPoint3 = new Vector3(triangle.point3);
            tempPoint3 = Vector3.getIntersectionPoint(Vector3.subtract(tempPoint3, Camera.mainCamera.position), Camera.mainCamera.position, renderPlane);
            rotatedPoint = Vector3.rotateAroundXaxis(Vector3.rotateAroundYaxis( //rotates the points to only be on the XY plane
                Vector3.subtract(tempPoint3, camCenterPoint), //moves the point to be centered around 0,0,0
                -Camera.mainCamera.h_orientation*0.017453292519943295), //amount to be rotated by horizontally
                Camera.mainCamera.v_orientation*0.017453292519943295); //amount to  be rotated by vertically
            if ((Math.abs(rotatedPoint.x) < Camera.mainCamera.renderPlaneWidth/2*1.2 && Math.abs(rotatedPoint.y) < Camera.mainCamera.renderPlaneWidth*((double)GraphicsManager.renderingPanel.getHeight()/GraphicsManager.renderingPanel.getWidth())/2*1.2))
                shouldDrawTriangle = true;
            p3ScreenCoords.x = (int)(getWidth()/2 + rotatedPoint.x*pixelsPerUnit);
            p3ScreenCoords.y = (int)(getHeight()/2 - rotatedPoint.y*pixelsPerUnit);
        }

        if (shouldDrawTriangle)
        {
            int brightness = (int)(Math.abs((Math.PI/Vector3.getAngleBetween(lightDirectionVector, Vector3.crossProduct(Vector3.subtract(triangle.point1, triangle.point2), Vector3.subtract(triangle.point2, triangle.point3))))/(lightingIntensity/100)));
            int red = triangle.color.getRed() - brightness;
            int green = triangle.color.getGreen() - brightness;
            int blue = triangle.color.getBlue() - brightness;

            if (red > 255)
                red = 255;
            if (red < 0)
                red = 0;
            if (green > 255)
                green = 255;
            if (green < 0)
                green = 0;
            if (blue > 255)
                blue = 255;
            if (blue < 0)
                blue = 0;
            
            Color triangleColor = new Color(red, green, blue);

            Polygon screenTriangle = new Polygon(new int[]{p1ScreenCoords.x, p2ScreenCoords.x, p3ScreenCoords.x}, new int[]{p1ScreenCoords.y, p2ScreenCoords.y, p3ScreenCoords.y}, 3);

            g2d.setColor(triangleColor);
            if (triangle.fill)
                g2d.fillPolygon(screenTriangle);
            else
            {
                g2d.setStroke(new BasicStroke(triangle.lineThickness));
                g2d.drawLine(p1ScreenCoords.x, p1ScreenCoords.y, p2ScreenCoords.x, p2ScreenCoords.y);
                g2d.drawLine(p2ScreenCoords.x, p2ScreenCoords.y, p3ScreenCoords.x, p3ScreenCoords.y);
                g2d.drawLine(p3ScreenCoords.x, p3ScreenCoords.y, p1ScreenCoords.x, p1ScreenCoords.y);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        repaint();
    }
}


