public class EulerAngle 
{
    public double x;
    public double y;
    public double z;

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
}
