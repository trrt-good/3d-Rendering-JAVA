package src.gameObject;
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

import src.graphics.Lighting;
import src.primitives.Quaternion;
import src.primitives.Triangle;
import src.primitives.Vector2;
import src.primitives.Vector3;
import src.Main;

//a class for storing groups of triangles in a mesh.
public class Mesh 
{
    //a collection of all the triangles in the mesh.
    private ArrayList<Triangle> triangles;
    private ArrayList<Vector3> vertices;

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
    public Mesh(String modelFileName, String textureFileName, Vector3 modelOffsetAmount, Quaternion modelOffsetRotation, double scale, boolean shaded, boolean shouldBackFaceCull)
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

        vertices = new ArrayList<Vector3>();
        triangles = new ArrayList<Triangle>();
        shading = shaded;
        backFaceCull = shouldBackFaceCull;
        baseColor = Color.MAGENTA;
        totalMovement = Vector3.zero();
        
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

    public Mesh(String modelFileName, Color color, Vector3 modelOffsetAmount, Quaternion modelOffsetRotation, double scale, boolean shaded, boolean shouldBackFaceCull)
    {
        long start = System.nanoTime();
        texture = null;
        textureRaster = null;
        vertices = new ArrayList<Vector3>();
        triangles = new ArrayList<Triangle>();
        shading = shaded;
        backFaceCull = shouldBackFaceCull;
        baseColor = (color == null)? Color.MAGENTA : color;
        totalMovement = Vector3.zero();
        
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
    public void rotate(Quaternion quaternion, Vector3 centerOfRotation)
    {
        for (int i = 0; i < vertices.size(); i ++)
        {
            vertices.get(i).set(Vector3.add(Vector3.rotate(Vector3.subtract(vertices.get(i), centerOfRotation), quaternion), centerOfRotation));
        }
    }

    //translates each triangle in the mesh by "amount" 
    public void translate(Vector3 amount)
    {
        for (int i = 0; i < vertices.size(); i ++)
        {
            vertices.get(i).set(Vector3.add(vertices.get(i), amount));
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

    public Raster getTextureRaster()
    {
        return textureRaster;
    }

    public ArrayList<Triangle> getTriangles()
    {
        return triangles;
    }

    public ArrayList<Vector3> getVertices()
    {
        return vertices;
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
    private void createTriangles(String fileName, Vector3 offsetPosition, Quaternion offsetOrientation, double scale)
    {

        //vertices are temporarily stored before they are conbined into triangles and added into the main
        //triangle list.
        ArrayList<Vector2> textureCoords = new ArrayList<Vector2>();
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
                //v means Vector3 in .obj files
                if (line.startsWith("v "))
                {
                    StringTokenizer lineTokens = new StringTokenizer(line);
                    lineTokens.nextToken();
                    //create the Vector3 object
                    Vector3 vertexCoordinate = new Vector3(Double.parseDouble(lineTokens.nextToken()), Double.parseDouble(lineTokens.nextToken()), Double.parseDouble(lineTokens.nextToken()));

                    //apply transformations to the Vector3 based on offset params
                    vertexCoordinate = Vector3.rotate(vertexCoordinate, offsetOrientation);
                    vertexCoordinate = Vector3.add(offsetPosition, vertexCoordinate);
                    vertexCoordinate = Vector3.multiply(vertexCoordinate, scale);

                    //adds the Vector3 to the array of vertices
                    vertices.add(vertexCoordinate);
                }

                //vt means Vector3 texture coordinates.
                if (texture!= null && line.startsWith("vt "))
                {
                    StringTokenizer tokens = new StringTokenizer(line);
                    tokens.nextToken();
                    textureCoords.add(new Vector2(Double.parseDouble(tokens.nextToken()), Double.parseDouble(tokens.nextToken())));
                }

                //f means face in .obj files
                if (line.startsWith("f "))
                {
                    StringTokenizer lineTokens = new StringTokenizer(line);
                    lineTokens.nextToken();
                    int tokenLength = lineTokens.countTokens();
                    int[] coordinateIndexes = new int[tokenLength];
                    int[] textureIndexes = new int[tokenLength]; 
                    String[] tempArr;

                    Color color = baseColor;
                    for (int i = 0; i < tokenLength; i ++)
                    {
                        tempArr = lineTokens.nextToken().split("/");
                        coordinateIndexes[i] = Integer.parseInt(tempArr[0])-1;
                        if (texture != null)
                        textureIndexes[i] = Integer.parseInt(tempArr[1])-1;
                    }
                    
                    //create triangles based on the indicated verticies. However often verticies are not in sets of 3, so create multiple triangles if necessary.
                    for (int i = 0; i < coordinateIndexes.length - 2; i ++)
                    {   
                        if (texture == null)
                            triangles.add(new Triangle(this, vertices.get(coordinateIndexes[0]), vertices.get(coordinateIndexes[i+1]), vertices.get(coordinateIndexes[i+2]), color));
                        else
                            triangles.add
                            (
                                new Triangle
                                (
                                    this, 
                                    vertices.get(coordinateIndexes[0]), 
                                    vertices.get(coordinateIndexes[i+1]), 
                                    vertices.get(coordinateIndexes[i+2]), 
                                    textureCoords.get(textureIndexes[0]),
                                    textureCoords.get(textureIndexes[i+1]),
                                    textureCoords.get(textureIndexes[i+2])
                                )
                            );
                    }
                }
            }
        }
    }
}
