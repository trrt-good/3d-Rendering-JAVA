public class Transform 
{
    private Vector3 position;
    private Vector3 forward; //the forward vector of the transform in world space. This is always the z-axis in local transform space
    private Vector3 right; //the rightwards vector of the transform in world space. This is always the x-axis in local transform space. 
    private Vector3 up; //the upwards vector of the transform in world space. This is always the y-axis in local transform space. 
    private EulerAngle rotation;

    public Transform(Vector3 positionIn, EulerAngle rotationIn)
    {
        position = positionIn;
        rotation = rotationIn;
    }

    public Vector3 getPosition()
    {
        return position;
    }
}
