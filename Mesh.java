import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;
import java.awt.Color;

public class Mesh 
{
    private ArrayList<Triangle> triangles;
    private Color color;
    private boolean shading;

    private boolean backFaceCull = true;

    public Mesh(String modelFileName, Vector3 offsetPosition, EulerAngle offsetOrientation, double scale, Color colorIn, boolean shaded, boolean shouldBackFaceCull)
    {
        System.out.print("Creating mesh: " + modelFileName + "... ");
        long start = System.nanoTime();
        triangles = new ArrayList<Triangle>();

        shading = shaded;
        backFaceCull = shouldBackFaceCull;
        color = colorIn;
        if (modelFileName.endsWith(".obj"))
        {
            readObjFile(modelFileName, offsetPosition, offsetOrientation, scale);
        }
        else
        {
            System.err.println("ERROR: Unsupported 3d model file type. Please use .obj files");
        }

        System.out.println("finished in all " + triangles.size() + " triangles in " + (System.nanoTime() - start)/1000000 + "ms");
    }

    public void applyMatrix(Matrix3x3 matrix)
    {
        for (int i = 0; i < triangles.size(); i ++)
        {
            triangles.get(i).point1 = Vector3.applyMatrix(matrix, triangles.get(i).point1);
            triangles.get(i).point2 = Vector3.applyMatrix(matrix, triangles.get(i).point2);
            triangles.get(i).point3 = Vector3.applyMatrix(matrix, triangles.get(i).point3);
        }
    }

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

    private void readObjFile(String fileName, Vector3 offsetPosition, EulerAngle offsetOrientation, double scale)
    {
        Matrix3x3 offsetRotationMatrix = Matrix3x3.eulerRotation(offsetOrientation);
        System.out.println("\n" + offsetRotationMatrix);
        ArrayList<Vector3> vertices = new ArrayList<Vector3>();
        Scanner scanner;
        String line = "";
        try 
        {
            scanner = new Scanner(new File(Main.resourcesDirectory, fileName));   
        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
            return;
        }

        while(scanner.hasNextLine())
        {
            line = scanner.nextLine();

            if (!line.equals(""))
            {
                if (line.startsWith("v "))
                {
                    String[] lineArr = line.substring(1).trim().split(" ");
                    Vector3 vertex = new Vector3(Double.parseDouble(lineArr[0]), Double.parseDouble(lineArr[1]), Double.parseDouble(lineArr[2]));
                    //TODO: use rotation matrices to make this intrinsic rotation. 
                    vertex = Vector3.applyMatrix(offsetRotationMatrix, vertex);
                    vertex = Vector3.add(offsetPosition, vertex);
                    vertex = Vector3.multiply(vertex, scale);
                    vertices.add(vertex);
                }
                if (line.startsWith("f "))
                {
                    String[] lineArr = line.split(" ");
                    int[] indexArr = new int[lineArr.length-1];
                    for (int i = 1; i < lineArr.length; i ++)
                    {
                        if (lineArr[i].contains("/"))
                            indexArr[i-1] = Integer.parseInt(lineArr[i].substring(0, lineArr[i].indexOf("/")))-1;
                    }
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
