import java.util.ArrayList;
import java.util.Scanner;

import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;

import java.awt.Color;
import java.awt.Image;

import javax.imageio.ImageIO;

//a class for storing groups of triangles in a mesh.
public class Mesh 
{
    //a collection of all the triangles in the mesh.
    private ArrayList<Triangle> triangles;

    //the color of all the triangles of the mesh.
    private Color color;

    //should the mesh be effected by lighting?
    private boolean shading;

    //a sum of all translations
    private Vector3 totalMovement;

    //the lighting object which was used last to recalculate lighting
    private Lighting lighting;

    //the texture applied to the mesh
    private Image texture; 

    //should the back face of the mesh be rendered? (keeping enabled greatly increases preformance, roughly 2x faster)*
    //*however, due to a poor implementation of backFaceCulling, for some cases, it is recommended to dissable this, 
    //which will may sacrafice preformance (especially for larger models), however it will mitigate the
    //strange visual effects that it may cause for certain models. 
    private boolean backFaceCull = true;

    //overloaded constructor takes in the name of the model, transform offsets, color and the boolean values 
    public Mesh(String modelFileName, String textureFileName, Vector3 modelOffsetAmount, EulerAngle modelOffsetRotation, double scale, Color colorIn, boolean shaded, boolean shouldBackFaceCull)
    {
        long start = System.nanoTime();
        triangles = new ArrayList<Triangle>();
        shading = shaded;
        backFaceCull = shouldBackFaceCull;
        color = colorIn;
        totalMovement = new Vector3();
        texture = null;

        if (modelFileName.endsWith(".obj"))
        {
            createTriangles(modelFileName, modelOffsetAmount, modelOffsetRotation, scale);
        }
        else
        {
            System.err.println("ERROR at: Mesh/constructor:\n\tUnsupported 3d model file type. Please use .obj files");
        }

        try
        {
            texture = ImageIO.read(new File(Main.resourcesDirectory,textureFileName));
        } 
        catch (IOException e)
        {
            System.err.println("ERROR at: Mesh/constructor:\n\tError while loading texture: " + textureFileName);
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

        if (texture != null)
        {

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
                    String[] lineArr = line.substring(1).trim().split(" ");
                    //create the vertex object
                    Vector3 vertex = new Vector3(Double.parseDouble(lineArr[0]), Double.parseDouble(lineArr[1]), Double.parseDouble(lineArr[2]));

                    //apply transformations to the vertex based on offset params
                    vertex = Vector3.applyMatrix(offsetRotationMatrix, vertex);
                    vertex = Vector3.add(offsetPosition, vertex);
                    vertex = Vector3.multiply(vertex, scale);

                    //adds the vertex to the array of vertices
                    vertices.add(vertex);
                }
                //f means face in .obj files
                if (line.startsWith("f "))
                {
                    String[] lineArr = line.split(" ");
                    int[] vertexIndexes = new int[lineArr.length-1];
                    int[] textureIndexes = new int[lineArr.length-1]; 
                    for (int i = 1; i < lineArr.length; i ++)
                    {
                        if (lineArr[i].contains("/"))
                            vertexIndexes[i-1] = Integer.parseInt(lineArr[i].substring(0, lineArr[i].indexOf("/")))-1;
                    }
                    //if the face contains three vertex indeces, create only one triangle, else create two. 
                    if (vertexIndexes.length <= 3)
                    {
                        triangles.add(new Triangle(this, vertices.get(vertexIndexes[0]), vertices.get(vertexIndexes[1]), vertices.get(vertexIndexes[2]), color));
                    }
                    else
                    {
                        triangles.add(new Triangle(this, vertices.get(vertexIndexes[0]), vertices.get(vertexIndexes[1]), vertices.get(vertexIndexes[2]), color));
                        triangles.add(new Triangle(this, vertices.get(vertexIndexes[0]), vertices.get(vertexIndexes[2]), vertices.get(vertexIndexes[3]), color));
                    }
                }
            }
        }
    }

}
