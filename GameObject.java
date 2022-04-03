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
    public boolean shading = true;
    public String name;

    public boolean wireframe;

    public GameObject(Vector3 positionIn, String modelFileName, Color colorIn, EulerAngle orientationIn, double scaleIn, boolean wireframeIn)
    {
        System.out.print("Creating gameObject: " + modelFileName + "... ");
        long start = System.nanoTime();
        triangles = new ArrayList<Triangle>();
        orientation = orientationIn;
        wireframe = wireframeIn;
        color = colorIn;
        name = modelFileName.substring(0, modelFileName.indexOf("."));
        position = new Vector3();
        readObjFile(modelFileName);
        setRotation(orientation);
        setScale(scaleIn);
        setPosition(positionIn);
        System.out.println("finished in all " + triangles.size() + " triangles in " + (System.nanoTime() - start)/1000000 + "ms");

    }

    public void recalculateLighting(Lighting lighting)
    {
        if (!wireframe && shading)
        {
            for (int i = 0; i < triangles.size(); i++)
            {
                triangles.get(i).calculateLightingColor(lighting);
            }
        }
    }

    public void setPosition(Vector3 positionIn)
    {
        for (int i = 0; i < triangles.size(); i++)
        {
            triangles.get(i).point1 = Vector3.add(triangles.get(i).point1, Vector3.subtract(positionIn, position));
            triangles.get(i).point2 = Vector3.add(triangles.get(i).point2, Vector3.subtract(positionIn, position));
            triangles.get(i).point3 = Vector3.add(triangles.get(i).point3, Vector3.subtract(positionIn, position));
        }
        position = positionIn;
    }

    public void move(Vector3 amount)
    {
        for (int i = 0; i < triangles.size(); i++)
        {
            triangles.get(i).point1 = Vector3.add(triangles.get(i).point1, amount);
            triangles.get(i).point2 = Vector3.add(triangles.get(i).point2, amount);
            triangles.get(i).point3 = Vector3.add(triangles.get(i).point3, amount);
        }
        position = Vector3.add(position, amount);
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
                    if (indexArr.length <= 3)
                    {
                        triangles.add(new Triangle(this, vertices.get(indexArr[0]), vertices.get(indexArr[1]), vertices.get(indexArr[2]), color, !wireframe));
                    }
                    else
                    {
                        triangles.add(new Triangle(this, vertices.get(indexArr[0]), vertices.get(indexArr[1]), vertices.get(indexArr[2]), color, !wireframe));
                        triangles.add(new Triangle(this, vertices.get(indexArr[0]), vertices.get(indexArr[2]), vertices.get(indexArr[3]), color, !wireframe));
                    }
                }
            }
        }
    }
}
