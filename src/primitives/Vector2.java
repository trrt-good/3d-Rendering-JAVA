package src.primitives;
/**
 * an object for representing 2d points or directions with 
 * double precision. 
 */
public class Vector2 
{
    public double x;
    public double y;

    public Vector2 (double xIn, double yIn)
    {
        x = xIn;
        y = yIn;
    }

    public Vector2()
    {
        x = 0;
        y = 0;
    }
}
