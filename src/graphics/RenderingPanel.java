package src.graphics;
import javax.swing.JPanel;

import src.gameObject.GameObject;
import src.primitives.Matrix3x3;
import src.primitives.Plane;
import src.primitives.Quaternion;
import src.primitives.Triangle;
import src.primitives.Vector3;
import src.testing.TimingHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.awt.Color;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Graphics;

import java.awt.image.BufferedImage;

public class RenderingPanel extends JPanel implements Runnable
{
    //collection of all the objects that the rendering panel will render
    private ArrayList<GameObject> gameObjects = new ArrayList<GameObject>(); 
    private HashMap<String, Integer> gameObjectIndices = new HashMap<String, Integer>();

    //for rendering:
    private BufferedImage renderImage;
    private Color backgroundColor;
    private Plane renderPlane;        
    private int[] blankImagePixelColorData;
    private ArrayList<Triangle2D>[] sortQeue;
    private ArrayList<Triangle2D> drawQeue;
    private Quaternion pointRotationQuaternion;
    private double pixelsPerUnit;
    private Vector3 camCenterPoint;
    private double maxTriangleDistance;
    private double minTriangleDistance;

    //multithreading:
    private Thread renderingThread;
    private boolean threadRunning;
    private int fps;

    //Camera values:
    private Camera camera;
    private Vector3 camDirection;
    private Vector3 camPos;
    private double renderPlaneWidth;

    //lighting:
    private Lighting lightingObject; 
    
    //fog stats:
    private double fogStartDistance;
    private double fullFogDistance;
    private boolean fogEnabled = false;
    private int fogR;
    private int fogG;
    private int fogB;

    //used to help with optimizations:
    private TimingHelper totalFrameTime = new TimingHelper("totalFrameTime");
    private TimingHelper trianglesOrderTime = new TimingHelper("triangleOrderTime");
    private TimingHelper trianglesCalculateTime = new TimingHelper("trianglesCalculateTime");
    private TimingHelper trianglesPaintTime = new TimingHelper("trianglesPaintTime");

    /**
     * creates a rendering panel with the specified with and height for the 
     * buffered image renderer. Once set, the buffered image size cannot be 
     * changed. 
     * @param width width of the panel and buffered image 
     * @param height width of the panel and buffered image 
     */
    public RenderingPanel(int width, int height)
    {
        setPreferredSize(new Dimension(width, height));

        //background color: 
        backgroundColor = new Color(200, 220, 255);

        //innitialize fields 
        camera = null;
        lightingObject = null;
        gameObjects = new ArrayList<GameObject>();
        sortQeue = null;
        camDirection = Vector3.ZERO;   
        camPos = Vector3.ZERO;
        fps = -1;
        drawQeue = new ArrayList<Triangle2D>();
        
        //creates the buffered image which will be used to render triangles. 
        renderImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        //empty image pixel color data array is used to store the pixel data for a blank image,
        //which is used to clear the buffered image before each frame is drawn.
        blankImagePixelColorData = new int[width*height];
        Arrays.fill(blankImagePixelColorData, convertToIntRGB(backgroundColor));
    }

    public void paintComponent(Graphics g) 
    {
        totalFrameTime.stopClock();
        totalFrameTime.startClock();
        //makes sure that there are triangles to render in the first place, and that the camera exists.
        if (gameObjects.size() > 0 && camera != null)
        {
            computeTriangles();
            sortTriangles();
            drawBufferedImage();
            g.drawImage(renderImage, 0, 0, this);
            //fps counter 
            g.drawString("fps: " + (int)(1000/totalFrameTime.getDeltaTime()), 30, 30);
        }
        else
        {
            //show an error on the screen if there is no camera or no game object
            g.drawString((camera == null)? "NO CAMERA" : "NO GAMEOBJECTS", getWidth()/2, getHeight()/2);
        }
    }

    /**
     * sets the frames per second limit of the rendering panel
     * @param limit the desired fps limit
     */
    public void setFPSlimit(int limit)
    {
        fps = Math.max(0, limit);
    }

    /**
     * sets the lighting for the entire scene, which automatically 
     * updates the lighting for each of the gameobjects in {@code gameObjects}. 
     * @param lighting the lighting object applied to the rendering panel
     */
    public void setLighting(Lighting lighting)
    {
        if (lighting == null)
        {
            System.err.println("WARNING at: RenderingPanel/setLighting() method: \n\tlighting is null, lighting not set");
            return;
        }
        lightingObject = lighting;
        lightingObject.update(gameObjects);
    }

    //adds a mesh to be rendered, as well as updating it's lighting
    public void addGameObject(GameObject gameObject)
    {
        if (gameObject != null && gameObject.getName() != null)
        {
            gameObjectIndices.put(gameObject.getName(), gameObjects.size());
            gameObjects.add(gameObject);
            if (lightingObject != null)
                lightingObject.update(gameObjects);
        }
        else
        {
            System.err.println("WARNING at: RenderingPanel/addMesh() method: \n\tGameObject or it's name is null. Object not added");
        }
    }

    /**
     * removes a game object based on it's name 
     * @param name name of the game object
     */
    public void removeGameObject(String name)
    {
        if (gameObjectIndices.containsKey(name))
        {
            gameObjects.remove((int)gameObjectIndices.get(name));
        }
        else
        {
            System.err.println("WARNING at: RenderingPanel/removeGameObject() method: \n\tCould not find the specified name. No GameObjects removed");
        }
    }

    /**
     * sets the camera for the rendering panel 
     * @param camIn camera 
     */
    public void setCamera(Camera camIn)
    {
        if (camIn == null)
        {
            System.err.println("WARNING at: RenderingPanel/setCamera() method: \n\tcamera is null, camera not set");
            return;
        }
        camera = camIn;
        renderPlaneWidth = camera.getRenderPlaneWidth();
        sortQeue = new ArrayList[(int)(camera.getFarClipDistancee()-camera.getNearClipDistance())+1];
        for (int i = 0; i < sortQeue.length; i ++)
        {
            sortQeue[i] = new ArrayList<Triangle2D>();
        }
        renderPlane = new Plane(Vector3.add(Vector3.multiply(camDirection, camera.getRenderPlaneDistance()), camera.getPosition()), camDirection);;
    }

    /**
     * set the fog of the rendering panel, and automatically enables fog
     * @param fogStartDistanceIn the distance where the fog starts 
     * @param fullFogDistanceIn the distance at which objects become fully covered 
     * by the fog, and become the fog color 
     * @param color the color of the fog
     */
    public void setFog(double fogStartDistanceIn, double fullFogDistanceIn, Color color)
    {
        fogStartDistance = fogStartDistanceIn;
        fullFogDistance = fullFogDistanceIn;
        fogR = color.getRed();
        fogG = color.getGreen();
        fogB = color.getBlue();
        fogEnabled = true;
    }

    /**
     * enables fog 
     */
    public void enableFog()
    {
        fogEnabled = true;
    }

    /**
     * dissables fog 
     */
    public void dissableFog()
    {
        fogEnabled = false;
    }

    private void computeTriangles()
    {
        maxTriangleDistance = 0;
        minTriangleDistance = camera.getFarClipDistancee();
        trianglesCalculateTime.startClock();
        pixelsPerUnit = getWidth()/renderPlaneWidth;
        renderPlaneWidth = camera.getRenderPlaneWidth();
        camPos = camera.getPosition();
        camDirection = camera.getDirectionVector();
        camCenterPoint = Vector3.add(Vector3.multiply(camDirection, camera.getRenderPlaneDistance()), camPos);
        renderPlane = new Plane(Vector3.add(Vector3.multiply(camDirection, camera.getRenderPlaneDistance()), camPos), camDirection);
        pointRotationQuaternion = createRotationQuaternion(camera.getVorientation(), -camera.getHorientation());
        
        for (int i = 0; i < gameObjects.size(); i ++)
        {
            if (gameObjects.get(i).getMesh() != null)
            {
                for (int j = 0; j < gameObjects.get(i).getMesh().getTriangles().size(); j++)
                {
                    calculateTriangle(gameObjects.get(i).getMesh().getTriangles().get(j));
                }
            }
        }

        trianglesCalculateTime.stopClock();
    }

    private Quaternion createRotationQuaternion(double pitch, double yaw)
    {
        //x axis rotation first
        pitch = Math.sin(pitch/2);
        double w1 = Math.sqrt(1-pitch*pitch);

        //y axis rotation
        yaw = Math.sin(yaw/2);
        double w2 = Math.sqrt(1-yaw*yaw);

        return new Quaternion(w1*w2, w2*pitch, w1*yaw, pitch*yaw);  
    }

    private void sortTriangles()
    {
        trianglesOrderTime.startClock();
        for (int i = (int)maxTriangleDistance; i > minTriangleDistance-1; i--)
        {
            if (sortQeue[i].size() > 0)
            {
                drawQeue.addAll(sortQeue[i]);
                sortQeue[i].clear();
            }
        }
        trianglesOrderTime.stopClock();
    }

    private void drawBufferedImage()
    {
        trianglesPaintTime.startClock();
        renderImage.getRaster().setDataElements(0, 0, renderImage.getWidth(), renderImage.getHeight(), blankImagePixelColorData);
        for (int i = 0; i < drawQeue.size(); i++)
        {
            Triangle2D triangle2d = drawQeue.get(i);
            paintTriangle(triangle2d.p1, triangle2d.p2, triangle2d.p3, triangle2d.color);    
        }
        drawQeue.clear();
        trianglesPaintTime.stopClock();
    }

    /**
     * starts the frame updates of the rendering panel 
     */
    public void start()
    {
        validate();
        revalidate();
        if (renderingThread == null)
        {
            threadRunning = true;
            renderingThread = new Thread(this, "Rendering");
            renderingThread.start();
        }
    }

    /**
     * the run method for the runnable thread  
     */
    public void run() 
    {
        while(threadRunning)
        {
            if (fps > 0)
            {
                try
                {
                    Thread.sleep(1000/(fps));
                }
                catch (InterruptedException e)
                {}
            }
            repaint();
        }
    }

    /**
     * stops the rendering panel frame updates 
     */
    public void stopThread()
    {
        try
        {
            threadRunning = false;
            renderingThread.interrupt();
            renderingThread = null;
        }
        catch (SecurityException e)
        {

        }
    }

    //calculates the three screen coordinates of a single triangle in world space, based off the orientation and position of the camera. 
    //It then adds the resulting 2d triangle into the triangle2dList for painting later. 
    private void calculateTriangle(Triangle triangle)
    {
        Vector3 triangleCenter = triangle.getCenter();
        double distanceToTriangle = Vector3.subtract(triangleCenter, camPos).getMagnitude();  
        if (distanceToTriangle > maxTriangleDistance)
            maxTriangleDistance = distanceToTriangle;
        else if (distanceToTriangle < minTriangleDistance)
            minTriangleDistance = distanceToTriangle;
        if 
        (
            Vector3.dotProduct(triangle.getPlane().normal, Vector3.subtract(triangleCenter, camPos)) > 0 //is the triangle facing away?
            || Vector3.dotProduct(Vector3.subtract(triangleCenter, camPos), camDirection) <= 0 //is the triangle behind the camera?
            || distanceToTriangle >= camera.getFarClipDistancee() //is the triangle too far away?
            || distanceToTriangle <= camera.getNearClipDistance() //is the triangle too close?
        )
            return;

        //clone the triangle's vertices:
        Vector3 triangleVertex1 = new Vector3(triangle.vertex1);
        Vector3 triangleVertex2 = new Vector3(triangle.vertex2);
        Vector3 triangleVertex3 = new Vector3(triangle.vertex3);
        //the screen coords of the triangle, to be determined by the rest of the method.
        Point p1ScreenCoords = new Point();
        Point p2ScreenCoords = new Point();
        Point p3ScreenCoords = new Point();
        //boolean default false, but set true if just one of the verticies is within the camera's fov. 
        boolean shouldDrawTriangle = false;

        //get intersection with render plane 
        triangleVertex1 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex1, camPos), camPos, renderPlane);

        //rotate the point: 
        triangleVertex1 = Vector3.rotate(Vector3.subtract(triangleVertex1, camCenterPoint), pointRotationQuaternion);

        //check if it's in the fov
        if ((Math.abs(triangleVertex1.x) < renderPlaneWidth/2*1.2 && Math.abs(triangleVertex1.y) < renderPlaneWidth*((double)getHeight()/(double)getWidth())/2))
            shouldDrawTriangle = true;

        //scale to the screen coordinates
        p1ScreenCoords.x = (int)(getWidth()/2 + triangleVertex1.x*pixelsPerUnit);
        p1ScreenCoords.y = (int)(getHeight()/2 - triangleVertex1.y*pixelsPerUnit);

        //repeat for each of the other two vertices
        triangleVertex2 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex2, camPos), camPos, renderPlane);
        triangleVertex2 = Vector3.rotate(Vector3.subtract(triangleVertex2, camCenterPoint), pointRotationQuaternion);
        if ((Math.abs(triangleVertex2.x) < renderPlaneWidth/2 && Math.abs(triangleVertex2.y) < renderPlaneWidth*((double)getHeight()/getWidth())/2))
            shouldDrawTriangle = true;
        p2ScreenCoords.x = (int)(getWidth()/2 + triangleVertex2.x*pixelsPerUnit);
        p2ScreenCoords.y = (int)(getHeight()/2 - triangleVertex2.y*pixelsPerUnit);

        triangleVertex3 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex3, camPos), camPos, renderPlane);
        triangleVertex3 = Vector3.rotate(Vector3.subtract(triangleVertex3, camCenterPoint), pointRotationQuaternion);
        if ((Math.abs(triangleVertex3.x) < renderPlaneWidth/2 && Math.abs(triangleVertex3.y) < renderPlaneWidth*((double)getHeight()/getWidth())/2))
            shouldDrawTriangle = true;
        p3ScreenCoords.x = (int)(getWidth()/2 + triangleVertex3.x*pixelsPerUnit);
        p3ScreenCoords.y = (int)(getHeight()/2 - triangleVertex3.y*pixelsPerUnit);

        if (shouldDrawTriangle)
        {
            int colorUsed = 16711935;
            if (triangle.getMesh() != null && triangle.getMesh().isShaded())
            {
                Color litColor = triangle.getColorWithLighting();
                if (fogEnabled && distanceToTriangle > fogStartDistance)
                {
                    if (distanceToTriangle > fullFogDistance)
                    {
                        colorUsed = convertToIntRGB(fogR, fogG, fogB);
                    }
                    else
                    {
                        //skews the triangle's color closer to the fog color as a function of distance. 
                        double fogAmt = (distanceToTriangle-fogStartDistance)/(fullFogDistance-fogStartDistance);
                        int red = (int)Math.max(0, Math.min(225, litColor.getRed() + (fogR-litColor.getRed())*fogAmt*fogAmt));
                        int green = (int)Math.max(0, Math.min(225, litColor.getGreen() + (fogG-litColor.getGreen())*fogAmt*fogAmt));
                        int blue = (int)Math.max(0, Math.min(225, litColor.getBlue() + (fogB-litColor.getBlue())*fogAmt*fogAmt));

                        colorUsed = convertToIntRGB(red, green, blue);
                    }
                }
                else 
                    colorUsed = convertToIntRGB(litColor);
            }   
            else 
                colorUsed = convertToIntRGB(triangle.getBaseColor());

            //adds the 2d triangle object into the triangle2d array.
            sortQeue[(int)distanceToTriangle].add(new Triangle2D(p1ScreenCoords, p2ScreenCoords, p3ScreenCoords, colorUsed, distanceToTriangle));
        }
    }

    /**
     * calculates the integer rgb value of a color, which is used for buffered images. 
     * @param r the red value of the color 
     * @param g the green value of the color 
     * @param b the blue value of the color 
     * @return the integer rgb value of a color, which is used for buffered images.
     */
    private int convertToIntRGB(int r, int g, int b)
    {
        return 65536 * r + 256 * g + b;
    }

    /**
     * calculates the integer rgb value of a color, which is used for buffered images. 
     * @param color a color object 
     * @return the integer rgb value of a color, which is used for buffered images.
     */
    private int convertToIntRGB(Color color)
    {
        return 65536 * color.getRed() + 256 * color.getGreen() + color.getBlue();
    }

    /**
     * paints a solid triangle on the buffered image with verticies at {@code p1}, {@code p2} and {@code p3}. 
     * Uses a scanline algorithm by interpolating the left and right edge of the lines. 
     * draws the upper part and then the lower part of the triangle. 
     * This method is much faster at drawing triangles than Graphics' fillPolygon() method.
     * @param p1 point 1 
     * @param p2 point 2
     * @param p3 point 3 
     * @param rgb the color of the triangle using rgb
     */
    private void paintTriangle(Point p1, Point p2, Point p3, int rgb)
    {
        Point high = p1;
        Point middle = p2;
        Point low = p3;

        //note that the highest point will actually have the lower y value because 0,0 in the screen
        //is the top left corner. 
        if (p1.y >= p2.y || p2.y >= p3.y) //checks if the sequence isnt p1, p2, p3
        {
            if (p1.y < p2.y)
            {
                if (p3.y < p1.y)
                {
                    high = p3;
                    middle = p1;
                    low = p2;
                }
                else
                {
                    middle = p3;
                    low = p2;
                }
            }
            else
            {
                if (p2.y < p3.y)
                {
                    high = p2;
                    if (p1.y < p3.y)
                    {
                        middle = p1;
                    }
                    else
                    {
                        middle = p3;
                        low = p1;
                    }
                }
                else
                {
                    high = p3;
                    low = p1;
                }
            }
        }

        //the y-level of the horizontal line being drawn
        int yScanLine;
        //the left or right bounds of the line being drawn
        int scanlineEdge1, scanlineEdge2;

        //Top part of triangle: 
        if (middle.y-high.y != 0 && low.y-high.y != 0)
        {
            //conditionals to account for the cases where the slope of a line of the triangle is undefined/vertical.
            if (middle.x - high.x == 0)
            {
                scanlineEdge1 = Math.max(0, Math.min(renderImage.getWidth(), high.x));
                for (yScanLine = high.y; yScanLine < middle.y && yScanLine < renderImage.getHeight(); yScanLine ++)
                {
                    if (yScanLine >= 0)
                    {
                        scanlineEdge2 = Math.max(0, Math.min(renderImage.getWidth(), (int)((yScanLine-high.y)/((double)(low.y-high.y)/(low.x-high.x)) + high.x)));
                        int[] pixelData = new int[Math.abs(scanlineEdge1-scanlineEdge2)+1];
                        Arrays.fill(pixelData, rgb);
                        drawHorizontalLine(Math.min(scanlineEdge1, scanlineEdge2), yScanLine, pixelData);    
                    }
                }
            }
            else if (low.x-high.x == 0)
            {
                scanlineEdge2 = Math.max(0, Math.min(renderImage.getWidth(), high.x));
                for (yScanLine = high.y; yScanLine < middle.y && yScanLine < renderImage.getHeight(); yScanLine ++)
                {
                    if (yScanLine >= 0)
                    {
                        scanlineEdge1 = Math.max(0, Math.min(renderImage.getWidth(), (int)((yScanLine-high.y)/((double)(middle.y-high.y)/(middle.x-high.x)) + high.x)));
                        int[] pixelData = new int[Math.abs(scanlineEdge1-scanlineEdge2)+1];
                        Arrays.fill(pixelData, rgb);
                        drawHorizontalLine(Math.min(scanlineEdge1, scanlineEdge2), yScanLine, pixelData);    
                    }
                }
            }
            else
            {
                for (yScanLine = high.y; yScanLine < middle.y && yScanLine < renderImage.getHeight(); yScanLine ++)
                {
                    if (yScanLine >= 0)
                    {
                        scanlineEdge1 = Math.max(0, Math.min(renderImage.getWidth(), (int)((yScanLine-high.y)/((double)(middle.y-high.y)/(middle.x-high.x)) + high.x)));
                        scanlineEdge2 = Math.max(0, Math.min(renderImage.getWidth(), (int)((yScanLine-high.y)/((double)(low.y-high.y)/(low.x-high.x)) + high.x)));

                        int[] pixelData = new int[Math.abs(scanlineEdge1-scanlineEdge2)+1];
                        Arrays.fill(pixelData, rgb);
                        drawHorizontalLine(Math.min(scanlineEdge1, scanlineEdge2), yScanLine, pixelData);    
                    }
                }
            }
        }

        //bottom part of triangle: 
        if (low.y-middle.y != 0 && low.y-high.y != 0)
        {
            //conditionals to account for the cases where the slope of a line of the triangle is vertical.
            if (low.x-middle.x == 0)
            {
                scanlineEdge1 = Math.max(0, Math.min(renderImage.getWidth(), middle.x));
                for (yScanLine = middle.y; yScanLine < low.y && yScanLine < renderImage.getHeight(); yScanLine ++)
                {
                    if (yScanLine >= 0)
                    {
                        scanlineEdge2 = Math.max(0, Math.min(renderImage.getWidth(), (int)((yScanLine-low.y)/((double)(low.y-high.y)/(low.x-high.x)) + low.x)));
                        int[] pixelData = new int[Math.abs(scanlineEdge1-scanlineEdge2)+1];
                        Arrays.fill(pixelData, rgb);
                        drawHorizontalLine(Math.min(scanlineEdge1, scanlineEdge2), yScanLine, pixelData);    
                    }
                }
            }
            else if (low.x - high.x == 0)
            {
                scanlineEdge2 = Math.max(0, Math.min(renderImage.getWidth(), low.x));
                for (yScanLine = middle.y; yScanLine < low.y && yScanLine < renderImage.getHeight(); yScanLine ++)
                {
                    if (yScanLine >= 0)
                    {
                        scanlineEdge1 = Math.max(0, Math.min(renderImage.getWidth(), (int)((yScanLine-low.y)/((double)(low.y-middle.y)/(low.x-middle.x)) + low.x)));
                        int[] pixelData = new int[Math.abs(scanlineEdge1-scanlineEdge2)+1];
                        Arrays.fill(pixelData, rgb);
                        drawHorizontalLine(Math.min(scanlineEdge1, scanlineEdge2), yScanLine, pixelData);    
                    }
                }
            }
            else
            {
                for (yScanLine = middle.y; yScanLine < low.y && yScanLine < renderImage.getHeight(); yScanLine ++)
                {
                    if (yScanLine >= 0)
                    {
                        scanlineEdge1 = Math.max(0, Math.min(renderImage.getWidth(), (int)((yScanLine-low.y)/((double)(low.y-middle.y)/(low.x-middle.x)) + low.x)));
                        scanlineEdge2 = Math.max(0, Math.min(renderImage.getWidth(), (int)((yScanLine-low.y)/((double)(low.y-high.y)/(low.x-high.x)) + low.x)));

                        int[] pixelData = new int[Math.abs(scanlineEdge1-scanlineEdge2)+1];
                        Arrays.fill(pixelData, rgb);
                        drawHorizontalLine(Math.min(scanlineEdge1, scanlineEdge2), yScanLine, pixelData);    
                    }
                }
            }
        }
    }

    //draws a horizontal line with the given constraints and the specified integer rgb color.
    private void drawHorizontalLine(int startOFLineX, int levelY, int[] pixelColorData)
    {
        renderImage.getRaster().setDataElements(startOFLineX, levelY, pixelColorData.length, 1, pixelColorData);
    }

    //Triangle2D class stores 2d triangle data before it is painted on the buffered image.
    //implements comparable to optimize sorting. 
    private class Triangle2D
    {
        //three screen coordinate points. 
        public final Point p1;
        public final Point p2;
        public final Point p3;

        //the color
        public final int color;

        //overloaded constructor. 
        public Triangle2D(Point p1In, Point p2In, Point p3In, int colorIn, double triangle3DDistanceIn)
        {
            p1 = p1In;
            p2 = p2In;
            p3 = p3In;
            color = colorIn;
        }
    }
}   

