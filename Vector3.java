//An object which represents 3d points or directions
//The Vector3 class also contains many static methods for 3d math.  
public class Vector3 
{
    public double x; //while looking north (0, 0, 1), the x-axis would run to the left and right or east/west
    public double y; //the y axis is vertical 
    public double z; //the "depth" axis which runs north/south

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
        return x*x+z*z + y*y;
    }

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

    //changes itself, as well as returning the result
    public Vector3 add(Vector3 vectorIn)
    {
        x+= vectorIn.x;
        y+= vectorIn.y;
        z+= vectorIn.z;
        return this;
    }

    //changes itself, as well as returning the result
    public Vector3 multiply(double multiplier)
    {
        x*=multiplier;
        y*=multiplier;
        z*=multiplier;
        return this;
    }


    public String toString()
    {
        return new String(String.format("[%.2f, %.2f, %.2f]", x, y, z));
    }

//============================= static methods ===============================

    //returns the dot product of two vectors.
    public static double dotProduct(Vector3 a, Vector3 b)
    {
        return a.x*b.x+a.y*b.y+a.z*b.z;
    }

    //returns the cross product of two vecotrs. 
    public static Vector3 crossProduct(Vector3 a, Vector3 b)
    {
        return new Vector3(a.y*b.z-a.z*b.y, a.z*b.x-a.x*b.z, a.x*b.y-a.y*b.x);
    }

    //returns the sum of two vectors. 
    public static Vector3 add(Vector3 a, Vector3 b)
    {
        return new Vector3(a.x+b.x, a.y+b.y, a.z+b.z);
    }
    
    //subtracts b from a in order to find vector from a start point to a terminal point
    //a is the terminal point and b is the start point. 
    public static Vector3 subtract(Vector3 a, Vector3 b) 
    { 
        return new Vector3(a.x-b.x, a.y-b.y, a.z-b.z);
    }

    //returns "vector" scaled by "scalar"
    public static Vector3 multiply(Vector3 vector, double scalar)
    {
        return new Vector3(vector.x*scalar, vector.y*scalar, vector.z*scalar);
    }

    //returns the negated vector using the negation matrix. 
    public static Vector3 negate(Vector3 vector)
    {
        return new Vector3(-vector.x, -vector.y, -vector.z);
    }

    //returns a vector with the specified rotation
    public static Vector3 angleToVector(double yaw, double pitch)
    {
        return new Vector3(Math.sin(yaw)*Math.cos(pitch), Math.sin(pitch), Math.cos(yaw)*Math.cos(pitch));
    }

    //returns the point at which the vector "lineDirection" starting at point "linePoint" intersects "plane"
    public static Vector3 getIntersectionPoint(Vector3 lineDirection, Vector3 linePoint, Plane plane)
    {
        return Vector3.add(linePoint, Vector3.multiply(lineDirection, Vector3.dotProduct(Vector3.subtract(plane.pointOnPlane, linePoint), plane.normal)/Vector3.dotProduct(lineDirection, plane.normal)));
    }

    //returns the smallest angle between two vectors. 
    public static double getAngleBetween(Vector3 a, Vector3 b) 
    {
        return Math.acos(Vector3.dotProduct(a, b)/(a.getMagnitude()*b.getMagnitude()));
    }

    //returns the shorted distance from "point" to the line defined by the two points, "lineP1" and "lineP2". 
    public static double distanceToLine(Vector3 point, Vector3 lineP1, Vector3 lineP2)
    {
        return (Vector3.crossProduct(Vector3.subtract(point, lineP1), Vector3.subtract(lineP1, lineP2)).getMagnitude())/(Vector3.subtract(lineP2, lineP1).getMagnitude());
    }

    //returns the shortest distance from "point" to "plane". Basically makes a vector from a point on the
    //plane to the inputted point and projects that onto the normal vector of the plane, and the magnitude of that
    //is the distance. Removing the absolute results in the signed distance, which is positive if "point" is on the
    //same side of the plane as it's normal vector, and negative otherwise. 
    public static double distanceToPlane(Vector3 point, Plane plane)
    {
        return Math.abs(plane.normal.x*point.x + plane.normal.y*point.y + plane.normal.z*point.z - plane.normal.x*plane.pointOnPlane.x - plane.normal.y*plane.pointOnPlane.y - plane.normal.z*plane.pointOnPlane.z)/Math.sqrt(plane.normal.x*plane.normal.x + plane.normal.y*plane.normal.y + plane.normal.z* plane.normal.z);
    }

    //Elemental rotation methods: returns a vector rotated "angle" degrees around either the x y or z axis.
    //intended use for rotating singular points once. For multiple points or multiple rotations, generate a single 
    //Matrix3x3 object that represents the transformation, and apply that matrix to all the points, which is much faster.
    
    /*  |  1    0    0  |
        |  0   cos -sin |
        |  1   sin  cos |  */
    
    public static Vector3 rotateAroundXaxis(Vector3 point, double angle) //clockwise
    {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vector3(Vector3.dotProduct(new Vector3(1, 0, 0), point), Vector3.dotProduct(new Vector3(0, cos, -sin), point), Vector3.dotProduct(new Vector3(0, sin, cos), point));
    }

    /*  | cos   0   sin |
        |  0    1    0  |
        |-sin   0   cos |  */
    public static Vector3 rotateAroundYaxis(Vector3 point, double angle)
    {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vector3(Vector3.dotProduct(new Vector3(cos, 0, sin), point), Vector3.dotProduct(new Vector3(0, 1, 0), point), Vector3.dotProduct(new Vector3(-sin, 0, cos), point));
    }

    /*  | cos -sin   0  |
        | sin  cos   0  |
        |  0    0    1  |  */
    public static Vector3 rotateAroundZaxis(Vector3 point, double angle)
    {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vector3(Vector3.dotProduct(new Vector3(cos, -sin, 0), point), Vector3.dotProduct(new Vector3(sin, cos, 0), point), Vector3.dotProduct(new Vector3(0, 0, 1), point));
    }

    //returns the orthagonal projection of the inputted vector onto a plane that intersects the origin and 
    //has the normal vector "normalVector". It first calculates the orthagonal projection of the inputted vector
    //onto the normal vector directly, then subtracts that vector from the inputted vector which gives the 
    //orthagonal projection onto a plane. 
    //formula where u is the inputted vector and n is the normal vector: 
    //  projPlane(u) = u-[(u dot n)/(||n||^2)]n
    public static Vector3 projectToPlane(Vector3 vector, Vector3 normalVector)
    {
        return Vector3.subtract(vector, Vector3.multiply(normalVector, Vector3.dotProduct(vector, normalVector)/normalVector.getSqrMagnitude()));
    }

    //returns the inputted vector rotated "angle" degrees around "axis", useful for axis-angle representation.
    //Uses Rodrigues' rotation formula, where a is the angle, e is a unit vector representing 
    //the axis of rotation and v is the vector to be rotated
    //  vrot = cos(a)v + sin(a)(e cross v) + (1 - cos(a))(e dot v)e
    //This is intended for single time and single point use. Using this method to transform an array of points 
    //may be very slow. Instead, generate a single Matrix3x3 object to preform the desired transformation on all points.  
    public static Vector3 axisAngleRotation(Vector3 axis, double angle, Vector3 vector) 
    {
        axis = axis.getNormalized(); //makes sure axis is normalized
        double cosAngle = Math.cos(angle); //local variable to mitigate preforming the cos function twice.
        return Vector3.add(Vector3.add(Vector3.multiply(vector, cosAngle), Vector3.multiply(Vector3.multiply(axis, Vector3.dotProduct(vector, axis)), 1-cosAngle)), Vector3.multiply(Vector3.crossProduct(axis, vector), Math.sin(angle)));
    }

    public static Vector3 applyMatrix(Matrix3x3 matrix, Vector3 vector)
    {
        return new Vector3
        (
        vector.x*matrix.R1C1 + vector.y*matrix.R1C2 + vector.z*matrix.R1C3,
        vector.x*matrix.R2C1 + vector.y*matrix.R2C2 + vector.z*matrix.R2C3,
        vector.x*matrix.R3C1 + vector.y*matrix.R3C2 + vector.z*matrix.R3C3
        );
    }
}
