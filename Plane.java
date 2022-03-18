public class Plane 
{
    public Vector3 planeNormal; //normal vector of the plane (perpendicular
    public Vector3 planePoint; //point on the plane

    public Plane(Vector3 pointIn, Vector3 normalVectorIn)
    {
        planeNormal = normalVectorIn;
        planePoint = pointIn;
    }

}
