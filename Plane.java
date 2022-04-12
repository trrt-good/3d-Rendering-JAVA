public class Plane 
{
    public static final Plane XZ_PLANE = new Plane(new Vector3(0, 0, 0), new Vector3(0, 1, 0));
    public static final Plane XY_PLANE = new Plane(new Vector3(0, 0, 0), new Vector3(0, 0, 1));
    public static final Plane ZY_PLANE = new Plane(new Vector3(0, 0, 0), new Vector3(1, 0, 0));

    public Vector3 normal; //normal vector of the plane (perpendicular
    public Vector3 pointOnPlane; //point on the plane
    
    public Plane(Vector3 pointIn, Vector3 normalVectorIn)
    {
        normal = normalVectorIn;
        pointOnPlane = pointIn;
    }

    public Plane(Vector3 point1, Vector3 point2, Vector3 point3)
    {
        normal = Vector3.crossProduct(Vector3.subtract(point1, point2), Vector3.subtract(point2, point3));
        pointOnPlane = point1;
    }

    public double getDValue()
    {
        return -normal.x*pointOnPlane.x - normal.y*pointOnPlane.y - normal.z*pointOnPlane.z;
    }
}
