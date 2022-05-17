import javax.lang.model.util.ElementScanner6;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.awt.Color;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Graphics;

import java.awt.image.BufferedImage;

public class RenderingPanel extends JPanel implements Runnable
{
    //collection of all the objects that the rendering panel will render
    private ArrayList<Mesh> meshes = new ArrayList<Mesh>(); 
    private ArrayList<Triangle> triangles = new ArrayList<Triangle>(); 

    //for rendering:
    private BufferedImage renderImage;
    private Color backgroundColor;
    private Plane renderPlane;        
    private int[] blankImagePixelColorData;
    private ArrayList<Triangle2D> drawQeue;
    private Matrix3x3 pointRotationMatrix;
    private double pixelsPerUnit;
    private Vector3 camCenterPoint;


    //Threads:
    private Thread renderingThread;
    private boolean threadRunning;
    private int fps;

    //Camera:
    private Camera camera;
    private Vector3 camDirection;
    private Vector3 camPos;
    private double renderPlaneWidth;


    //lighting:
    private Lighting lightingObject; 
    
    //fog:
    private double fogStartDistance;
    private double fullFogDistance;
    private boolean fogEnabled = false;
    private Color fogColor;

    //used to help with optimizations:
    private TimingHelper totalFrameTime = new TimingHelper("totalFrameTime");
    private TimingHelper trianglesOrderTime = new TimingHelper("triangleOrderTime");
    private TimingHelper trianglesCalculateTime = new TimingHelper("trianglesCalculateTime");
    private TimingHelper trianglesPaintTime = new TimingHelper("trianglesPaintTime");

    public RenderingPanel(int width, int height)
    {
        setPreferredSize(new Dimension(width, height));

        //background color: 
        backgroundColor = new Color(200, 220, 255);

        //innitialize fields 
        camera = null;
        lightingObject = null;
        meshes = new ArrayList<Mesh>();
        triangles = new ArrayList<Triangle>();
        drawQeue = new ArrayList<Triangle2D>();
        camDirection = new Vector3();   
        camPos = new Vector3();
        fps = -1;
        
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
        if (meshes.size() > 0 && camera != null)
        {
            computeTriangles();
            sortTriangles();
            drawBufferedImage();
            g.drawImage(renderImage, 0, 0, this);
        }
        //fps counter 
        g.drawString("fps: " + (int)(1000/totalFrameTime.getDeltaTime()), 30, 30);
    }

    public void setFPSlimit(int limit)
    {
        fps = Math.max(0, limit);
    }

    //sets the lighting, which updates the lighting of all meshes. 
    public void setLighting(Lighting lighting)
    {
        if (lighting == null)
        {
            System.err.println("WARNING at: RenderingPanel/setLighting() method: \n\tlighting is null, lighting not set");
            return;
        }
        lightingObject = lighting;
        lightingObject.update(meshes);
    }

    //adds a mesh to be rendered, as well as updating it's lighting
    public void addMesh(Mesh mesh)
    {
        if (mesh != null)
        {
            meshes.add(mesh);
            if (lightingObject != null)
                lightingObject.update(meshes);
            triangles.addAll(mesh.getTriangles());
        }
        else
        {
            System.err.println("WARNING at: RenderingPanel/addMesh() method: \n\tmesh is null, triangles not added");
        }
    }

    //sets the camera 
    public void setCamera(Camera camIn)
    {
        if (camIn == null)
        {
            System.err.println("WARNING at: RenderingPanel/setCamera() method: \n\tcamera is null, camera not set");
            return;
        }
        camera = camIn;
        renderPlaneWidth = camera.getRenderPlaneWidth();
        renderPlane = new Plane(Vector3.add(Vector3.multiply(camDirection, camera.getRenderPlaneDistance()), camera.getPosition()), camDirection);;
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

    public void computeTriangles()
    {
        trianglesCalculateTime.startClock();
        pixelsPerUnit = getWidth()/renderPlaneWidth;
        renderPlaneWidth = camera.getRenderPlaneWidth();
        camPos = camera.getPosition();
        camDirection = camera.getDirectionVector();
        camCenterPoint = Vector3.add(Vector3.multiply(camDirection, camera.getRenderPlaneDistance()), camPos);
        renderPlane = new Plane(Vector3.add(Vector3.multiply(camDirection, camera.getRenderPlaneDistance()), camPos), camDirection);
        pointRotationMatrix = Matrix3x3.multiply(Matrix3x3.rotationMatrixAxisX(camera.getVorientation()*0.017453292519943295), Matrix3x3.rotationMatrixAxisY(-camera.getHorientation()*0.017453292519943295));
        
        drawQeue.clear();
        for (int i = 0; i < triangles.size(); i ++)
        {
            calculateTriangle(triangles.get(i));
        }

        trianglesCalculateTime.stopClock();
    }

    public void sortTriangles()
    {
        trianglesOrderTime.startClock();
        Collections.sort(drawQeue);
        trianglesOrderTime.stopClock();
    }

    public void drawBufferedImage()
    {
        trianglesPaintTime.startClock();
        renderImage.getRaster().setDataElements(0, 0, renderImage.getWidth(), renderImage.getHeight(), blankImagePixelColorData);
        for (int i = 0; i < drawQeue.size(); i++)
        {
            Triangle2D triangle2d = drawQeue.get(i);
            paintTriangle(triangle2d.p1, triangle2d.p2, triangle2d.p3, triangle2d.color);
        }
        trianglesPaintTime.stopClock();
    }

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

        if (Vector3.dotProduct(triangle.getPlane().normal, Vector3.subtract(triangleCenter, camPos).getNormalized()) > 0 && Vector3.dotProduct(Vector3.subtract(triangleCenter, camPos), camDirection) <= 0 && distanceToTriangle >= camera.getFarClipDistancee())
            return;
        
        Vector3 triangleVertex1 = new Vector3(triangle.vertex1);
        Vector3 triangleVertex2 = new Vector3(triangle.vertex2);
        Vector3 triangleVertex3 = new Vector3(triangle.vertex3);

        if (distanceToTriangle < camera.getNearClipDistance()*1.2)
        {
            Plane nearClipPlane = new Plane(Vector3.add(camPos, Vector3.multiply(camDirection, camera.getNearClipDistance())), camDirection);
            boolean v1TooClose = false;
            boolean v2TooClose = false;
            boolean v3TooClose = false;
    
            if (Vector3.dotProduct(nearClipPlane.normal, Vector3.subtract(triangleVertex1, nearClipPlane.pointOnPlane)) < 0)
                v1TooClose = true;
            if (Vector3.dotProduct(nearClipPlane.normal, Vector3.subtract(triangleVertex2, nearClipPlane.pointOnPlane)) < 0)
                v2TooClose = true;
            if (Vector3.dotProduct(nearClipPlane.normal, Vector3.subtract(triangleVertex3, nearClipPlane.pointOnPlane)) < 0)
                v3TooClose = true;
    
            if (v1TooClose && v2TooClose && v3TooClose)
                return;
    
            clipping: 
            {
                if (v1TooClose)
                {
                    if (v2TooClose)
                    {
                        triangleVertex1 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex1, triangleVertex3), triangleVertex3, nearClipPlane);
                        triangleVertex2 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex2, triangleVertex3), triangleVertex3, nearClipPlane);
                        
                        break clipping;
                    }
                    else if (v3TooClose)
                    {
                        triangleVertex1 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex1, triangleVertex2), triangleVertex2, nearClipPlane);
                        triangleVertex3 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex3, triangleVertex2), triangleVertex2, nearClipPlane);
                        break clipping;
                    }
                    else 
                    {
                        Vector3 tempV = triangleVertex1;
                        triangleVertex1 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex1, triangleVertex2), triangleVertex2, nearClipPlane);
                        Triangle newTriangle = new Triangle(triangle.getMesh(), triangleVertex1, triangleVertex3, Vector3.getIntersectionPoint(Vector3.subtract(tempV, triangleVertex3), triangleVertex3, nearClipPlane), triangle.getColorWithLighting());
                        calculateTriangle(newTriangle);
                        break clipping;
                    }
                }
                else if (v2TooClose)
                {
                    if (v3TooClose)
                    {
                        triangleVertex2 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex2, triangleVertex1), triangleVertex1, nearClipPlane);
                        triangleVertex3 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex3, triangleVertex1), triangleVertex1, nearClipPlane);
                        break clipping;
                    }
                    else 
                    {
                        Vector3 tempV = triangleVertex2;
                        triangleVertex2 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex2, triangleVertex1), triangleVertex1, nearClipPlane);
                        Triangle newTriangle = new Triangle(triangle.getMesh(), triangleVertex3, triangleVertex2, Vector3.getIntersectionPoint(Vector3.subtract(tempV, triangleVertex3), triangleVertex3, nearClipPlane), triangle.getColorWithLighting());
                        calculateTriangle(newTriangle);
                        break clipping;
                    }
                }
                if (v3TooClose)
                {
                    Vector3 tempV = new Vector3(triangleVertex3);
                    triangleVertex3 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex3, triangleVertex2), triangleVertex2, nearClipPlane);
                    Triangle newTriangle = new Triangle(triangle.getMesh(), triangleVertex3, triangleVertex1, Vector3.getIntersectionPoint(Vector3.subtract(tempV, triangleVertex1), triangleVertex1, nearClipPlane), triangle.getColorWithLighting());
                    calculateTriangle(newTriangle);
                }
            }
        }
        //create local variables: 

        //the screen coords of the triangle, to be determined by the rest of the method.
        Point p1ScreenCoords = new Point();
        Point p2ScreenCoords = new Point();
        Point p3ScreenCoords = new Point();
        //boolean default false, but set true if just one of the verticies is within the camera's fov. 
        boolean shouldDrawTriangle = false;

        triangleVertex1 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex1, camPos), camPos, renderPlane);
        Vector3 rotatedPoint = Vector3.applyMatrix(pointRotationMatrix, Vector3.subtract(triangleVertex1, camCenterPoint));
        if ((Math.abs(rotatedPoint.x) < renderPlaneWidth/2*1.2 && Math.abs(rotatedPoint.y) < renderPlaneWidth*((double)getHeight()/(double)getWidth())/2*1.2))
            shouldDrawTriangle = true;
        p1ScreenCoords.x = (int)(getWidth()/2 + rotatedPoint.x*pixelsPerUnit);
        p1ScreenCoords.y = (int)(getHeight()/2 - rotatedPoint.y*pixelsPerUnit);

        triangleVertex2 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex2, camPos), camPos, renderPlane);
        rotatedPoint = Vector3.applyMatrix(pointRotationMatrix, Vector3.subtract(triangleVertex2, camCenterPoint));
        if ((Math.abs(rotatedPoint.x) < renderPlaneWidth/2*1.2 && Math.abs(rotatedPoint.y) < renderPlaneWidth*((double)getHeight()/getWidth())/2*1.2))
            shouldDrawTriangle = true;
        p2ScreenCoords.x = (int)(getWidth()/2 + rotatedPoint.x*pixelsPerUnit);
        p2ScreenCoords.y = (int)(getHeight()/2 - rotatedPoint.y*pixelsPerUnit);

        triangleVertex3 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex3, camPos), camPos, renderPlane);
        rotatedPoint = Vector3.applyMatrix(pointRotationMatrix, Vector3.subtract(triangleVertex3, camCenterPoint));
        if ((Math.abs(rotatedPoint.x) < renderPlaneWidth/2*1.2 && Math.abs(rotatedPoint.y) < renderPlaneWidth*((double)getHeight()/getWidth())/2*1.2))
            shouldDrawTriangle = true;
        p3ScreenCoords.x = (int)(getWidth()/2 + rotatedPoint.x*pixelsPerUnit);
        p3ScreenCoords.y = (int)(getHeight()/2 - rotatedPoint.y*pixelsPerUnit);

        if (shouldDrawTriangle)
        {
            Color colorUsed;
            if (triangle.getMesh() != null && triangle.getMesh().isShaded())
            {
                Color litColor = triangle.getColorWithLighting();
                if (fogEnabled && distanceToTriangle > fogStartDistance)
                {
                    Color triangleColor;
                    if (distanceToTriangle > fullFogDistance)
                        triangleColor = fogColor;
                    else
                    {
                        //skews the triangle's color closer to the fog color as a function of distance. 
                        double fogAmt = (distanceToTriangle-fogStartDistance)/(fullFogDistance-fogStartDistance);
                        int red = litColor.getRed() + (int)((fogColor.getRed()-litColor.getRed())*fogAmt*fogAmt);
                        int green = litColor.getGreen() + (int)((fogColor.getGreen()-litColor.getGreen())*fogAmt*fogAmt);
                        int blue = litColor.getBlue() + (int)((fogColor.getBlue()-litColor.getBlue())*fogAmt*fogAmt);

                        //clamps color values to between 0 and 255
                        red = Math.max(0, Math.min(255, red));
                        green = Math.max(0, Math.min(255, green));
                        blue = Math.max(0, Math.min(255, blue));
                        triangleColor = new Color(red, green, blue);
                    }
                    colorUsed = triangleColor;
                }
                else 
                    colorUsed = litColor;
            }   
            else 
                colorUsed = triangle.getBaseColor();
            if (colorUsed == null)
            {
                colorUsed = Color.MAGENTA;
            }
            //adds the 2d triangle object into the triangle2d array.
            drawQeue.add(new Triangle2D(p1ScreenCoords, p2ScreenCoords, p3ScreenCoords, colorUsed, distanceToTriangle));
        }
    }

    //returns the integer rgb value of a color, which is used for buffered images. 
    private int convertToIntRGB(Color color)
    {
        return 65536 * color.getRed() + 256 * color.getGreen() + color.getBlue();
    }

    //paints a solid triangle on the buffered image with verticies at "p1", "p2" and "p3". 
    //rasterization algorithm starts at the top point, then draws horizontal lines from one 
    //edge of the triangle to the other (using a simple slope-intercept equation), first 
    //drawing the upper part and then the lower part of the triangle. 
    //This method is much faster at drawing triangles than Graphics' fillPolygon() method.
    private void paintTriangle(Point p1, Point p2, Point p3, Color triangleColor)
    {
        Point tempPoint = new Point();
        int tempIndex = 0;
        int rgb = convertToIntRGB(triangleColor);

        Point high = p1;
        Point middle = p2;
        Point low = p3;

        int highIndex = 0;
        int middleIndex = 1;
        int lowIndex = 2;        

        //-1 means p2 is on the left of line p1 -> p3
        // 1 means p2 is on the right of line p1 -> p3
        int type = 0;

        //sorts the three points by height using a very simple bubble sort algorithm
        if (high.getY() > middle.getY())
        {
            tempPoint = high;
            high = middle;
            middle = tempPoint;

            tempIndex = highIndex;
            highIndex = middleIndex;
            middleIndex = tempIndex;
        }
        if (middle.getY() > low.getY())
        {
            tempPoint = middle;
            middle = low;
            low = tempPoint;

            tempIndex = middleIndex;
            middleIndex = lowIndex;
            lowIndex = tempIndex;
        }
        if (high.getY() > middle.getY())
        {
            tempPoint = high;
            high = middle;
            middle = tempPoint;

            tempIndex = highIndex;
            highIndex = middleIndex;
            middleIndex = tempIndex;
        }
        if (middle.getY() > low.getY())
        {
            tempPoint = middle;
            middle = low;
            low = tempPoint;

            tempIndex = middleIndex;
            middleIndex = lowIndex;
            lowIndex = tempIndex;
        } 

        if (low.y-high.y != 0)
        {
            type = (((middle.y-high.y)*(low.x-high.x))/(low.y-high.y) + high.x > middle.x)? -1 : 1;
        }

        //the y-level of the horizontal line being drawn
        int yScanLine;
        //the left or right bounds of the line being drawn
        int scanlineEdge1, scanlineEdge2;

        // double[] scanlineEdge1Weight;
        // double[] scanlineEdge2Weight;

        // //x in the vector represents p1 weight, y is p2 weight, z is p3 weight

        // double[][] leftEdgeWeights = new double[low.y-high.y + 1][3];
        // double[][] rightEdgeWeights = new double[low.y-high.y + 1][3];

        // if (type == -1)
        // {
        //     int midLowDistance = leftEdgeWeights.length - (low.y - middle.y);
        //     int topMidDistance = leftEdgeWeights.length - (middle.y-high.y)-1;
        //     for (double i = 0; i < topMidDistance; i++)
        //     {
        //         leftEdgeWeights[(int)i][highIndex] = 1 - i/topMidDistance;
        //         leftEdgeWeights[(int)i][middleIndex] = i/topMidDistance;
        //         leftEdgeWeights[(int)i][lowIndex] = 0;
        //     }
        //     for (double i = 0; i < midLowDistance; i++)
        //     {
        //         leftEdgeWeights[(int)i + topMidDistance][highIndex] = 0;
        //         leftEdgeWeights[(int)i + topMidDistance][middleIndex] = 1 - i/midLowDistance;
        //         leftEdgeWeights[(int)i + topMidDistance][lowIndex] = i/midLowDistance;
        //     }

        //     for (double i = 0; i < rightEdgeWeights.length; i++)
        //     {
        //         rightEdgeWeights[(int)i][highIndex] = 1 - i/rightEdgeWeights.length;
        //         rightEdgeWeights[(int)i][middleIndex] = 0;
        //         rightEdgeWeights[(int)i][lowIndex] = i/rightEdgeWeights.length;
        //     }
        // }
        // else
        // {
        //     int midLowDistance = leftEdgeWeights.length - (low.y - middle.y);
        //     int topMidDistance = leftEdgeWeights.length - (middle.y-high.y)-1;

        //     for (double i = 0; i < leftEdgeWeights.length; i++)
        //     {
        //         leftEdgeWeights[(int)i][lowIndex] = 1 - i/leftEdgeWeights.length;
        //         leftEdgeWeights[(int)i][highIndex] = 0;
        //         leftEdgeWeights[(int)i][middleIndex] = i/leftEdgeWeights.length;
        //     }

        //     for (double i = 0; i < topMidDistance; i++)
        //     {
        //         rightEdgeWeights[(int)i][lowIndex] = 1 - i/topMidDistance;
        //         rightEdgeWeights[(int)i][highIndex] = i/topMidDistance;
        //         rightEdgeWeights[(int)i][middleIndex] = 0;
        //     }
        //     for (double i = 0; i < midLowDistance; i++)
        //     {
        //         rightEdgeWeights[(int)i + topMidDistance][lowIndex] = 0;
        //         rightEdgeWeights[(int)i + topMidDistance][highIndex] = 1 - i/midLowDistance;
        //         rightEdgeWeights[(int)i + topMidDistance][middleIndex] = i/midLowDistance;
        //     }
        // }

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
                        int relativeScanLine = yScanLine-high.y;
                        scanlineEdge2 = Math.max(0, Math.min(renderImage.getWidth(), (int)((relativeScanLine)/((double)(low.y-high.y)/(low.x-high.x)) + high.x)));
                        int[] pixelData = new int[Math.abs(scanlineEdge1-scanlineEdge2)+1];
                        Arrays.fill(pixelData, rgb);
                        drawHorizontalLine(Math.min(scanlineEdge1, scanlineEdge2), Math.max(scanlineEdge1, scanlineEdge2), yScanLine, pixelData);                    
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
                        int relativeScanLine = yScanLine-high.y;
                        scanlineEdge1 = Math.max(0, Math.min(renderImage.getWidth(), (int)((relativeScanLine)/((double)(middle.y-high.y)/(middle.x-high.x)) + high.x)));
                        int[] pixelData = new int[Math.abs(scanlineEdge1-scanlineEdge2)+1];
                        Arrays.fill(pixelData, rgb);
                        drawHorizontalLine(Math.min(scanlineEdge1, scanlineEdge2), Math.max(scanlineEdge1, scanlineEdge2), yScanLine, pixelData);                    
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

                        // scanlineEdge1Weight = leftEdgeWeights[leftEdgeWeights.length-(yScanLine-high.y)-1];
                        // scanlineEdge2Weight = rightEdgeWeights[rightEdgeWeights.length-(yScanLine-high.y)-1];

                        // int[] pixelData = new int[Math.abs(scanlineEdge1-scanlineEdge2)+1];

                        // for (int i = 0; i < pixelData.length; i ++)
                        // {
                        //     double[] barycentricCoord = new double[]
                        //     {
                        //         (scanlineEdge1Weight[0]*(pixelData.length-i) + scanlineEdge2Weight[0]*i)/pixelData.length,
                        //         (scanlineEdge1Weight[1]*(pixelData.length-i) + scanlineEdge2Weight[1]*i)/pixelData.length,
                        //         (scanlineEdge1Weight[2]*(pixelData.length-i) + scanlineEdge2Weight[2]*i)/pixelData.length,                                
                        //     };
                        //     pixelData[i] = convertToIntRGB(new Color((int)(barycentricCoord[0]*255), (int)(barycentricCoord[1]*255), (int)(barycentricCoord[2]*255)));
                        // }
                        int[] pixelData = new int[Math.abs(scanlineEdge1-scanlineEdge2)+1];
                        Arrays.fill(pixelData, rgb);
                        drawHorizontalLine(Math.min(scanlineEdge1, scanlineEdge2), Math.max(scanlineEdge1, scanlineEdge2), yScanLine, pixelData);    
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
                        int relativeScanLine = yScanLine-middle.y;
                        scanlineEdge2 = Math.max(0, Math.min(renderImage.getWidth(), (int)((yScanLine-low.y)/((double)(low.y-high.y)/(low.x-high.x)) + low.x)));
                        int[] pixelData = new int[Math.abs(scanlineEdge1-scanlineEdge2)+1];
                        Arrays.fill(pixelData, rgb);
                        drawHorizontalLine(Math.min(scanlineEdge1, scanlineEdge2), Math.max(scanlineEdge1, scanlineEdge2), yScanLine, pixelData);                    
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
                        int relativeScanLine = yScanLine-middle.y;
                        scanlineEdge1 = Math.max(0, Math.min(renderImage.getWidth(), (int)((yScanLine-low.y)/((double)(low.y-middle.y)/(low.x-middle.x)) + low.x)));
                        int[] pixelData = new int[Math.abs(scanlineEdge1-scanlineEdge2)+1];
                        Arrays.fill(pixelData, rgb);
                        drawHorizontalLine(Math.min(scanlineEdge1, scanlineEdge2), Math.max(scanlineEdge1, scanlineEdge2), yScanLine, pixelData);                    
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


                        // scanlineEdge1Weight = leftEdgeWeights[leftEdgeWeights.length-(yScanLine-high.y)-1];
                        // scanlineEdge2Weight = rightEdgeWeights[rightEdgeWeights.length-(yScanLine-high.y)-1];

                        // int[] pixelData = new int[Math.abs(scanlineEdge1-scanlineEdge2)+1];

                        // for (int i = 0; i < pixelData.length; i ++)
                        // {
                        //     double[] barycentricCoord = new double[]
                        //     {
                        //         (scanlineEdge1Weight[0]*(pixelData.length-i) + scanlineEdge2Weight[0]*i)/pixelData.length,
                        //         (scanlineEdge1Weight[1]*(pixelData.length-i) + scanlineEdge2Weight[1]*i)/pixelData.length,
                        //         (scanlineEdge1Weight[2]*(pixelData.length-i) + scanlineEdge2Weight[2]*i)/pixelData.length,                                
                        //     };
                        //     pixelData[i] = convertToIntRGB(new Color((int)(barycentricCoord[0]*255), (int)(barycentricCoord[1]*255), (int)(barycentricCoord[2]*255)));
                        // }
                        int[] pixelData = new int[Math.abs(scanlineEdge1-scanlineEdge2)+1];
                        Arrays.fill(pixelData, rgb);
                        drawHorizontalLine(Math.min(scanlineEdge1, scanlineEdge2), Math.max(scanlineEdge1, scanlineEdge2), yScanLine, pixelData);    
                    }
                }
            }
        }
    }



    //draws a horizontal line with the given constraints and the specified integer rgb color.
    private void drawHorizontalLine(int startOFLineX, int endOfLineX, int levelY, int[] pixelColorData)
    {
        renderImage.getRaster().setDataElements(startOFLineX, levelY, Math.abs(endOfLineX-startOFLineX), 1, pixelColorData);
    }

    //Triangle2D class stores 2d triangle data before it is painted on the buffered image.
    //implements comparable to optimize sorting. 
    class Triangle2D implements Comparable<Triangle2D>
    {
        //three screen coordinate points. 
        public Point p1;
        public Point p2;
        public Point p3;

        //the color
        public Color color;

        //the distance from this triangle's corresponding 3d triangle to the camera. 
        //for the sole purpose of sorting triangles by distance, but rather than wasting 
        //compute to sort 3d triangles before the program knows wether or not they are 
        //even in view, the 2d triangles store this value as a way for the algorithm to 
        //sort triangles by distance later in the pipeline, which is much more efficient. 
        private double triangle3DDistance;

        //overloaded constructor. 
        public Triangle2D(Point p1In, Point p2In, Point p3In, Color colorIn, double triangle3DDistanceIn)
        {
            p1 = p1In;
            p2 = p2In;
            p3 = p3In;
            color = colorIn;
            triangle3DDistance = triangle3DDistanceIn;

        }

        //the compareTo method allows java.util.Collections to compare two Triangle2D 
        //objects with eachother. This allows the program to use java.util.Collections'
        //very fast sorting algorithm to sort triangles by distance. 
        public int compareTo(Triangle2D o) 
        {
            int num = 0;
            if (o.triangle3DDistance-triangle3DDistance < 0)
                num = -1;
            else if (o.triangle3DDistance-triangle3DDistance > 0)
                num = 1;
            return num;
        }

        private int fastfloor(double x) 
        {
            int xi = (int) x;
            return x < xi ? xi - 1 : xi;
        }
    }
}   


