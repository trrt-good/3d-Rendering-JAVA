public class GameObject 
{  
    //the mesh of the object
    private Mesh mesh;

    //the transform of the object, which handles position and rotation.
    private Transform transform;

    //name
    private String name;

    public GameObject(String nameIn, Mesh meshIn, Transform transformIn)
    {
        mesh = meshIn;
        name = nameIn;
        transformIn.setGameObject(this);
        transform = transformIn;
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
