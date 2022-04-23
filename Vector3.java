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
        double magnitude = getMagnitude();
        return new Vector3(x/magnitude, y/magnitude, z/magnitude);
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

    public String toString()
    {
        return new String(String.format("[%.2f, %.2f, %.2f]", x, y, z));
    }

//============================= static methods ===============================

    public static double dotProduct(Vector3 a, Vector3 b)
    {
        return a.x*b.x+a.y*b.y+a.z*b.z;
    }

    public static Vector3 crossProduct(Vector3 a, Vector3 b)
    {
        return new Vector3(a.y*b.z-a.z*b.y, a.z*b.x-a.x*b.z, a.x*b.y-a.y*b.x);
    }

    public static Vector3 add(Vector3 a, Vector3 b)
    {
        return new Vector3(a.x+b.x, a.y+b.y, a.z+b.z);
    }

    public static Vector3 subtract(Vector3 a, Vector3 b) //subtracts b from a || to find vector from a start point to a terminal point
    { //a is the terminal point and a is the starting point. 
        return new Vector3(a.x-b.x, a.y-b.y, a.z-b.z);
    }

    public static Vector3 multiply(Vector3 vector, double scalar)
    {
        return new Vector3(vector.x*scalar, vector.y*scalar, vector.z*scalar);
    }

    public static Vector3 negate(Vector3 vector)
    {
        return new Vector3(-vector.x, -vector.y, -vector.z);
    }

    public static Vector3 angleToVector(double yaw, double pitch)
    {
        //new Vector3(Math.cos(yaw)*Math.cos(pitch), Math.sin(pitch), Math.sin(yaw)*Math.cos(pitch));
        return new Vector3(Math.sin(yaw)*Math.cos(pitch), Math.sin(pitch), Math.cos(yaw)*Math.cos(pitch));
    }

    public static Vector3 getIntersectionPoint(Vector3 lineDirection, Vector3 linePoint, Plane plane)
    {
        return Vector3.add(linePoint, Vector3.multiply(lineDirection, Vector3.dotProduct(Vector3.subtract(plane.pointOnPlane, linePoint), plane.normal)/Vector3.dotProduct(lineDirection, plane.normal)));
    }

    public static double getAngleBetween(Vector3 a, Vector3 b) //in radians 
    {
        return Math.acos(Vector3.dotProduct(a, b)/(a.getMagnitude()*b.getMagnitude()));
    }

    public static double distanceToLineSegment(Vector3 point, Vector3 segmentStart, Vector3 segmentEnd)
    {
        return (Vector3.crossProduct(Vector3.subtract(point, segmentStart), Vector3.subtract(segmentStart, segmentEnd)).getMagnitude())/(Vector3.subtract(segmentEnd, segmentStart).getMagnitude());
    }

    public static double distanceToPlane(Vector3 point, Plane plane)
    {
        return Math.abs(plane.normal.x*point.x + plane.normal.y*point.y + plane.normal.z*point.z - plane.normal.x*plane.pointOnPlane.x - plane.normal.y*plane.pointOnPlane.y - plane.normal.z*plane.pointOnPlane.z)/Math.sqrt(plane.normal.x*plane.normal.x + plane.normal.y*plane.normal.y + plane.normal.z* plane.normal.z);
    }

    public static double angleBetweenPlanes(Plane plane1, Plane plane2)
    {
        return Math.abs(Math.acos(Vector3.dotProduct(plane1.normal, plane2.normal)/Math.sqrt((plane1.normal.x*plane1.normal.x + plane1.normal.y*plane1.normal.y + plane1.normal.z*plane1.normal.z)*(plane2.normal.x*plane2.normal.x + plane2.normal.y*plane2.normal.y + plane2.normal.z*plane2.normal.z)))); 
    }

    public static Vector3 rotateAroundXaxis(Vector3 point, double angle) //clockwise
    {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vector3(Vector3.dotProduct(new Vector3(1, 0, 0), point), Vector3.dotProduct(new Vector3(0, cos, -sin), point), Vector3.dotProduct(new Vector3(0, sin, cos), point));
    }

    public static Vector3 rotateAroundYaxis(Vector3 point, double angle)
    {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vector3(Vector3.dotProduct(new Vector3(cos, 0, sin), point), Vector3.dotProduct(new Vector3(0, 1, 0), point), Vector3.dotProduct(new Vector3(-sin, 0, cos), point));
    }

    public static Vector3 rotateAroundZaxis(Vector3 point, double angle)
    {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vector3(Vector3.dotProduct(new Vector3(cos, -sin, 0), point), Vector3.dotProduct(new Vector3(sin, cos, 0), point), Vector3.dotProduct(new Vector3(0, 0, 1), point));
    }

    //returns the orthagonal projection of the inputted vector onto a plane that intersects the origin and 
    //has the normal vector "normalVector". It first calculates the orthagonal projection of the 
    //formula: 
    //
    public static Vector3 projectToPlane(Vector3 vector, Vector3 normalVector)
    {
        Vector3 normalNormalized = (normalVector.getSqrMagnitude() == 1)? normalVector : normalVector.getNormalized();
        return Vector3.subtract(vector, Vector3.multiply(normalNormalized, Vector3.dotProduct(vector, normalNormalized)));
    }

    //returns the inputted vector rotated "angle" degrees around "axis", useful for axis-angle representation.
    //Uses Rodrigues' rotation formula, where a is the angle, e is a unit vector representing 
    //the axis of rotation and v is the vector to be rotated
    //  cos(a)v + sin(a)(e cross v) + (1 - cos(a))(e dot v)e
    public static Vector3 axisAngleRotation(Vector3 axis, double angle, Vector3 vector) 
    {
        axis = (axis.getSqrMagnitude() == 1)? axis : axis.getNormalized(); //makes sure axis is normalized
        double cosAngle = Math.cos(angle); //local variable to mitigate preforming the cos function twice.
        return Vector3.add(Vector3.add(Vector3.multiply(vector, cosAngle), Vector3.multiply(Vector3.multiply(axis, Vector3.dotProduct(vector, axis)), 1-cosAngle)), Vector3.multiply(Vector3.crossProduct(axis, vector), Math.sin(angle)));
    }
}
