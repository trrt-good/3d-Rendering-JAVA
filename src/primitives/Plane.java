package src.primitives;
public class Plane 
{
    //just for reference if ever needed. 
    public static final Plane XZ_PLANE = new Plane(new Vector3(0, 0, 0), new Vector3(0, 1, 0));
    public static final Plane XY_PLANE = new Plane(new Vector3(0, 0, 0), new Vector3(0, 0, 1));
    public static final Plane ZY_PLANE = new Plane(new Vector3(0, 0, 0), new Vector3(1, 0, 0));

    /** a vector normal to the plane */
    public Vector3 normal; 

    /** a point on the plane */
    public Vector3 pointOnPlane;
    
    /**
     * creates a plane object with point {@code pointIn} that lies on the plane and
     * {@code normalVectorIn} which is normal to the plane.
     * @param pointIn a point located on the plane 
     * @param normalVectorIn a vector normal to the plane
     */
    public Plane(Vector3 pointIn, Vector3 normalVectorIn)
    {
        normal = normalVectorIn;
        pointOnPlane = pointIn;
    }

    /**
     * a slightly slower constructor which accepts 3 points on the plane , mainly because of normalization. 
     * @param point1 
     * @param point2
     * @param point3
     */
    public Plane(Vector3 point1, Vector3 point2, Vector3 point3)
    {
        normal = Vector3.crossProduct(Vector3.subtract(point1, point2), Vector3.subtract(point2, point3)).getNormalized();
        pointOnPlane = point1;
    }

    /**
     * returns the {@code d} value of the standard equation for planes: <pre>
     * ax + by + cz + d = 0 </pre>
     * @return the {@code d} value of the standard equation for planes
     */
    public double getDValue()
    {
        return -normal.x*pointOnPlane.x - normal.y*pointOnPlane.y - normal.z*pointOnPlane.z;
    }
}
