package src.primitives;
/**
 * an object for representing 2d points or directions with 
 * double precision. 
 */
public class Vector2 
{
    public static final Vector2 ZERO = new Vector2(0, 0);
    /**
     * x component of the vector2 with double accuracy 
     */
    public final double x;

    /**
     * y component of the vector2 with double accuracy 
     */
    public final double y;

    public Vector2 (double xIn, double yIn)
    {
        x = xIn;
        y = yIn;
    }
}
