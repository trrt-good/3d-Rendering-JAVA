import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;
import java.io.File;
import java.io.FileNotFoundException;
public class GameObject 
{
    public List<Triangle> triangles;
    public Vector3 position;
    public Vector3 centerOfRotation;
    public EulerAngle orientation;

    public GameObject(String objName)
    {
        triangles = new ArrayList<Triangle>();
        readObjFile(objName);
    }

    public void readObjFile(String fileName)
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
                    String index1 = scanner.next();
                    String index2 = scanner.next();
                    String index3 = scanner.next();
                    triangles.add(new Triangle(vertices.get(Integer.parseInt(index1.substring(0, index1.indexOf('/')))-1), vertices.get(Integer.parseInt(index2.substring(0, index2.indexOf('/')))-1), vertices.get(Integer.parseInt(index3.substring(0, index3.indexOf('/')))-1)));
                }
            }
        }
    }
}
