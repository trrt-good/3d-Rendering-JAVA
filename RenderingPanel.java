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
        if 
        (
            distanceToTriangle < camera.getFarClipDistancee() //is the triangle within the camera's render distance?
            && distanceToTriangle > camera.getNearClipDistance() //is the triangle far enough from the camera?
            && Vector3.dotProduct(Vector3.subtract(triangleCenter, camPos), camDirection) > 0 //is the triangle on the side that the camera is facing?
            && (Vector3.dotProduct(triangle.getPlane().normal, camDirection) < 0 || !triangle.getMesh().backFaceCulling()) //is the triangle facing away? 
        )
        {
            //create local variables: 

            //the screen coords of the triangle, to be determined by the rest of the method.
            Point p1ScreenCoords = new Point();
            Point p2ScreenCoords = new Point();
            Point p3ScreenCoords = new Point();
            //boolean default false, but set true if just one of the verticies is within the camera's fov. 
            boolean shouldDrawTriangle = false;
            Vector3 triangleVertex1 = new Vector3(triangle.vertex1);
            Vector3 triangleVertex2 = new Vector3(triangle.vertex2);
            Vector3 triangleVertex3 = new Vector3(triangle.vertex3);

            triangleVertex1 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex1, camPos), camPos, renderPlane);
            Vector3 camCenterPoint = Vector3.getIntersectionPoint(camDirection, camPos, renderPlane);
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
    
            triangleVertex3 = new Vector3(triangle.vertex3);
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
        int rgb = convertToIntRGB(triangleColor);

        //sorts the three points by height using a very simple bubble sort algorithm
        if (p1.getY() > p2.getY())
        {
            tempPoint = p1;
            p1 = p2;
            p2 = tempPoint;
        }
        if (p2.getY() > p3.getY())
        {
            tempPoint = p2;
            p2 = p3;
            p3 = tempPoint;
        }
        if (p1.getY() > p2.getY())
        {
            tempPoint = p1;
            p1 = p2;
            p2 = tempPoint;
        }
        if (p2.getY() > p3.getY())
        {
            tempPoint = p2;
            p2 = p3;
            p3 = tempPoint;
        } 

        //the y-level of the horizontal line being drawn
        int yScanLine;
        //the left or right bounds of the line being drawn
        int edge1, edge2;

        //Top part of triangle: 
        if (p2.y-p1.y != 0 && p3.y-p1.y != 0)
        {
            //conditionals to account for the cases where the slope of a line of the triangle is undefined/vertical.
            if (p2.x - p1.x == 0)
            {
                edge1 = Math.max(0, Math.min(renderImage.getWidth(), p1.x));
                for (yScanLine = p1.y; yScanLine < p2.y && yScanLine < renderImage.getHeight(); yScanLine ++)
                {
                    if (yScanLine >= 0)
                    {
                        edge2 = Math.max(0, Math.min(renderImage.getWidth(), (int)((yScanLine-p1.y)/((double)(p3.y-p1.y)/(p3.x-p1.x)) + p1.x)));
                        drawHorizontalLine(Math.min(edge1, edge2), Math.max(edge1, edge2), yScanLine, rgb);
                    }
                }
            }
            else if (p3.x-p1.x == 0)
            {
                edge2 = Math.max(0, Math.min(renderImage.getWidth(), p1.x));
                for (yScanLine = p1.y; yScanLine < p2.y && yScanLine < renderImage.getHeight(); yScanLine ++)
                {
                    if (yScanLine >= 0)
                    {
                        edge1 = Math.max(0, Math.min(renderImage.getWidth(), (int)((yScanLine-p1.y)/((double)(p2.y-p1.y)/(p2.x-p1.x)) + p1.x)));
                        drawHorizontalLine(Math.min(edge1, edge2), Math.max(edge1, edge2), yScanLine, rgb);
                    }
                }
            }
            else
            {
                for (yScanLine = p1.y; yScanLine < p2.y && yScanLine < renderImage.getHeight(); yScanLine ++)
                {
                    if (yScanLine >= 0)
                    {
                        edge1 = Math.max(0, Math.min(renderImage.getWidth(), (int)((yScanLine-p1.y)/((double)(p2.y-p1.y)/(p2.x-p1.x)) + p1.x)));
                        edge2 = Math.max(0, Math.min(renderImage.getWidth(), (int)((yScanLine-p1.y)/((double)(p3.y-p1.y)/(p3.x-p1.x)) + p1.x)));
                        double w = (yScanLine-p1.y)/(p2.y-p1.y+1);
                        Vector3 edge1Weight = new Vector3(w, 1-w, 0);
                        w = (yScanLine-p1.y)/(p3.y-p1.y+1);
                        Vector3 edge2Weight = new Vector3(w, 0, 1-w);
                        Vector3[] rowWeights = new Vector3[Math.abs(edge2-edge1)+1];
                        for (int i = 0; i < rowWeights.length; i++)
                        {
                            if (rowWeights.length >1)
                                rowWeights[i] = Vector3.add(Vector3.multiply(edge1Weight, i/(double)(rowWeights.length-1)), Vector3.multiply(edge2Weight, (rowWeights.length-i)/(double)(rowWeights.length-1)));
                        }
                        if (rowWeights.length > 0 && rowWeights[0] != null)
                            drawHorizontalLine(Math.min(edge1, edge2), Math.max(edge1, edge2), rowWeights, yScanLine, rgb);
                        else 
                            drawHorizontalLine(Math.min(edge1, edge2), Math.max(edge1, edge2), yScanLine, rgb);
                    }
                }
            }
        }

        //bottom part of triangle: 
        if (p3.y-p2.y != 0 && p3.y-p1.y != 0)
        {
            //conditionals to account for the cases where the slope of a line of the triangle is vertical.
            if (p3.x-p2.x == 0)
            {
                edge1 = Math.max(0, Math.min(renderImage.getWidth(), p2.x));
                for (yScanLine = p2.y; yScanLine < p3.y && yScanLine < renderImage.getHeight(); yScanLine ++)
                {
                    if (yScanLine >= 0)
                    {
                        edge2 = Math.max(0, Math.min(renderImage.getWidth(), (int)((yScanLine-p3.y)/((double)(p3.y-p1.y)/(p3.x-p1.x)) + p3.x)));
                        drawHorizontalLine(Math.min(edge1, edge2), Math.max(edge1, edge2), yScanLine, rgb);
                    }
                }
            }
            else if (p3.x - p1.x == 0)
            {
                edge2 = Math.max(0, Math.min(renderImage.getWidth(), p3.x));
                for (yScanLine = p2.y; yScanLine < p3.y && yScanLine < renderImage.getHeight(); yScanLine ++)
                {
                    if (yScanLine >= 0)
                    {
                        edge1 = Math.max(0, Math.min(renderImage.getWidth(), (int)((yScanLine-p3.y)/((double)(p3.y-p2.y)/(p3.x-p2.x)) + p3.x)));
                        drawHorizontalLine(Math.min(edge1, edge2), Math.max(edge1, edge2), yScanLine, rgb);
                    }
                }
            }
            else
            {
                for (yScanLine = p2.y; yScanLine < p3.y && yScanLine < renderImage.getHeight(); yScanLine ++)
                {
                    if (yScanLine >= 0)
                    {
                        edge1 = Math.max(0, Math.min(renderImage.getWidth(), (int)((yScanLine-p3.y)/((double)(p3.y-p2.y)/(p3.x-p2.x)) + p3.x)));
                        edge2 = Math.max(0, Math.min(renderImage.getWidth(), (int)((yScanLine-p3.y)/((double)(p3.y-p1.y)/(p3.x-p1.x)) + p3.x)));
                        drawHorizontalLine(Math.min(edge1, edge2), Math.max(edge1, edge2), yScanLine, rgb);
                    }
                }
            }
        }
    }



    //draws a horizontal line with the given constraints and the specified integer rgb color.
    private void drawHorizontalLine(int startOFLineX, int endOfLineX, int levelY, int rgb)
    {
        int[] pixelArray = new int[(Math.abs(endOfLineX-startOFLineX))];
        Arrays.fill(pixelArray, rgb);
        renderImage.getRaster().setDataElements(startOFLineX, levelY, Math.abs(endOfLineX-startOFLineX), 1, pixelArray);
    }

    //draws a horizontal line with the given constraints and the specified integer rgb color.
    private void drawHorizontalLine(int startOFLineX, int endOfLineX, Vector3[] barycentricWeights, int levelY, int rgb)
    {
        int[] colors = new int[barycentricWeights.length];
        for (int i = 0; i < barycentricWeights.length; i ++)
        {
            colors[i] = convertToIntRGB(new Color((int)(barycentricWeights[i].x*255), (int)(barycentricWeights[i].y*255), (int)(barycentricWeights[i].y*255)));
        }
        int[] pixelArray = new int[(Math.abs(endOfLineX-startOFLineX))];
        
        Arrays.fill(pixelArray, rgb);

        renderImage.getRaster().setDataElements(startOFLineX, levelY, Math.abs(endOfLineX-startOFLineX), 1, colors);
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
            return (o.triangle3DDistance > triangle3DDistance)? 1 : ((o.triangle3DDistance < triangle3DDistance)? -1 : 0);
        }
    }
}   


