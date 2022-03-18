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

    public static double dotProduct(Vector3 a, Vector3 b)
    {
        return a.x*b.x+a.y*b.y+a.z*b.z;
    }

    public static Vector3 add(Vector3 a, Vector3 b)
    {
        return new Vector3(a.x+b.x, a.y+b.y, a.z+b.z);
    }

    public static Vector3 subtract(Vector3 a, Vector3 b) //subtracts b from a
    {
        return new Vector3(a.x-b.x, a.y-b.y, a.z-b.z);
    }

    public static Vector3 multiply(Vector3 vector, double scalar)
    {
        return new Vector3(vector.x*scalar, vector.y*scalar, vector.z*scalar);
    }
}
