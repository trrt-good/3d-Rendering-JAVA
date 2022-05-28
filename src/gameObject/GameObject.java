package src.gameObject;

import java.io.Serializable;

import src.primitives.Vector3;

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
}
