import java.util.List;
import java.util.ArrayList;
public class GameObject 
{
    public List<Triangle> triangles = new ArrayList<Triangle>();
    public Vector3 position;
    public Vector3 centerOfRotation;
    public EulerAngle orientation;

    public GameObject()
    {
        
    }
}
