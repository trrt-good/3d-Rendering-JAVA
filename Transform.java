public class Transform 
{
    private GameObject gameObject; //the gameobject that this transform is attached to

    private Vector3 position; //position of transform in world-space.

    //the forward vector of the transform in world space. This is always the z-axis in local transform space
    private Vector3 forward; 

    //the rightwards vector of the transform in world space. This is always the x-axis in local transform space. 
    private Vector3 right; 

    //the upwards vector of the transform in world space. This is always the y-axis in local transform space. 
    private Vector3 up; 

    //the rotation of the transform, x = pitch, y = yaw, z = roll. x value represents rotation about
    //the right vector, y represents rotation about up vector and z represents rotation about the 
    //forward vector.
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

    public void setPosition(Vector3 positionIn)
    {
        gameObject.getMesh().translate(Vector3.subtract(positionIn, position));
        position = positionIn;
    }

    public void move(Vector3 amount)
    {
        gameObject.getMesh().translate(amount);
        position = Vector3.add(position, amount);
    }

    //following three methods:
    //sets the specified rotation around a local axis, as well as updating
    //the orientation of an attached mesh. 
    public void setPitch(double angle)
    {
        Matrix3x3 rotationMatrix = Matrix3x3.axisAngleMatrix(right, angle-rotation.x);
        up = Vector3.applyMatrix(rotationMatrix, up);
        forward = Vector3.applyMatrix(rotationMatrix, forward);
        if (gameObject.getMesh() != null)
            gameObject.getMesh().rotate(rotationMatrix, position);
        rotation.x = angle;
    }

    public void setYaw(double angle)
    {
        Matrix3x3 rotationMatrix = Matrix3x3.axisAngleMatrix(up, angle-rotation.y);
        forward = Vector3.applyMatrix(rotationMatrix, forward);
        right = Vector3.applyMatrix(rotationMatrix, right);
        if (gameObject.getMesh() != null)
            gameObject.getMesh().rotate(rotationMatrix, position);
        rotation.y = angle;
    }
    
    public void setRoll(double angle)
    {
        Matrix3x3 rotationMatrix = Matrix3x3.axisAngleMatrix(forward, angle-rotation.z);
        up = Vector3.applyMatrix(rotationMatrix, up);
        right = Vector3.applyMatrix(rotationMatrix, right);
        if (gameObject.getMesh() != null)
            gameObject.getMesh().rotate(rotationMatrix, position);
        rotation.z = angle;
    }

    //returns the world-space equivilant of "point" in local space. 
    //example: in local space, the forward direction can always be 
    //represented by (0, 0, 1), but translating that into world space will 
    //return "forward" vector, which could be something like (0, 0.3, 0.4) if 
    //the transform is pitched up. 
    public Vector3 transformToWorld(Vector3 point)
    {
        up = up.getNormalized();
        forward = forward.getNormalized();
        right = right.getNormalized();
        return Vector3.applyMatrix(new Matrix3x3(right, up, forward), point);
    }

    //opposite of "transformToWorld". It returns a point with local-space
    //coorindates equivilant to the inputted world-space coordinates.  
    public Vector3 transformToLocal(Vector3 point)
    {
        up = up.getNormalized();
        forward = forward.getNormalized();
        right = right.getNormalized();
        return Vector3.applyMatrix(new Matrix3x3(right, up, forward).getInverse(), point);
    }

    //#region getter/setter methods
    public Vector3 getForward()
    {
        return forward.getNormalized();
    }

    public Vector3 getUp()
    {
        return up.getNormalized();
    }

    public Vector3 getRight()
    {
        return right.getNormalized();
    }

    public Vector3 getPosition()
    {
        return position;
    }

    public void setGameObject(GameObject gameObjectIn)
    {
        gameObject = gameObjectIn;
    }
    //#endregion
}
