public class GameObject 
{  
    private Mesh mesh;
    private Transform transform;
    private String name;

    public GameObject(String nameIn, Mesh meshIn, Transform transformIn)
    {
        mesh = meshIn;
        name = nameIn;
        transformIn.setGameObject(this);
        transform = transformIn;
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
}
