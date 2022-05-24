package src.primitives;

public class Quaternion 
{
    public static final Quaternion IDENTITY = new Quaternion(1, new Vector3(0, 0, 0));

    /**
     * scalar part of the quaternion
     */
    public final double w;
    
    public final double x;
    public final double y;
    public final double z;

    /**
     * 
     * @param axis
     * @param angle angle in radians 
     */
    public Quaternion(double angle, Vector3 axis)
    {
        axis = (axis.getSqrMagnitude() != 1)? axis.getNormalized() : axis;
        angle = Math.sin(angle/2);
        w = Math.sqrt(1-angle*angle); //equal to doing Math.cos() of the origonal angle but faster

        double sinAngle = angle; 
        x = axis.x*sinAngle;
        y = axis.y*sinAngle;
        z = axis.z*sinAngle;
    }

    public Quaternion(double wIn, double iIn, double jIn, double kIn)
    {
        w = wIn;

        x = iIn;
        y = jIn;
        z = kIn;
    }

    public Quaternion getInverse()
    {
        return new Quaternion(w, -x, -y, -z);
    }

    public String toString()
    {
        return new String(String.format("[%.3f, %.3f, %.3f, %.3f]", w, x, y, z));
    }

    public static Quaternion multiply(Quaternion q1, Quaternion q2)
    {
        return new Quaternion(
            q1.w*q2.w-(q1.x*q2.x+q1.y*q2.y+q1.z*q2.z),   //w
            q2.w*q1.x + q1.w*q2.x + q1.y*q2.z-q1.z*q2.y,  //x
            q2.w*q1.y + q1.w*q2.y + q1.z*q2.x-q1.x*q2.z,  //y
            q2.w*q1.z + q1.w*q2.z + q1.x*q2.y-q1.y*q2.x);  //z
            
    }

    public static Quaternion toQuaternion(double pitch, double yaw, double roll)
    {
        double sy = Math.sin(roll * 0.5);
        double cy = Math.sqrt(1-sy*sy);
        double sp = Math.sin(yaw * 0.5);
        double cp = Math.sqrt(1-sp*sp);
        double sr = Math.sin(pitch * 0.5);
        double cr = Math.sqrt(1-sr*sr);

        return new Quaternion
        (
            cr * cp * cy + sr * sp * sy, 
            sr * cp * cy - cr * sp * sy, 
            cr * sp * cy + sr * cp * sy, 
            cr * cp * sy - sr * sp * cy
        );
    }
}
