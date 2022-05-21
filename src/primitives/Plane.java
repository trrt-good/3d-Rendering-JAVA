package src.primitives;
public class Plane 
{
    //just for reference if ever needed. 
    public static final Plane XZ_PLANE = new Plane(new Vector3(0, 0, 0), new Vector3(0, 1, 0));
    public static final Plane XY_PLANE = new Plane(new Vector3(0, 0, 0), new Vector3(0, 0, 1));
    public static final Plane ZY_PLANE = new Plane(new Vector3(0, 0, 0), new Vector3(1, 0, 0));

    public Vector3 normal; //normal vector of the plane (perpendicular
    public Vector3 pointOnPlane; //point on the plane
    
    //creates a plane object with point "pointIn" that lies on the plane and
    //"normalVectorIn" which is normal to the plane.
    public Plane(Vector3 pointIn, Vector3 normalVectorIn)
    {
        normal = normalVectorIn;
        pointOnPlane = pointIn;
    }

    //slightly slower constructor, mainly bexcause of normalization. 
    public Plane(Vector3 point1, Vector3 point2, Vector3 point3)
    {
        normal = Vector3.crossProduct(Vector3.subtract(point1, point2), Vector3.subtract(point2, point3)).getNormalized();
        pointOnPlane = point1;
    }

    //returns the "d" value of the standard equation for planes:
    // ax + by + cz + d = 0
    public double getDValue()
    {
        return -normal.x*pointOnPlane.x - normal.y*pointOnPlane.y - normal.z*pointOnPlane.z;
    }
}
