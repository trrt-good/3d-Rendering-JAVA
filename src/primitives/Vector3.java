package src.primitives;
/**
 * An object which represents 3d points or directions, and includes many static methods for 3d math.  */ 
public class Vector3 
{    
    public static final Vector3 UP = new Vector3(0, 1, 0);
    public static final Vector3 RIGHT = new Vector3(1, 0, 0);
    public static final Vector3 FORWARD = new Vector3(0, 0, 1);
    public static final Vector3 ZERO = new Vector3(0, 0, 0);

    /**
     * The {@code x} component of the {@code Vector3} in 3d cartesian coordinate space.
     */
    public final double x;

    /**
     * The {@code y} component of the {@code Vector3} in 3d cartensian coordinate space.
     * Represents the vertical axis.
     */
    public final double y;

    /**
     * The {@code z} component of the {@code Vector3} in 3d cartesian coordinate space.
     * Represents the "depth" axis. 
     */
    public final double z; 

    /**
     * Creates a {@code Vector3} object with the specified {@code x}, {@code y} and {@code z} components 
     * @param xIn the {@code x} component of the {@code Vector3} object being made
     * @param yIn the {@code y} component of the {@code Vector3} object being made
     * @param zIn the {@code z} component of the {@code Vector3} object being made
     */
    public Vector3(double xIn, double yIn, double zIn)
    {
        x = xIn;
        y = yIn;
        z = zIn;
    }

    /**
     * Creates a {@code Vector3} object with identical {@code x}, {@code y} and {@code z} components
     * as the specified vector
     * @param vector the vector to be cloned 
     */
    public Vector3(Vector3 vector)
    {
        x = vector.x;
        y = vector.y;
        z = vector.z;
    }

    /**
     * @return the exact magnitude of the vector as a scalar value
     * @see #getSqrMagnitude()
     */
    public double getMagnitude()
    {
        return Math.sqrt(x*x+z*z + y*y);
    }

    /**
     * A (much) faster alternative to the {@link #getMagnitude} method. 
     * This returns the squared magnitude of the vector, which is recommended 
     * for checking if the magnitude is over a certain value, as the other 
     * value can just be squared, and the resulting comparison will be just as
     * accurate and much faster than using {@link #getMagnitude}. Example 
     * where a is a {@code double} value and vector is a {@code Vector3} object:<pre>
     *   if (vector.getSqrMagnitude > a*a)
     *      return true;</pre>
     *
     * <p id="fail-fast">
     * 
     * @return the squared magnitude of the vector
     */
    public double getSqrMagnitude()
    {
        return x*x+z*z + y*y;
    }


    //returns the normalized vector. 
    /**
     * Returns the vector normalized with a magnitude of 1. Note that this
     * doesn't change the {@code x}, {@code y} or {@code z} components. It 
     * simply returns a normalized version of the vector
     * @return returns the normalized vector
     */
    public Vector3 getNormalized()
    {
        if (getSqrMagnitude() != 1)
        {
            double magnitude = getMagnitude();
            return new Vector3(x/magnitude, y/magnitude, z/magnitude);
        }
        else
            return this;
    }
    
    /**
     * adds the specified vector to itself, and also returns itself as the result
     * after the operation. This does change the {@code x}, {@code y} and {@code z} components
     * of the vector. 
     * @param vectorIn the vector to be added
     * @return the vector after the operation. 
     */
    public Vector3 add(Vector3 vectorIn)
    {
        return Vector3.add(this, vectorIn);
    }

    /**
     * multiplies itself by the specified scalar multiplier. Then returns itself.
     * There is no divide method so simply multiply by the reciprocal instead. 
     * @param multiplier the scalar multiplier
     * @return the product
     */
    public Vector3 multiply(double multiplier)
    {
        return Vector3.multiply(this, multiplier);
    }

    /**
     * rotates itself by the specified quaterion and also returns itself 
     * as a result. 
     * @param q the quaternion representing the rotation
     * @return the resulting vector3 after the rotation
     */
    public Vector3 rotate(Quaternion q)
    {
        return Vector3.rotate(this, q);
    }

    /** 
     * @return a vector with negated x, y and z components 
     */
    public Vector3 negate()
    {
        return Vector3.negate(this);
    }

    /**
     * Returns a {@code Vector3} object with with value (x*x, y*y, z*z)
     * where x y and z are the components of the origonal vector.
     * @return the sqaured vector.
     */
    public Vector3 getSquare()
    {
        return new Vector3(x*x, y*y, z*z);
    }

    /**
     * formats the vector into a string for printing. 
     * Uses the format: 
     */
    public String toString()
    {
        return new String(String.format("[%.2f, %.2f, %.2f]", x, y, z));
    }

    public Quaternion toQuaternion()
    {
        return new Quaternion(0, x, y, z);
    }

//============================= static methods ===============================

    /**
     * the magnitude of vector {@code a} projected into vector {@code b} or vice versa 
     * @param a vector 
     * @param b vector
     * @return the dot product between {@code a} and {@code b}
     */
    public static double dotProduct(Vector3 a, Vector3 b)
    {
        return a.x*b.x+a.y*b.y+a.z*b.z;
    }

    /** 
     * the resulting vector is perpendicular to {@code a} and {@code b}
     * note that the cross product of two unit vectors is not necessarily
     * a unit vector too  
     * @param a vector
     * @param b vector
     * @return the cross product of {@code a} and {@code b}
     */ 
    public static Vector3 crossProduct(Vector3 a, Vector3 b)
    {
        return new Vector3(a.y*b.z-a.z*b.y, a.z*b.x-a.x*b.z, a.x*b.y-a.y*b.x);
    }

    /**
     * @param a vector
     * @param b vector
     * @return the sum of {@code a} and {@code b}
     */
    public static Vector3 add(Vector3 a, Vector3 b)
    {
        return new Vector3(a.x+b.x, a.y+b.y, a.z+b.z);
    }
    
    /**
     * preforms vector {@code a} minus {@code b}. This will return the 
     * a vector who's start point is {@code b} and terminal point is {@code a}
     * @param a vector 
     * @param b vector
     * @return {@code a - b}
     */
    public static Vector3 subtract(Vector3 a, Vector3 b) 
    { 
        return new Vector3(a.x-b.x, a.y-b.y, a.z-b.z);
    }

    /**
     * @param vector the vector to be scaled
     * @param scalar the scale factor 
     * @return returns {@code vector} scaled by {@code scalar}
     */
    public static Vector3 multiply(Vector3 vector, double scalar)
    {
        return new Vector3(vector.x*scalar, vector.y*scalar, vector.z*scalar);
    }

    /**
     * @param vector the vector to be negated 
     * @return the negated vector using the negation matrix. 
     */
    public static Vector3 negate(Vector3 vector)
    {
        return new Vector3(-vector.x, -vector.y, -vector.z);
    }

    /**
     * @param yaw yaw of the vector 
     * @param pitch pitch of the vector 
     * @return returns a vector with the specified rotation
     */
    public static Vector3 angleToVector(double yaw, double pitch)
    {
        double cosPitch = Math.cos(pitch);
        return new Vector3(Math.sin(yaw)*cosPitch, Math.sin(pitch), Math.cos(yaw)*cosPitch);
    }

    /**
     * Essentially shoots a ray from {@code linePoint} in the direction {@code lineDirection} and returns 
     * the intersection of that ray with the plane {@code plane}
     * @param lineDirection the direction of the ray
     * @param linePoint the point that the ray is shot from
     * @param plane the plane which the ray intersects with 
     * @return returns the point at which the vector {@code lineDirection} starting at point {@code linePoint} intersects {@code plane}
     */
    public static Vector3 getIntersectionPoint(Vector3 lineDirection, Vector3 linePoint, Plane plane)
    {
        return Vector3.add(linePoint, Vector3.multiply(lineDirection, Vector3.dotProduct(Vector3.subtract(plane.pointOnPlane, linePoint), plane.normal)/Vector3.dotProduct(lineDirection, plane.normal)));
    }

    /**
     * @param a vector a 
     * @param b vector b
     * @return returns the smallest angle between two vectors. 
     */
    public static double getAngleBetween(Vector3 a, Vector3 b) 
    {
        return Math.acos(Vector3.dotProduct(a, b)/(a.getMagnitude()*b.getMagnitude()));
    }

    /**
     * @param point the point  
     * @param lineP1 a point on a line 
     * @param lineP2 the other point on a line
     * @return the shortest distance from {@code point} to the line defined by the two points, {@code lineP1} and {@code lineP2}
     */ 
    public static double distanceToLine(Vector3 point, Vector3 lineP1, Vector3 lineP2)
    {
        return (Vector3.crossProduct(Vector3.subtract(point, lineP1), Vector3.subtract(lineP1, lineP2)).getMagnitude())/(Vector3.subtract(lineP2, lineP1).getMagnitude());
    }

    /**
     * returns the shortest distance from {@code point} to {@code plane}. Basically creates a vector from a point on the
     * plane to the inputted {@code point} and projects that onto the normal vector of the {@code plane}, and the magnitude of that
     * is the distance. Removing the absolute results in the signed distance, which is positive if {@code point} is on the
     * same side of the plane as it's normal vector, and negative otherwise. 
     * @param point a point 
     * @param plane the plane 
     * @return the shortest distance between {@code point} and {@code plane}
     */
    public static double distanceToPlane(Vector3 point, Plane plane)
    {
        return Math.abs(plane.normal.x*point.x + plane.normal.y*point.y + plane.normal.z*point.z - plane.normal.x*plane.pointOnPlane.x - plane.normal.y*plane.pointOnPlane.y - plane.normal.z*plane.pointOnPlane.z)/Math.sqrt(plane.normal.x*plane.normal.x + plane.normal.y*plane.normal.y + plane.normal.z* plane.normal.z);
    }

    /**
     * rotates the given point around the X axis using the matrix:
     * <pre>
     * |  1    0    0  |
     *|  0   cos -sin |
     *|  1   sin  cos |</pre>
     * note that using the {@link Matrix3x3} class and it's corresponding method
     * {@link Matrix3x3#rotationMatrixAxisZ(double)} is much faster for rotating
     * multiple points 
     * @param point the vector to be rotated
     * @param angle the angle of rotation in radians 
     * @return the rotated result
     */
    public static Vector3 rotateAroundXaxis(Vector3 point, double angle) //clockwise
    {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vector3(Vector3.dotProduct(new Vector3(1, 0, 0), point), Vector3.dotProduct(new Vector3(0, cos, -sin), point), Vector3.dotProduct(new Vector3(0, sin, cos), point));
    }

    /**
     * rotates the given point around the Y axis using the matrix:
     * <pre>
     * | cos   0   sin |
     *|  0    1    0  |
     *|-sin   0   cos |</pre>
     * note that using the {@link Matrix3x3} class and it's corresponding method
     * {@link Matrix3x3#rotationMatrixAxisZ(double)} is much faster for rotating
     * multiple points 
     * @param point the vector to be rotated
     * @param angle the angle of rotation in radians 
     * @return the rotated result
     */
    public static Vector3 rotateAroundYaxis(Vector3 point, double angle)
    {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vector3(Vector3.dotProduct(new Vector3(cos, 0, sin), point), Vector3.dotProduct(new Vector3(0, 1, 0), point), Vector3.dotProduct(new Vector3(-sin, 0, cos), point));
    }

    /**
     * rotates the given point around the Z axis using the matrix:
     * <pre>
     * | cos -sin   0  |
     *| sin  cos   0  |
     *|  0    0    1  |</pre>
     * note that using the {@link Matrix3x3} class and it's corresponding method
     * {@link Matrix3x3#rotationMatrixAxisZ(double)} is much faster for rotating
     * multiple points 
     * @param point the vector to be rotated
     * @param angle the angle of rotation in radians 
     * @return the rotated result
     */
    public static Vector3 rotateAroundZaxis(Vector3 point, double angle)
    {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vector3(Vector3.dotProduct(new Vector3(cos, -sin, 0), point), Vector3.dotProduct(new Vector3(sin, cos, 0), point), Vector3.dotProduct(new Vector3(0, 0, 1), point));
    }

    /**
     * returns the orthagonal projection of the inputted vector onto a plane that intersects the origin and 
     * has the normal vector {@code planeNormal}. It first calculates the orthagonal projection of the inputted vector
     * onto the normal vector directly, then subtracts that vector from the inputted vector which gives the 
     * orthagonal projection onto a plane. 
     * formula where {@code u} is the inputted vector and {@code n} is the normal vector: <pre>
     *   projPlane(u) = u-[(u dot n)/(||n||^2)]n </pre>
     * @param vector the vector to be projected
     * @param planeNormal a vector normal to the plane
     * @return {@code vector} projected onto a plane {@code planeNormal}
     */
    public static Vector3 projectToPlane(Vector3 vector, Vector3 planeNormal)
    {
        return Vector3.subtract(vector, Vector3.multiply(planeNormal, Vector3.dotProduct(vector, planeNormal)/planeNormal.getSqrMagnitude()));
    }

    /**
     * projects a orthagonally onto b <pre>
     * projb(a) = ((a dot b)/(b dot b))b </pre>
     * @param a vector to be projected 
     * @param b vector recieving the projection 
     * @return a projected onto b 
     */
    public static Vector3 projectToVector(Vector3 a, Vector3 b)
    {
        return Vector3.multiply(b, Vector3.dotProduct(b, a)/b.getSqrMagnitude());
    }

    /**
     * linearly interpolates between two vectors. Interpolated value is: <pre>
     *      start + (end-start)*time</pre>
     * time value of 1 would return the end result, and 0 would return the start
     * @param start the starting value for interpolation 
     * @param end the ending value of the interpolation 
     * @param time the amount of interpolation as a range from 0 to 1
     * @return the interpolated Vector 
     */
    public static Vector3 lerp(Vector3 start, Vector3 end, double time)
    {
        return Vector3.add(start, Vector3.multiply(Vector3.subtract(end, start), time));
    }

    /**
     * returns the inputted vector rotated {@code angle} degrees around {@code axis}, useful for axis-angle representation.
     * Uses Rodrigues' rotation formula, where a is the angle, e is a unit vector representing 
     * the axis of rotation and v is the vector to be rotated:<pre>
     *   rotV = cos(a)v + sin(a)(e cross v) + (1 - cos(a))(e dot v)e</pre>
     * <p id="fail-fast">
     * This is intended for single time and single point use. Using this method to transform an array of points 
     * may be very slow. Instead, generate a single {@code Matrix3x3} using the {@link Matrix3x3#axisAngleMatrix(Vector3, double)}
     * method and apply that matrix to all the desired points.  
     * @param axis the axis of rotation as a Vector3. This will be normalized if not already
     * @param angle the angle of rotation in radians 
     * @param vector the {@code Vector3} object for the rotation to be applied to 
     * @return the rotated vector 
     */
    public static Vector3 axisAngleRotation(Vector3 axis, double angle, Vector3 vector) 
    {
        axis = axis.getNormalized(); //makes sure axis is normalized
        double cosAngle = Math.cos(angle); //local variable to mitigate preforming the cos function twice.
        return Vector3.add(Vector3.add(Vector3.multiply(vector, cosAngle), Vector3.multiply(Vector3.multiply(axis, Vector3.dotProduct(vector, axis)), 1-cosAngle)), Vector3.multiply(Vector3.crossProduct(axis, vector), Math.sin(angle)));
    }

    /**
     * multiplies the vector by the matrix 
     * @param matrix matrix multiplier
     * @param vector vector to be multiplied
     * @return the transformed vector 
     */
    public static Vector3 applyMatrix(Matrix3x3 matrix, Vector3 vector)
    {
        return new Vector3
        (
            vector.x*matrix.R1C1 + vector.y*matrix.R1C2 + vector.z*matrix.R1C3,
            vector.x*matrix.R2C1 + vector.y*matrix.R2C2 + vector.z*matrix.R2C3,
            vector.x*matrix.R3C1 + vector.y*matrix.R3C2 + vector.z*matrix.R3C3
        );
    }

    /**
     * rotates the specified vector by the specified quaternion 
     * @param v the vector to be rotated
     * @param q the rotation to be applied as a quaternion
     * @return the resulting vector 
     */
    public static Vector3 rotate(Vector3 v, Quaternion q)
    {
        final double w = -(q.x*v.x+q.y*v.y+q.z*v.z);
        final double x = q.w*v.x + q.y*v.z-q.z*v.y;
        final double y = q.w*v.y + q.z*v.x-q.x*v.z;
        final double z = q.w*v.z + q.x*v.y-q.y*v.x;

        return new Vector3
        (
            q.w*x - w*q.x - y*q.z + z*q.y,
            q.w*y - w*q.y - z*q.x + x*q.z,
            q.w*z - w*q.z - x*q.y + y*q.x
        );
    }
}
