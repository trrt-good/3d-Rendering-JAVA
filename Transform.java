public class Transform 
{
    private GameObject gameObject; //the gameobject that this transform is attached to

    private Vector3 position;
    private Vector3 forward; //the forward vector of the transform in world space. This is always the z-axis in local transform space
    private Vector3 right; //the rightwards vector of the transform in world space. This is always the x-axis in local transform space. 
    private Vector3 up; //the upwards vector of the transform in world space. This is always the y-axis in local transform space. 
    private EulerAngle rotation;

    //default rotation always 0, 0, 0
    public Transform(Vector3 positionIn)
    {
        position = positionIn;
        rotation = new EulerAngle();
        forward = new Vector3(0, 0, 1);
        right = new Vector3(1, 0, 0);
        up = new Vector3(0, 1, 0);
    }

    public void setGameObject(GameObject gameObjectIn)
    {
        gameObject = gameObjectIn;
    }

    public void setPitch(double angle)
    {
        Matrix3x3 rotationMatrix = Matrix3x3.axisAngleMatrix(right, angle-rotation.x);
        up = Vector3.applyMatrix(rotationMatrix, up);
        forward = Vector3.applyMatrix(rotationMatrix, forward);
        gameObject.getMesh().applyMatrix(rotationMatrix);
        rotation.x = angle;
    }

    public void setYaw(double angle)
    {
        Matrix3x3 rotationMatrix = Matrix3x3.axisAngleMatrix(up, angle-rotation.y);
        forward = Vector3.applyMatrix(rotationMatrix, forward);
        right = Vector3.applyMatrix(rotationMatrix, right);
        gameObject.getMesh().applyMatrix(rotationMatrix);
        rotation.y = angle;
    }
    
    public void setRoll(double angle)
    {
        Matrix3x3 rotationMatrix = Matrix3x3.axisAngleMatrix(forward, angle-rotation.z);
        up = Vector3.applyMatrix(rotationMatrix, up);
        right = Vector3.applyMatrix(rotationMatrix, right);
        gameObject.getMesh().applyMatrix(rotationMatrix);
        rotation.z = angle;
    }

    public Vector3 transformToWorld(Vector3 point)
    {
        up = up.getNormalized();
        forward = forward.getNormalized();
        right = right.getNormalized();
        return Vector3.applyMatrix(new Matrix3x3(right, up, forward), point);
    }

    public Vector3 transformToLocal(Vector3 point)
    {
        up = up.getNormalized();
        forward = forward.getNormalized();
        right = right.getNormalized();
        return Vector3.applyMatrix(new Matrix3x3(right, up, forward).getInverse(), point);
    }

    public Vector3 getForward()
    {
        return forward;
    }

    public Vector3 getUp()
    {
        return up;
    }

    public Vector3 getRight()
    {
        return right;
    }

    public Vector3 getPosition()
    {
        return position;
    }

    
    /*
    TODO: 
    create a rotate function that rotates the forward, right and up vectors. In order to set the triangles in the mesh to the same rotation,
    use the forward right and up vectors as a matrix and multiply each triangle by that matrix. 
    */
}
