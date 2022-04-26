public class Matrix3x3 
{
    //R means row and C means column. R2C3 would be second row third column.
    public final double R1C1, R1C2, R1C3, R2C1, R2C2, R2C3, R3C1, R3C2, R3C3;

    //overloaded constructor which accepts three Vector3s. 
    public Matrix3x3(Vector3 column1, Vector3 column2, Vector3 column3)
    {
        R1C1 = column1.x;    R1C2 = column2.x;    R1C3 = column3.x; 
        R2C1 = column1.y;    R2C2 = column2.y;    R2C3 = column3.y; 
        R3C1 = column1.z;    R3C2 = column2.z;    R3C3 = column3.z; 
    }

    //overloaded constructor which allows all 9 values of the matrix. 
    public Matrix3x3(double r1c1, double r1c2, double r1c3, double r2c1, double r2c2, double r2c3, double r3c1, double r3c2, double r3c3)
    {
        R1C1 = r1c1;    R1C2 = r1c2;    R1C3 = r1c3; 
        R2C1 = r2c1;    R2C2 = r2c2;    R2C3 = r2c3; 
        R3C1 = r3c1;    R3C2 = r3c2;    R3C3 = r3c3; 
    }

    //formats the values in the matrix into a string. 
    public String toString()
    {
        return String.format("\n|%39s\n|%10.2f%10.2f%10.2f%9s\n|%39s\n|%10.2f%10.2f%10.2f%9s\n|%39s\n|%10.2f%10.2f%10.2f%9s\n|%39s\n",
        "|", R1C1, R1C2, R1C3, "|", "|", R2C1, R2C2, R2C3, "|", "|", R3C1, R3C2, R3C3, "|", "|");
    }

    //returns the determinant of the 3x3 matrix. 
    public double getDeterminant()
    {
        return R1C1*(R2C2*R3C3-R2C3*R3C2)-R1C2*(R2C1*R3C3-R2C3*R3C1)+R1C3*(R2C1*R3C2-R2C2*R3C1);
    }

    //returns the cofactor matrix. 
    public Matrix3x3 getCofactorMatrix()
    {
        return new Matrix3x3
        (
            R2C2*R3C3-R2C3*R3C2, -(R2C1*R3C3-R2C3*R3C1), R2C1*R3C2-R2C2*R3C1, 
            -(R1C2*R3C3-R1C3*R3C2), R1C1*R3C3-R1C3*R3C1, -(R1C1*R3C2-R1C2*R3C1), 
            R1C2*R2C3-R1C3*R2C2, -(R1C1*R2C3-R1C3*R2C1), R1C1*R2C2-R1C2*R2C1
        );
    }

    //returns the adjugate matrix, basically just the transposed cofactor matrix.  
    public Matrix3x3 getAdjugateMatrix()
    {
        return new Matrix3x3
        (
            R2C2*R3C3-R2C3*R3C2, -(R1C2*R3C3-R1C3*R3C2), R1C2*R2C3-R1C3*R2C2, 
            -(R2C1*R3C3-R2C3*R3C1), R1C1*R3C3-R1C3*R3C1, -(R1C1*R2C3-R1C3*R2C1), 
            R2C1*R3C2-R2C2*R3C1, -(R1C1*R3C2-R1C2*R3C1), R1C1*R2C2-R1C2*R2C1
        );
    }

    //returns the inverse of the matrix, which is just the adjugate/det
    public Matrix3x3 getInverse()
    {
        return Matrix3x3.multiply(getAdjugateMatrix(), 1/getDeterminant());
    }

    //#region ----------- static methods ------------- 

    //applies matrix m1 to matrix m2 and returns the resulting matrix. order matters!
    public static Matrix3x3 multiply(Matrix3x3 m1, Matrix3x3 m2)
    {
        return new Matrix3x3(
            m1.R1C1*m2.R1C1 + m1.R1C2*m2.R2C1 + m1.R1C3*m2.R3C1,    m1.R1C1*m2.R1C2 + m1.R1C2*m2.R2C2 + m1.R1C3*m2.R3C2,    m1.R1C1*m2.R1C3 + m1.R1C2*m2.R2C3 + m1.R1C3*m2.R3C3, 
            m1.R2C1*m2.R1C1 + m1.R2C2*m2.R2C1 + m1.R2C3*m2.R3C1,    m1.R2C1*m2.R1C2 + m1.R2C2*m2.R2C2 + m1.R2C3*m2.R3C2,    m1.R2C1*m2.R1C3 + m1.R2C2*m2.R2C3 + m1.R2C3*m2.R3C3, 
            m1.R3C1*m2.R1C1 + m1.R3C2*m2.R2C1 + m1.R3C3*m2.R3C1,    m1.R3C1*m2.R1C2 + m1.R3C2*m2.R2C2 + m1.R3C3*m2.R3C2,    m1.R3C1*m2.R1C3 + m1.R3C2*m2.R2C3 + m1.R3C3*m2.R3C3);
    }

    //multiplies a matrix by a scalar value
    public static Matrix3x3 multiply(Matrix3x3 matrix, double scalar)
    {
        return new Matrix3x3
        (
            matrix.R1C1*scalar, matrix.R1C2*scalar, matrix.R1C3*scalar, 
            matrix.R2C1*scalar, matrix.R2C2*scalar, matrix.R2C3*scalar, 
            matrix.R3C1*scalar, matrix.R3C2*scalar, matrix.R3C3*scalar
        );
    }

    //
    public static Matrix3x3 rotationMatrixAxisX(double angle)
    {
        //local variables to mitigate preforming the same slow trig function multiple times. 
        double cosAngle = Math.cos(angle); 
        double sinAngle = Math.sin(angle); 

        /*  | cos -sin   0  |
            | sin  cos   0  |
            |  0    0    1  |  */

        return new Matrix3x3
        (
            1, 0, 0, 
            0, cosAngle, -sinAngle, 
            0, sinAngle, cosAngle
        );
    }

    public static Matrix3x3 rotationMatrixAxisY(double angle)
    {
        //local variables to mitigate preforming the same slow trig function multiple times. 
        double cosAngle = Math.cos(angle); 
        double sinAngle = Math.sin(angle); 

        /*  | cos   0   sin |
            |  0    1    0  |
            |-sin   0   cos |  */

        return new Matrix3x3
        (
            cosAngle, 0, sinAngle, 
            0, 1, 0, 
            -sinAngle, 0, cosAngle
        );
    }

    public static Matrix3x3 rotationMatrixAxisZ(double angle)
    {
        //local variables to mitigate preforming the same slow trig function multiple times. 
        double cosAngle = Math.cos(angle); 
        double sinAngle = Math.sin(angle); 

    /*  | cos -sin   0  |
        | sin  cos   0  |
        |  0    0    1  |  */

        return new Matrix3x3
        (
            cosAngle, -sinAngle, 0, 
            sinAngle, cosAngle, 0, 
            0, 0, 1
        );
    }

    //returns a matrix which can preform a rotation "angle" radians about "axis"
    public static Matrix3x3 axisAngleMatrix(Vector3 axis, double angle)
    {
        axis = axis.getNormalized();

        //local variables to mitigate preforming the same slow trig function multiple times. 
        double cos = Math.cos(angle); 
        double cos1 = 1-cos;
        double sin = Math.sin(angle); 

        return new Matrix3x3
        (
            cos+axis.x*axis.x*cos1, axis.x*axis.y*cos1-axis.z*sin, axis.x*axis.z*cos1+axis.y*sin, 
            axis.y*axis.x*cos1+axis.z*sin, cos+axis.y*axis.y*cos1, axis.y*axis.z*cos1-axis.x*sin, 
            axis.z*axis.x*cos1-axis.y*sin, axis.z*axis.y*cos1+axis.x*sin, cos+axis.z*axis.z*cos1
        );
    }

    // https://en.wikipedia.org/wiki/Rotation_matrix
    // incentric rotation y-x-z order, however note that this program uses the y axis 
    // as vertical, so yaw is rotation about the y-axis and roll is about the z axis.
    public static Matrix3x3 eulerRotation(EulerAngle angle)
    {
        double cos_a = Math.cos(angle.z);
        double sin_a = Math.sin(angle.z);
        double cos_B = Math.cos(angle.y);
        double sin_B = Math.sin(angle.y);
        double cos_y = Math.cos(angle.x);
        double sin_y = Math.sin(angle.x);

        return new Matrix3x3
        (
            cos_a*cos_B, cos_a*sin_B*sin_y-sin_a*cos_y, cos_a*sin_B*cos_y+sin_a*sin_y, 
            sin_a*cos_B, sin_a*sin_B*sin_y+cos_a*cos_y, sin_a*sin_B*cos_y-cos_a*sin_y, 
            -sin_B, cos_B*sin_y, cos_B*cos_y
        );
    }

    //#endregion
}
