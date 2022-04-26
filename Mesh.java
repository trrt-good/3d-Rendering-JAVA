import java.util.ArrayList;
import java.util.Scanner;

import java.io.FileNotFoundException;
import java.io.File;

import java.awt.Color;

//a class for storing groups of triangles in a mesh.
public class Mesh 
{
    //a collection of all the triangles in the mesh.
    private ArrayList<Triangle> triangles;

    //the color of all the triangles of the mesh.
    private Color color;

    //should the mesh be effected by lighting?
    private boolean shading;

    //should the back face of the mesh be rendered? (keeping enabled greatly increases preformance, roughly 2x faster)*
    //*however, due to a poor implementation of backFaceCulling, for some cases, it is recommended to dissable this, 
    //which will may sacrafice preformance (especially for larger models), however it will mitigate the
    //strange visual effects that it may cause for certain models. 
    private boolean backFaceCull = true;

    //overloaded constructor takes in the name of the model, transform offsets, color and the boolean values 
    public Mesh(String modelFileName, Vector3 modelOffsetAmount, EulerAngle modelOffsetRotation, double scale, Color colorIn, boolean shaded, boolean shouldBackFaceCull)
    {
        long start = System.nanoTime();
        triangles = new ArrayList<Triangle>();
        shading = shaded;
        backFaceCull = shouldBackFaceCull;
        color = colorIn;
        if (modelFileName.endsWith(".obj"))
        {
            readObjFile(modelFileName, modelOffsetAmount, modelOffsetRotation, scale);
        }
        else
        {
            System.err.println("ERROR at: Mesh/constructor:\n\tUnsupported 3d model file type. Please use .obj files");
        }

        System.out.println("mesh created: " + modelFileName + " in " + (System.nanoTime() - start)/1000000 + "ms\n\t- " + triangles.size() + " triangles");
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

    //recalculates the lighting of each triangle in the mesh based off the given
    //lighting object
    public void recalculateLighting(Lighting lighting)
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
    private void readObjFile(String fileName, Vector3 offsetPosition, EulerAngle offsetOrientation, double scale)
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
                    int[] indexArr = new int[lineArr.length-1];
                    for (int i = 1; i < lineArr.length; i ++)
                    {
                        if (lineArr[i].contains("/"))
                            indexArr[i-1] = Integer.parseInt(lineArr[i].substring(0, lineArr[i].indexOf("/")))-1;
                    }
                    //if the face contains three vertex indeces, create only one triangle, else create two. 
                    if (indexArr.length <= 3)
                    {
                        triangles.add(new Triangle(this, vertices.get(indexArr[0]), vertices.get(indexArr[1]), vertices.get(indexArr[2]), color));
                    }
                    else
                    {
                        triangles.add(new Triangle(this, vertices.get(indexArr[0]), vertices.get(indexArr[1]), vertices.get(indexArr[2]), color));
                        triangles.add(new Triangle(this, vertices.get(indexArr[0]), vertices.get(indexArr[2]), vertices.get(indexArr[3]), color));
                    }
                }
            }
        }
    }

}
