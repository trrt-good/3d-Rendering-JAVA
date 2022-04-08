import javax.swing.*;

import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
public class RenderingPanel extends JPanel implements ActionListener
{
    private List<GameObject> gameObjects = new ArrayList<GameObject>();
    private List<Triangle> triangles = new ArrayList<Triangle>();

    private Timer timer;

    //for rendering:
    private BufferedImage renderImage;
    private Color backgroundColor;
    private Plane renderPlane;
    private boolean antiAliasing;

    //Camera:
    private Camera camera;

    //lighting:
    private Lighting lightingObject; 
    
    //fog:
    private double fogStartDistance;
    private double fullFogDistance;
    private boolean fogEnabled = false;
    private Color fogColor;

    //used for debug:
    private long nanosecondsPerFrame;
    private Font font = new Font("Times", Font.BOLD, 20);
    

    public RenderingPanel(boolean antiAliasingIn)
    {
        renderImage = new BufferedImage(1920, 1080, BufferedImage.TYPE_3BYTE_BGR);
        backgroundColor = new Color(200, 220, 255);
        setBackground(backgroundColor);
        antiAliasing = antiAliasingIn;

        timer = new Timer(1, this);
        timer.start();
    }

    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);

        long startOfFrame = System.nanoTime();
        requestFocusInWindow();
        g.drawImage(renderImage, 0, 0, this);
        g.setFont(font);
        if (gameObjects.size() > 0 && camera != null)
            drawTriangles(g);
        nanosecondsPerFrame = System.nanoTime()-startOfFrame;
    }

    public void setLighting(Lighting lighting)
    {
        long lightingStartTime = System.nanoTime();
        System.out.print("\tadding lighting... ");
        lightingObject = lighting;
        lightingObject.update(gameObjects);
        System.out.println("finished in " + (System.nanoTime()-lightingStartTime)/1000000.0 + "ms");
    }

    public void addGameObject(GameObject gameObject)
    {
        long gameObjectStartTime = System.nanoTime();
        System.out.print("\tadding gameObject " + gameObject.name + "... ");
        gameObjects.add(gameObject);
        lightingObject.update(gameObjects);
        triangles.addAll(gameObject.triangles);
        System.out.println("finished in " + (System.nanoTime()-gameObjectStartTime)/1000000.0 + "ms");
    }

    public void setCamera(Camera camIn)
    {
        long camStartTime = System.nanoTime();
        System.out.print("\tadding camera... ");
        if (camera == null)
        {
            camera = camIn;
            camera.timer.start();
            renderPlane = camera.getRenderPlane();
        }
        else    
        {
            camera.timer.stop();
            camera = camIn;
            camera.timer.start();
            renderPlane = camera.getRenderPlane();
        }
        System.out.println("finished in " + (System.nanoTime()-camStartTime)/1000000.0 + "ms");
    }

    public void setFog(double fogStartDistanceIn, double fullFogDistanceIn, Color fogColorIn)
    {
        fogStartDistance = fogStartDistanceIn;
        fullFogDistance = fullFogDistanceIn;
        fogColor = fogColorIn;
        fogEnabled = true;
    }

    public void enableFog()
    {
        fogEnabled = true;
    }

    public void dissableFog()
    {
        fogEnabled = false;
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
        renderPlane = camera.getRenderPlane();

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
                    if (Vector3.subtract(camera.position, Vector3.centerOfTriangle(triangles.get(i))).getMagnitude() < Vector3.subtract(camera.position, Vector3.centerOfTriangle(triangles.get(i+1))).getMagnitude())
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

        double distanceToTriangle = Vector3.subtract(Vector3.centerOfTriangle(triangle), camera.position).getMagnitude();  
        if (distanceToTriangle < camera.viewDistance)
        {
            if (Vector3.dotProduct(renderPlane.normal, Vector3.subtract(tempPoint1, renderPlane.pointOnPlane)) > 0 && Vector3.dotProduct(renderPlane.normal, Vector3.subtract(tempPoint2, renderPlane.pointOnPlane)) > 0 && Vector3.dotProduct(renderPlane.normal, Vector3.subtract(tempPoint3, renderPlane.pointOnPlane)) > 0)
            {
                tempPoint1 = Vector3.getIntersectionPoint(Vector3.subtract(tempPoint1, camera.position), camera.position, renderPlane);
                double pixelsPerUnit = getWidth()/camera.renderPlaneWidth;
                Vector3 camCenterPoint = Vector3.getIntersectionPoint(camera.getDirectionVector(), camera.position, renderPlane);
                Vector3 rotatedPoint = Vector3.rotateAroundXaxis(Vector3.rotateAroundYaxis( //rotates the points to only be on the XY plane
                    Vector3.subtract(tempPoint1, camCenterPoint), //moves the point to be centered around 0,0,0
                    -camera.h_orientation*0.017453292519943295), //amount to be rotated by horizontally
                    camera.v_orientation*0.017453292519943295); //amount to  be rotated by vertically
                if ((Math.abs(rotatedPoint.x) < camera.renderPlaneWidth/2*1.2 && Math.abs(rotatedPoint.y) < camera.renderPlaneWidth*((double)GraphicsManager.renderingPanel.getHeight()/(double)GraphicsManager.renderingPanel.getWidth())/2*1.2))
                    shouldDrawTriangle = true;
                p1ScreenCoords.x = (int)(getWidth()/2 + rotatedPoint.x*pixelsPerUnit);
                p1ScreenCoords.y = (int)(getHeight()/2 - rotatedPoint.y*pixelsPerUnit);
        
                tempPoint2 = Vector3.getIntersectionPoint(Vector3.subtract(tempPoint2, camera.position), camera.position, renderPlane);
                rotatedPoint = Vector3.rotateAroundXaxis(Vector3.rotateAroundYaxis( //rotates the points to only be on the XY plane
                    Vector3.subtract(tempPoint2, camCenterPoint), //moves the point to be centered around 0,0,0
                    -camera.h_orientation*0.017453292519943295), //amount to be rotated by horizontally
                    camera.v_orientation*0.017453292519943295); //amount to  be rotated by vertically
                if ((Math.abs(rotatedPoint.x) < camera.renderPlaneWidth/2*1.2 && Math.abs(rotatedPoint.y) < camera.renderPlaneWidth*((double)GraphicsManager.renderingPanel.getHeight()/GraphicsManager.renderingPanel.getWidth())/2*1.2))
                    shouldDrawTriangle = true;
                p2ScreenCoords.x = (int)(getWidth()/2 + rotatedPoint.x*pixelsPerUnit);
                p2ScreenCoords.y = (int)(getHeight()/2 - rotatedPoint.y*pixelsPerUnit);
        
                tempPoint3 = new Vector3(triangle.point3);
                tempPoint3 = Vector3.getIntersectionPoint(Vector3.subtract(tempPoint3, camera.position), camera.position, renderPlane);
                rotatedPoint = Vector3.rotateAroundXaxis(Vector3.rotateAroundYaxis( //rotates the points to only be on the XY plane
                    Vector3.subtract(tempPoint3, camCenterPoint), //moves the point to be centered around 0,0,0
                    -camera.h_orientation*0.017453292519943295), //amount to be rotated by horizontally
                    camera.v_orientation*0.017453292519943295); //amount to  be rotated by vertically
                if ((Math.abs(rotatedPoint.x) < camera.renderPlaneWidth/2*1.2 && Math.abs(rotatedPoint.y) < camera.renderPlaneWidth*((double)GraphicsManager.renderingPanel.getHeight()/GraphicsManager.renderingPanel.getWidth())/2*1.2))
                    shouldDrawTriangle = true;
                p3ScreenCoords.x = (int)(getWidth()/2 + rotatedPoint.x*pixelsPerUnit);
                p3ScreenCoords.y = (int)(getHeight()/2 - rotatedPoint.y*pixelsPerUnit);
            }

            if (shouldDrawTriangle)
            {
                Polygon screenTriangle = new Polygon(new int[]{p1ScreenCoords.x, p2ScreenCoords.x, p3ScreenCoords.x}, new int[]{p1ScreenCoords.y, p2ScreenCoords.y, p3ScreenCoords.y}, 3);
                Color colorUsed;
                if (triangle.parentGameObject != null && triangle.parentGameObject.shading && !triangle.parentGameObject.wireframe)
                {
                    Color litColor = triangle.getColorWithLighting();
                    if (fogEnabled && distanceToTriangle > fogStartDistance)
                    {
                        Color triangleColor;
                        if (distanceToTriangle > fullFogDistance)
                            triangleColor = fogColor;
                        else
                        {
                            double fogAmt = (distanceToTriangle-fogStartDistance)/(fullFogDistance-fogStartDistance);
                            int red = litColor.getRed() + (int)((fogColor.getRed()-litColor.getRed())*fogAmt*fogAmt);
                            int green = litColor.getGreen() + (int)((fogColor.getGreen()-litColor.getGreen())*fogAmt*fogAmt);
                            int blue = litColor.getBlue() + (int)((fogColor.getBlue()-litColor.getBlue())*fogAmt*fogAmt);
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
                            triangleColor = new Color(red, green, blue);
                        }
                        colorUsed = triangleColor;
                    }
                    else 
                        colorUsed = litColor;
                }   
                else 
                    colorUsed = triangle.color;

                if (triangle.fill)
                {
                    //g2d.fillPolygon(screenTriangle);
                    paintTriangle(p1ScreenCoords, p2ScreenCoords, p3ScreenCoords, colorUsed);
                    
                }
                    
                else
                {
                    g2d.setStroke(new BasicStroke(triangle.lineThickness));
                    g2d.drawLine(p1ScreenCoords.x, p1ScreenCoords.y, p2ScreenCoords.x, p2ScreenCoords.y);
                    g2d.drawLine(p2ScreenCoords.x, p2ScreenCoords.y, p3ScreenCoords.x, p3ScreenCoords.y);
                    g2d.drawLine(p3ScreenCoords.x, p3ScreenCoords.y, p1ScreenCoords.x, p1ScreenCoords.y);
                }
            }
        }
        
        // g2d.setColor(Color.BLACK);
        // g2d.drawString("time per frame: " + nanosecondsPerFrame/1000000.0 + "ms", 10, 20);
    }

    private void paintTriangle(Point p1, Point p2, Point p3, Color triangleColor)
    {
        Point tempPoint = new Point();
        int rgb = triangleColor.getRGB();
        System.out.println(rgb);
        if (p1.getY() < p2.getY())
        {
            tempPoint = p1;
            p1 = p2;
            p2 = tempPoint;
        }
        if (p2.getY() < p3.getY())
        {
            tempPoint = p2;
            p2 = p3;
            p3 = tempPoint;
        }
        if (p1.getY() < p2.getY())
        {
            tempPoint = p1;
            p1 = p2;
            p2 = tempPoint;
        }
        if (p2.getY() < p3.getY())
        {
            tempPoint = p2;
            p2 = p3;
            p3 = tempPoint;
        } 

        renderImage.setRGB(p3.x, p3.y, rgb);
        for (int i = 0; i < p2.getY() - p3.getY(); i ++)
        {
            int yLevel = p3.y + i;
            int leftCollide;
            leftCollide = (int)((yLevel-p2.y)/((double)(p3.y-p2.y)/(p3.x-p2.x))+p2.x);
            int rightCollide; 
            rightCollide = (int)((yLevel-p1.y)/((double)(p3.y-p1.y)/(p3.x-p1.x))+p1.x);
            for (int j  = 0; j < rightCollide-leftCollide; j++)
            {
                renderImage.setRGB(leftCollide + j, yLevel, rgb);
            }
        }
        
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        this.repaint();
    }
}


