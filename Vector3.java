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

    public double getMagnitude()
    {
        return Math.sqrt((x*x+z*z) + y*y);
    }

    public double getSqrMagnitude()
    {
        return (x*x+z*z) + y*y;
    }

}
