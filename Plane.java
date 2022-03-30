public class Plane 
{
    public Vector3 normal; //normal vector of the plane (perpendicular
    public Vector3 pointOnPlane; //point on the plane
    
    public Plane(Vector3 pointIn, Vector3 normalVectorIn)
    {
        normal = normalVectorIn;
        pointOnPlane = pointIn;
    }
}
