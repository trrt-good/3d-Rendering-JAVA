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

    public void multiply(Vector3 multiplier)
    {
        x*=multiplier.x;
        y*=multiplier.y;
        z*=multiplier.z;
    }
}
