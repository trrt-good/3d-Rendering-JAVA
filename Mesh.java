import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import javax.imageio.ImageIO;

//a class for storing groups of triangles in a mesh.
public class Mesh 
{
    //a collection of all the triangles in the mesh.
    private ArrayList<Triangle> triangles;

    //the color of all the triangles of the mesh.
    private Color baseColor;

    //should the mesh be effected by lighting?
    private boolean shading;

    //a sum of all translations
    private Vector3 totalMovement;

    //the lighting object which was used last to recalculate lighting
    private Lighting lighting;

    //the texture applied to the mesh
    private BufferedImage texture; 
    private Raster textureRaster;

    //should the back face of the mesh be rendered? (keeping enabled greatly increases preformance, roughly 2x faster)*
    //*however, due to a poor implementation of backFaceCulling, for some cases, it is recommended to dissable this, 
    //which will may sacrafice preformance (especially for larger models), however it will mitigate the
    //strange visual effects that it may cause for certain models. 
    private boolean backFaceCull = true;

    //overloaded constructor takes in the name of the model, transform offsets, color and the boolean values 
    public Mesh(String modelFileName, String textureFileName, Vector3 modelOffsetAmount, EulerAngle modelOffsetRotation, double scale, Color colorIn, boolean shaded, boolean shouldBackFaceCull)
    {
        long start = System.nanoTime();
        texture = null;
        try
        {
            if (textureFileName != null)
                texture = ImageIO.read(new File(Main.resourcesDirectory, textureFileName));
        } 
        catch (IOException e)
        {
            System.err.println("ERROR at: Mesh/constructor:\n\tError while loading texture: " + textureFileName);
        }
        if (texture != null)
            textureRaster = texture.getData();

        triangles = new ArrayList<Triangle>();
        shading = shaded;
        backFaceCull = shouldBackFaceCull;
        baseColor = (colorIn == null)? Color.MAGENTA : colorIn;
        totalMovement = new Vector3();
        
        if (modelFileName.endsWith(".obj"))
        {
            createTriangles(modelFileName, modelOffsetAmount, modelOffsetRotation, scale);
        }
        else
        {
            System.err.println("ERROR at: Mesh/constructor:\n\tUnsupported 3d model file type. Please use .obj files");
        }
        System.out.println("mesh created: " + modelFileName + " in " + (System.nanoTime() - start)/1000000 + "ms\n\t- " + triangles.size() + " triangles");
    }

    protected Mesh(boolean shadedIn, boolean shouldBackFaceCull)
    {        
        shading = shadedIn;
        backFaceCull = shouldBackFaceCull;
        triangles = new ArrayList<Triangle>();
    }

    //rotates each triangle in the mesh according to a rotation matrix, and around the center of rotation.
    public void rotate(Matrix3x3 rotationMatrix, Vector3 centerOfRotation)
    {
        for (int i = 0; i < triangles.size(); i ++)
        {
            triangles.get(i).point1 = Vector3.add(Vector3.applyMatrix(rotationMatrix, Vector3.subtract(triangles.get(i).point1, centerOfRotation)), centerOfRotation);
            triangles.get(i).point2 = Vector3.add(Vector3.applyMatrix(rotationMatrix, Vector3.subtract(triangles.get(i).point2, centerOfRotation)), centerOfRotation);
            triangles.get(i).point3 = Vector3.add(Vector3.applyMatrix(rotationMatrix, Vector3.subtract(triangles.get(i).point3, centerOfRotation)), centerOfRotation);
        }
    }

    //translates each triangle in the mesh by "amount" 
    public void translate(Vector3 amount)
    {
        for (int i = 0; i < triangles.size(); i ++)
        {
            triangles.get(i).point1 = Vector3.add(triangles.get(i).point1, amount);
            triangles.get(i).point2 = Vector3.add(triangles.get(i).point2, amount);
            triangles.get(i).point3 = Vector3.add(triangles.get(i).point3, amount);
        }
        totalMovement = Vector3.add(totalMovement, amount);
    }

    //#region getter methods 
    public boolean isShaded()
    {
        return shading;
    }

    public boolean backFaceCulling()
    {
        return backFaceCull;
    }

    public ArrayList<Triangle> getTriangles()
    {
        return triangles;
    }
    //#endregion

    //calculates the lighting of each triangle in the mesh based off the given
    //lighting object
    public void calculateLighting(Lighting lightingIn)
    {
        if (shading)
        {
            for (int i = 0; i < triangles.size(); i++)
            {
                triangles.get(i).calculateLightingColor(lightingIn);
            }
        }
        lighting = lightingIn;
    }

    //refreshes the lighting based on the stored lighting object. 
    public void refreshLighting()
    {
        if (shading)
        {
            for (int i = 0; i < triangles.size(); i++)
            {
                triangles.get(i).calculateLightingColor(lighting);
            }
        }
    }

    //reads a .obj file (a text file) and stores triangles inside the triangle list. 
    private void createTriangles(String fileName, Vector3 offsetPosition, EulerAngle offsetOrientation, double scale)
    {
        Matrix3x3 offsetRotationMatrix = Matrix3x3.eulerRotation(offsetOrientation);
        //vertices are temporarily stored before they are conbined into triangles and added into the main
        //triangle list.
        ArrayList<Vector3> vertices = new ArrayList<Vector3>();
        ArrayList<Double> textureCoordsX = new ArrayList<Double>();
        ArrayList<Double> textureCoordsY = new ArrayList<Double>();
        Scanner scanner;
        String line = "";

        //innitialize the scanner
        try 
        {
            scanner = new Scanner(new File(Main.resourcesDirectory, fileName));   
        } 
        catch (FileNotFoundException e) 
        {
            System.err.println("ERROR at: Mesh/readObjFile() method:\n\tfile " + fileName + " not found in " + Main.resourcesDirectory.getAbsolutePath());
            return;
        }

        //scanner goes through the file
        while(scanner.hasNextLine())
        {
            line = scanner.nextLine();

            if (!line.equals(""))
            {
                //v means vertex in .obj files
                if (line.startsWith("v "))
                {
                    StringTokenizer lineTokens = new StringTokenizer(line);
                    lineTokens.nextToken();
                    //create the vertex object
                    Vector3 vertex = new Vector3(Double.parseDouble(lineTokens.nextToken()), Double.parseDouble(lineTokens.nextToken()), Double.parseDouble(lineTokens.nextToken()));

                    //apply transformations to the vertex based on offset params
                    vertex = Vector3.applyMatrix(offsetRotationMatrix, vertex);
                    vertex = Vector3.add(offsetPosition, vertex);
                    vertex = Vector3.multiply(vertex, scale);

                    //adds the vertex to the array of vertices
                    vertices.add(vertex);
                }

                //vt means vertex texture coordinates.
                if (texture!= null && line.startsWith("vt "))
                {
                    StringTokenizer tokens = new StringTokenizer(line);
                    tokens.nextToken();
                    textureCoordsX.add(Double.parseDouble(tokens.nextToken()));
                    textureCoordsY.add(Double.parseDouble(tokens.nextToken()));
                }

                //f means face in .obj files
                if (line.startsWith("f "))
                {
                    StringTokenizer lineTokens = new StringTokenizer(line);
                    lineTokens.nextToken();
                    int tokenLength = lineTokens.countTokens();
                    int[] vertexIndexes = new int[tokenLength];
                    int[] textureIndexes = new int[tokenLength]; 
                    String[] tempArr;

                    Color color = baseColor;
                    for (int i = 0; i < tokenLength; i ++)
                    {
                        tempArr = lineTokens.nextToken().split("/");
                        vertexIndexes[i] = Integer.parseInt(tempArr[0])-1;
                        if (texture != null)
                        textureIndexes[i] = Integer.parseInt(tempArr[1])-1;
                    }
                    
                    //create triangles based on the indicated verticies. However often verticies are not in sets of 3, so create multiple triangles if necessary.
                    for (int i = 0; i < vertexIndexes.length - 2; i ++)
                    {
                        if (texture != null)
                        color = calculateTriangleBaseColor
                        (
                            textureCoordsX.get(textureIndexes[0]), textureCoordsY.get(textureIndexes[0]), 
                            textureCoordsX.get(textureIndexes[i + 1]), textureCoordsY.get(textureIndexes[i + 1]), 
                            textureCoordsX.get(textureIndexes[i + 2]), textureCoordsY.get(textureIndexes[i + 2])
                        );
                        triangles.add(new Triangle(this, vertices.get(vertexIndexes[0]), vertices.get(vertexIndexes[i + 1]), vertices.get(vertexIndexes[i +2]), color));
                    } 
                }
            }
        }
    }

    private Color calculateTriangleBaseColor(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        double centerX = (x1 + x2 + x3)/3;
        double centerY = (y1 + y2 + y3)/3;
        int[] color = new int[4];
        color = textureRaster.getPixel((int)(centerX*texture.getWidth()), texture.getHeight() - (int)(centerY*texture.getHeight()), color);
        return new Color(color[0], color[1], color[2]);
    }
}
