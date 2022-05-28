package trrt.rendering3d.gameObject;

import java.io.*;

import trrt.rendering3d.primitives.*;
import trrt.rendering3d.Main;

public class GameObject implements Serializable
{  
    //the mesh of the object
    private Mesh mesh;

    //the transform of the object, which handles position and rotation.
    private Transform transform;

    //name
    private String name;

    /**
     * creates a game object with the given mesh and transform components 
     * @param nameIn name of the game object
     * @param meshIn mesh for the game object
     * @param transformPos transform for the game object
     */
    public GameObject(String nameIn, Mesh meshIn, Vector3 transformPos)
    {
        mesh = meshIn;
        name = nameIn;
        transform = new Transform((transformPos == null)? new Vector3(0, 0, 0) : transformPos);
    }
    
    //#region getter methods 
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
    //#endregion

    public static void saveGameObject(GameObject gameObject, File parentDirectory)
    {
        File outputFile = new File(parentDirectory, gameObject.name + ".GAMEOBJECT");
        
        try
        {
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            ObjectOutputStream objOutStream = new ObjectOutputStream(outputStream);
            objOutStream.writeObject(gameObject);            
            objOutStream.close();
        }        
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void saveGameObject(GameObject gameObject, String name, File parentDirectory)
    {
        File outputFile = new File(parentDirectory, name + ".GAMEOBJECT");
        
        try
        {
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            ObjectOutputStream objOutStream = new ObjectOutputStream(outputStream);
            objOutStream.writeObject(gameObject);            
            objOutStream.close();
        }        
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static GameObject readGameObject(String gameObjectName)
    {
        GameObject obj = null;
        try
        {
            FileInputStream fileInputStream = new FileInputStream(new File(Main.GAMEOBJECT_DIRECTORY, gameObjectName));
            ObjectInputStream objInputStream = new ObjectInputStream(fileInputStream);
            obj = (GameObject)objInputStream.readObject();
            objInputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return obj;
    }
}
