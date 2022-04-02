import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
public class GameObject 
{
    public List<Triangle> triangles;
    private Vector3 position;
    public Vector3 centerOfRotation;
    private EulerAngle orientation;
    public Color color;

    public boolean wireframe;

    public GameObject(String objName, Color colorIn, EulerAngle orientationIn, double scaleIn, boolean wireframeIn)
    {
        triangles = new ArrayList<Triangle>();
        orientation = orientationIn;
        wireframe = wireframeIn;
        color = colorIn;
        readObjFile(objName);
        setRotation(orientation);
        setScale(scaleIn);
    }

    public void setScale(double scale)
    {
        for (int i = 0; i < triangles.size(); i++)
        {
            triangles.get(i).point1 = Vector3.multiply(triangles.get(i).point1, scale);
            triangles.get(i).point2 = Vector3.multiply(triangles.get(i).point2, scale);
            triangles.get(i).point3 = Vector3.multiply(triangles.get(i).point3, scale);
        }
    }

    public void rotate(EulerAngle angle)
    {
        for (int i = 0; i < triangles.size(); i++)
        {
            triangles.get(i).point1 = Vector3.rotateAroundXaxis(triangles.get(i).point1, angle.x);
            triangles.get(i).point1 = Vector3.rotateAroundYaxis(triangles.get(i).point1, angle.y);
            triangles.get(i).point1 = Vector3.rotateAroundZaxis(triangles.get(i).point1, angle.z);
            triangles.get(i).point2 = Vector3.rotateAroundXaxis(triangles.get(i).point2, angle.x);
            triangles.get(i).point2 = Vector3.rotateAroundYaxis(triangles.get(i).point2, angle.y);
            triangles.get(i).point2 = Vector3.rotateAroundZaxis(triangles.get(i).point2, angle.z);
            triangles.get(i).point3 = Vector3.rotateAroundXaxis(triangles.get(i).point3, angle.x);
            triangles.get(i).point3 = Vector3.rotateAroundYaxis(triangles.get(i).point3, angle.y);
            triangles.get(i).point3 = Vector3.rotateAroundZaxis(triangles.get(i).point3, angle.z);
        }
    }

    public void setRotation(EulerAngle angle)
    {
        for (int i = 0; i < triangles.size(); i++)
        {
            triangles.get(i).point1 = Vector3.rotateAroundXaxis(triangles.get(i).point1, angle.x-orientation.x);
            triangles.get(i).point1 = Vector3.rotateAroundYaxis(triangles.get(i).point1, angle.y-orientation.x);
            triangles.get(i).point1 = Vector3.rotateAroundZaxis(triangles.get(i).point1, angle.z-orientation.x);
            triangles.get(i).point2 = Vector3.rotateAroundXaxis(triangles.get(i).point2, angle.x-orientation.x);
            triangles.get(i).point2 = Vector3.rotateAroundYaxis(triangles.get(i).point2, angle.y-orientation.x);
            triangles.get(i).point2 = Vector3.rotateAroundZaxis(triangles.get(i).point2, angle.z-orientation.x);
            triangles.get(i).point3 = Vector3.rotateAroundXaxis(triangles.get(i).point3, angle.x-orientation.x);
            triangles.get(i).point3 = Vector3.rotateAroundYaxis(triangles.get(i).point3, angle.y-orientation.x);
            triangles.get(i).point3 = Vector3.rotateAroundZaxis(triangles.get(i).point3, angle.z-orientation.x);
        }
    }

    private void readObjFile(String fileName)
    {
        List<Vector3> vertices = new ArrayList<Vector3>();
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
                    vertices.add(new Vector3(Double.parseDouble(lineArr[0]), Double.parseDouble(lineArr[1]), Double.parseDouble(lineArr[2])));
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
                    if (indexArr.length <= 3 || wireframe == true)
                    {
                        triangles.add(new Triangle(vertices.get(indexArr[0]), vertices.get(indexArr[1]), vertices.get(indexArr[2]), color, !wireframe));
                    }
                    else
                    {
                        triangles.add(new Triangle(vertices.get(indexArr[0]), vertices.get(indexArr[1]), vertices.get(indexArr[2]), color, !wireframe));
                        triangles.add(new Triangle(vertices.get(indexArr[0]), vertices.get(indexArr[2]), vertices.get(indexArr[3]), color, !wireframe));
                    }
                }
            }
        }
    }
}
