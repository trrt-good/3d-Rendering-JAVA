import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
public class GameObject 
{
    public List<Triangle> triangles;
    public Vector3 position;
    public Vector3 centerOfRotation;
    public EulerAngle orientation;

    public boolean wireframe;

    public GameObject(String objName, EulerAngle orientationIn, boolean wireframeIn)
    {
        triangles = new ArrayList<Triangle>();
        orientation = orientationIn;
        wireframe = wireframeIn;
        readObjFile(objName);
        for (int i = 0; i < triangles.size(); i++)
        {
            triangles.get(i).point1 = Vector3.rotateAroundXaxis(triangles.get(i).point1, orientation.x);
            triangles.get(i).point1 = Vector3.rotateAroundYaxis(triangles.get(i).point1, orientation.y);
            triangles.get(i).point1 = Vector3.rotateAroundZaxis(triangles.get(i).point1, orientation.z);
            triangles.get(i).point2 = Vector3.rotateAroundXaxis(triangles.get(i).point2, orientation.x);
            triangles.get(i).point2 = Vector3.rotateAroundYaxis(triangles.get(i).point2, orientation.y);
            triangles.get(i).point2 = Vector3.rotateAroundZaxis(triangles.get(i).point2, orientation.z);
            triangles.get(i).point3 = Vector3.rotateAroundXaxis(triangles.get(i).point3, orientation.x);
            triangles.get(i).point3 = Vector3.rotateAroundYaxis(triangles.get(i).point3, orientation.y);
            triangles.get(i).point3 = Vector3.rotateAroundZaxis(triangles.get(i).point3, orientation.z);
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

    private void readObjFile(String fileName)
    {
        List<Vector3> vertices = new ArrayList<Vector3>();
        Scanner scanner;
        String word = "";
        try 
        {
            scanner = new Scanner(new File(Main.resourcesDirectory, fileName));   
        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
            return;
        }

        while(scanner.hasNext())
        {
            word = scanner.next();
            if (!(word.charAt(0) == '#'))
            {
                if (word.equals("v"))
                {
                    vertices.add(new Vector3(scanner.nextDouble(), scanner.nextDouble(), scanner.nextDouble()));
                }
                else if (word.equals("f"))
                {
                    String index1String = scanner.next();
                    int index1 = Integer.parseInt(index1String.substring(0, index1String.indexOf('/')))-1;
                    String index2String = scanner.next();
                    int index2 = Integer.parseInt(index2String.substring(0, index2String.indexOf('/')))-1;
                    String index3String = scanner.next();
                    int index3 = Integer.parseInt(index3String.substring(0, index3String.indexOf('/')))-1;
                    triangles.add(new Triangle(vertices.get(index1), vertices.get(index2), vertices.get(index3), !wireframe));
                }
            }
        }
    }
}
