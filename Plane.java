public class Plane 
{
    public Vector3 planeNormal; //normal vector of the plane (perpendicular
    public Vector3 planePoint; //point on the plane

    public Plane(Vector3 pointIn, Vector3 normalVectorIn)
    {
        planeNormal = normalVectorIn;
        planePoint = pointIn;
    }

    public Vector3 getIntersectionPoint(Vector3 lineDirection, Vector3 linePoint)
    {
        return Vector3.add(linePoint, Vector3.multiply(lineDirection, Vector3.dotProduct(Vector3.subtract(planePoint, linePoint), planeNormal)/Vector3.dotProduct(lineDirection, planeNormal)));
    }
}
