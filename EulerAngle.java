public class EulerAngle 
{
    public double x;
    public double y;
    public double z;

    public EulerAngle()
    {
        x = 0;
        y = 0;
        z = 0;
    }

    public EulerAngle(double xIn, double yIn, double zIn)
    {
        x = xIn;
        y = yIn;
        z = zIn;
    }

    public EulerAngle add(EulerAngle angle)
    {
        return new EulerAngle(x + angle.x, y + angle.y, z + angle.z);
    }

    public EulerAngle multiply(double multiplier)
    {
        return new EulerAngle(x*multiplier, y*multiplier, z*multiplier);
    }

    public String toString()
    {
        return new String(String.format("[%.2f, %.2f, %.2f]", x, y, z));
    }
}
