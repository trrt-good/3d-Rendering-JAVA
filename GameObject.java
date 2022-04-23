import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
public class GameObject 
{  
    private Mesh mesh;
    private Transform transform;
    private String name;

    public GameObject(String nameIn, Mesh meshIn, Transform transformIn)
    {
        mesh = meshIn;
        name = nameIn;
    }
    
    public Mesh getMesh()
    {
        return mesh;
    }

    public Transform getTransform()
    {
        return transform;
    }
    
    public String getName()
    {
        return name;
    }

    

    // public void setPosition(Vector3 positionIn)
    // {
    //     Triangle triangle;
    //     for (int i = 0; i < mesh.size(); i++)
    //     {
    //         triangle = mesh.get(i);
    //         triangle.point1 = Vector3.add(triangle.point1, Vector3.subtract(positionIn, globalPosition));
    //         triangle.point2 = Vector3.add(triangle.point2, Vector3.subtract(positionIn, globalPosition));
    //         triangle.point3 = Vector3.add(triangle.point3, Vector3.subtract(positionIn, globalPosition));
    //     }
    //     globalPosition = positionIn;
    // }

    // public void move(Vector3 amount)
    // {
    //     Triangle triangle;
    //     for (int i = 0; i < mesh.size(); i++)
    //     {
    //         triangle = mesh.get(i);
    //         triangle.point1 = Vector3.add(triangle.point1, amount);
    //         triangle.point2 = Vector3.add(triangle.point2, amount);
    //         triangle.point3 = Vector3.add(triangle.point3, amount);
    //     }
    //     globalPosition = Vector3.add(globalPosition, amount);
    // }

    // private void setScale(double scale)
    // {
    //     Triangle triangle;
    //     for (int i = 0; i < mesh.size(); i++)
    //     {
    //         triangle = mesh.get(i);
    //         triangle.point1 = Vector3.multiply(triangle.point1, scale);
    //         triangle.point2 = Vector3.multiply(triangle.point2, scale);
    //         triangle.point3 = Vector3.multiply(triangle.point3, scale);
    //     }
    // }

    // public void setGlobalRotation(EulerAngle angle)
    // {
    //     Triangle triangle;
    //     for (int i = 0; i < mesh.size(); i++)
    //     {
    //         triangle = mesh.get(i);
    //         triangle.point1 = Vector3.rotateAroundXaxis(triangle.point1, angle.x-orientation.x);
    //         triangle.point1 = Vector3.rotateAroundYaxis(triangle.point1, angle.y-orientation.x);
    //         triangle.point1 = Vector3.rotateAroundZaxis(triangle.point1, angle.z-orientation.x);
    //         triangle.point2 = Vector3.rotateAroundXaxis(triangle.point2, angle.x-orientation.x);
    //         triangle.point2 = Vector3.rotateAroundYaxis(triangle.point2, angle.y-orientation.x);
    //         triangle.point2 = Vector3.rotateAroundZaxis(triangle.point2, angle.z-orientation.x);
    //         triangle.point3 = Vector3.rotateAroundXaxis(triangle.point3, angle.x-orientation.x);
    //         triangle.point3 = Vector3.rotateAroundYaxis(triangle.point3, angle.y-orientation.x);
    //         triangle.point3 = Vector3.rotateAroundZaxis(triangle.point3, angle.z-orientation.x);
    //     }
    //     orientation = angle;
    // }

    // public void setLocalRotation(EulerAngle angle)
    // {
    //     Triangle triangle;
    //     for (int i = 0; i < mesh.size(); i++)
    //     {
    //         triangle = mesh.get(i);
    //         triangle.point1 = Vector3.subtract(triangle.point1, globalPosition);
    //         triangle.point1 = Vector3.rotateAroundXaxis(triangle.point1, angle.x-orientation.x);
    //         triangle.point1 = Vector3.rotateAroundYaxis(triangle.point1, angle.y-orientation.y);
    //         triangle.point1 = Vector3.rotateAroundZaxis(triangle.point1, angle.z-orientation.z);
    //         triangle.point1 = Vector3.add(triangle.point1, globalPosition);

    //         triangle.point2 = Vector3.subtract(triangle.point2, globalPosition);
    //         triangle.point2 = Vector3.rotateAroundXaxis(triangle.point2, angle.x-orientation.x);
    //         triangle.point2 = Vector3.rotateAroundYaxis(triangle.point2, angle.y-orientation.y);
    //         triangle.point2 = Vector3.rotateAroundZaxis(triangle.point2, angle.z-orientation.z);
    //         triangle.point2 = Vector3.add(triangle.point2, globalPosition);

    //         triangle.point3 = Vector3.subtract(triangle.point3, globalPosition);
    //         triangle.point3 = Vector3.rotateAroundXaxis(triangle.point3, angle.x-orientation.x);
    //         triangle.point3 = Vector3.rotateAroundYaxis(triangle.point3, angle.y-orientation.y);
    //         triangle.point3 = Vector3.rotateAroundZaxis(triangle.point3, angle.z-orientation.z);
    //         triangle.point3 = Vector3.add(triangle.point3, globalPosition);
    //     }
    //     orientation = angle;
    // }

    // private void overrideLocalRotation(EulerAngle angle)
    // {
    //     Triangle triangle;
    //     for (int i = 0; i < mesh.size(); i++)
    //     {
    //         triangle = mesh.get(i);
    //         triangle.point1 = Vector3.subtract(triangle.point1, globalPosition);
    //         triangle.point1 = Vector3.rotateAroundXaxis(triangle.point1, angle.x-orientation.x);
    //         triangle.point1 = Vector3.rotateAroundYaxis(triangle.point1, angle.y-orientation.y);
    //         triangle.point1 = Vector3.rotateAroundZaxis(triangle.point1, angle.z-orientation.z);
    //         triangle.point1 = Vector3.add(triangle.point1, globalPosition);

    //         triangle.point2 = Vector3.subtract(triangle.point2, globalPosition);
    //         triangle.point2 = Vector3.rotateAroundXaxis(triangle.point2, angle.x-orientation.x);
    //         triangle.point2 = Vector3.rotateAroundYaxis(triangle.point2, angle.y-orientation.y);
    //         triangle.point2 = Vector3.rotateAroundZaxis(triangle.point2, angle.z-orientation.z);
    //         triangle.point2 = Vector3.add(triangle.point2, globalPosition);

    //         triangle.point3 = Vector3.subtract(triangle.point3, globalPosition);
    //         triangle.point3 = Vector3.rotateAroundXaxis(triangle.point3, angle.x-orientation.x);
    //         triangle.point3 = Vector3.rotateAroundYaxis(triangle.point3, angle.y-orientation.y);
    //         triangle.point3 = Vector3.rotateAroundZaxis(triangle.point3, angle.z-orientation.z);
    //         triangle.point3 = Vector3.add(triangle.point3, globalPosition);
    //     }
    // }

    
}
