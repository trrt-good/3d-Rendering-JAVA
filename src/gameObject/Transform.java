package src.gameObject;
import src.primitives.Matrix3x3;
import src.primitives.Vector3;
import src.primitives.Quaternion;

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

    //the quaternion rotation of the transform
    private Quaternion rotation;

    //default rotation always 0, 0, 0
    public Transform(Vector3 positionIn)
    {
        position = positionIn;
        rotation = new Quaternion(1, 0, 0, 0);
        forward = new Vector3(0, 0, 1);
        right = new Vector3(1, 0, 0);
        up = new Vector3(0, 1, 0);
    }

    public void lookTowards(Vector3 point)
    {
        // if (point.x != 0 && point.y != 0 && point.z != 0)
        // {
        //     point = transformToLocal(point);
        //     setYaw(rotation.y + ((point.x < 0)? -Math.atan(point.z/point.x)-Math.PI/2 : Math.PI/2-Math.atan(point.z/point.x)));
    
        //     setPitch(rotation.x + Math.atan(point.y/Math.sqrt(point.x*point.x + point.z*point.z)));
        // }
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

    public void rotate(Quaternion q)
    {
        rotation = Quaternion.multiply(rotation, q);
        gameObject.getMesh().rotate(q, position);
        forward.rotate(q);
        right.rotate(q);
        up.rotate(q);
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
        return (forward = forward.getNormalized());
    }

    public Vector3 getUp()
    {
        return (up = up.getNormalized());
    }

    public Vector3 getRight()
    {
        return (right = right.getNormalized());
    }

    public Vector3 getPosition()
    {
        return position;
    }

    public GameObject getGameObject()
    {
        return gameObject;
    }

    public void setGameObject(GameObject gameObjectIn)
    {
        gameObject = gameObjectIn;
    }
    //#endregion
}
