//all rotations using euler angles are intrinsic and applied in order y-x-z.  
public class EulerAngle 
{
    public double y; //yaw
    public double x; //pitch
    public double z; //roll

    //no-arg constructor default 0, 0, 0
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

    //formats into string, similar to Vector3s 
    public String toString()
    {
        return new String(String.format("[%.2f, %.2f, %.2f]", x, y, z));
    }

    public static EulerAngle subtract(EulerAngle angle1, EulerAngle angle2)
    {
        return new EulerAngle(angle1.x-angle2.x, angle1.y-angle2.y, angle1.z-angle2.z);
    }
}
