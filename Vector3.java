public class Vector3 //An object which represents 3d points or vectors 
{
    public double x;
    public double y;
    public double z;

    public Vector3(double xIn, double yIn, double zIn)
    {
        x = xIn;
        y = yIn;
        z = zIn;
    }

    public Vector3(Vector3 vector)
    {
        x = vector.x;
        y = vector.y;
        z = vector.z;
    }

    public Vector3()
    {
        x = 0;
        y = 0;
        z = 0;
    }

    public double getMagnitude()
    {
        return Math.sqrt((x*x+z*z) + y*y);
    }

    public double getSqrMagnitude()
    {
        return (x*x+z*z) + y*y;
    }

    public Vector3 getNormalized()
    {
        double magnitude = getMagnitude();
        return new Vector3(x/magnitude, y/magnitude, z/magnitude);
    }

    public void add(Vector3 vectorIn)
    {
        x+= vectorIn.x;
        y+= vectorIn.y;
        z+= vectorIn.z;
    }
    
    public void add(double xIn, double yIn, double zIn)
    {
        x+= xIn;
        y+= yIn;
        z+= zIn;
    }

    public void multiply(double multiplier)
    {
        x*=multiplier;
        y*=multiplier;
        z*=multiplier;
    }

    public String toString()
    {
        return new String(String.format("[%.1f, %.1f, %.1f]", x, y, z));
    }

//============================= static methods ===============================

    public static double dotProduct(Vector3 a, Vector3 b)
    {
        return a.x*b.x+a.y*b.y+a.z*b.z;
    }

    public static Vector3 add(Vector3 a, Vector3 b)
    {
        return new Vector3(a.x+b.x, a.y+b.y, a.z+b.z);
    }

    public static Vector3 subtract(Vector3 a, Vector3 b) //subtracts b from a || to find vector from a start point to a terminal point
    { //a is the terminal point and a is the starting point. 
        return new Vector3(a.x-b.x, a.y-b.y, a.z-b.z);
    }

    public static Vector3 multiply(Vector3 vector, double scalar)
    {
        return new Vector3(vector.x*scalar, vector.y*scalar, vector.z*scalar);
    }

    public static Vector3 negate(Vector3 vector)
    {
        return new Vector3(-vector.x, -vector.y, -vector.z);
    }

    public static Vector3 degAngleToVector(double horizontalAng, double verticalAng)
    {
        return new Vector3(Math.sin(Math.toRadians(horizontalAng)), Math.sin(Math.toRadians(verticalAng)), Math.cos(Math.toRadians(horizontalAng)));
    }

    public static Vector3 radAngleToVector(double horizontalAng, double verticalAng)
    {
        return new Vector3(Math.sin(horizontalAng), Math.sin(verticalAng), Math.cos(horizontalAng));
    }

    public static Vector3 getIntersectionPoint(Vector3 lineDirection, Vector3 linePoint, Plane plane)
    {
        return Vector3.add(linePoint, Vector3.multiply(lineDirection, Vector3.dotProduct(Vector3.subtract(plane.planePoint, linePoint), plane.planeNormal)/Vector3.dotProduct(lineDirection, plane.planeNormal)));
    }

    public static Vector3 divide(Vector3 vector, Vector3 divisorVector)
    {
        return new Vector3();
    }
}
