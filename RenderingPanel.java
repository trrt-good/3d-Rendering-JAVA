import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
public class RenderingPanel extends JPanel implements ActionListener
{
    private Timer timer;
    private Color backgroundColor;

    private Plane renderPlane;
    private boolean antiAliasing;
    private long nsPerFrame;
    private Font font = new Font("Times", Font.BOLD, 20);
    private boolean startedRendering = false;
    private List<GameObject> gameObjects = new ArrayList<GameObject>();
    private List<Triangle> triangles = new ArrayList<Triangle>();

    public Lighting lightingObject; 
    

    public RenderingPanel(boolean antiAliasingIn)
    {
        backgroundColor = new Color(200, 220, 255);
        setBackground(backgroundColor);
        antiAliasing = antiAliasingIn;

        Camera.mainCamera = new Camera();
        Camera.mainCamera.timer.start();
        renderPlane = Camera.mainCamera.getRenderPlane();

        timer = new Timer(1, this);
        timer.start();
        startedRendering = true;
    }

    public void paintComponent(Graphics g) 
    {
        long startOfFrame = System.nanoTime();
        requestFocusInWindow();
        super.paintComponent(g);
        g.setFont(font);
        if (startedRendering)
            drawTriangles(g);
        nsPerFrame = System.nanoTime()-startOfFrame;
    }

    public void addLighting(Lighting lighting)
    {
        long lightingStart = System.nanoTime();
        System.out.print("\tlighting... ");
        lightingObject = lighting;
        lightingObject.update(gameObjects);
        System.out.println("finished in " + (System.nanoTime()-lightingStart)/1000000 + "ms");
    }

    public void addGameObject(GameObject gameObject)
    {
        gameObjects.add(gameObject);
        lightingObject.update(gameObjects);
        triangles.addAll(gameObject.triangles);
    }
    
    private void drawTriangles(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;
        if (antiAliasing)
        {
            RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHints(rh);
        }
        renderPlane = Camera.mainCamera.getRenderPlane();

        orderTriangles();    

        for (int i = 0; i < triangles.size(); i ++)
        {
            renderTriangle(g2d, triangles.get(i));
        }
    }

    private void orderTriangles()
    {
        boolean changed = true;
        while (changed == true)
        {
            changed = false;
            for (int i = 0; i < triangles.size()-1; i++)
            {
                if (triangles.get(i).parentGameObject.shading)
                {
                    if (Vector3.subtract(Camera.mainCamera.position, Vector3.centerOfTriangle(triangles.get(i))).getMagnitude() < Vector3.subtract(Camera.mainCamera.position, Vector3.centerOfTriangle(triangles.get(i+1))).getMagnitude())
                    {
                        Triangle closerTriangle = triangles.get(i);
                        triangles.set(i, triangles.get(i+1));
                        triangles.set(i + 1, closerTriangle);
                        changed = true;
                    }
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
            Polygon screenTriangle = new Polygon(new int[]{p1ScreenCoords.x, p2ScreenCoords.x, p3ScreenCoords.x}, new int[]{p1ScreenCoords.y, p2ScreenCoords.y, p3ScreenCoords.y}, 3);
            if (triangle.parentGameObject != null && triangle.parentGameObject.shading && !triangle.parentGameObject.wireframe)
                g2d.setColor(triangle.getColorWithLighting());
            else if (triangle.parentGameObject == null && triangle.fill)
                g2d.setColor(triangle.getColorWithLighting());
            else 
                g2d.setColor(triangle.color);

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

        g2d.setColor(Color.BLACK);
        g2d.drawString("time per frame: " + nsPerFrame/1000000.0 + "ms", 10, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        repaint();
    }
}


