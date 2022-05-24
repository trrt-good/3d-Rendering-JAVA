package src.testing;

import src.*;
import src.primitives.Matrix3x3;
import src.primitives.Quaternion;
//import org.apache.commons.math3.util.FastMath;
import src.primitives.Vector3;

public class AlgorithmTesting
{
    public static final long ITERATIONS_PER_TEST = (int)Math.pow(10,9);

    public static void main(String[] args) 
    {
        runTests(100);
    }

    public static void runTests(int numberOfTests)
    {
        System.out.println("\nworking... ");
        int test1Wins = 0;
        int test2Wins = 0;
        double diffSum = 0;
        double time1Sum = 0;
        double time2Sum = 0;
        for (int i = 0; i < numberOfTests; i ++)
        {
            long test1Time = test1();
            long test2Time = test2();
    
            if (test2Time > test1Time)
            {
                test1Wins ++;
            }
            else
            {
                test2Wins++;
            }

            System.out.printf("\r%d/%d tests\ttime est: %s      ", i+1, numberOfTests, getTime((test1Time+test2Time)*(numberOfTests-i)));

            diffSum += test2Time-test1Time;
            time1Sum += test1Time;
            time2Sum += test2Time;
        }
        System.out.printf("\r%d/%d all tests complete        ", numberOfTests, numberOfTests);
        System.out.println("\n\n-------- SUMMARY --------\n");
        System.out.println("Winrate: \n\tTest1 wins: " + test1Wins + "\n\tTest2 wins: " + test2Wins);
        System.out.println("Average Difference: " + Math.abs(diffSum/numberOfTests));
        System.out.println("Average Test1 Time: " + (int)(time1Sum/numberOfTests));
        System.out.println("Average Test2 Time: " + (int)(time2Sum/numberOfTests));
        System.out.println
        (
            (test2Wins > test1Wins)? 
            (String.format("Test2 is %.5f times faster than Test1", (float)(time1Sum/numberOfTests)/(time2Sum/numberOfTests))) : 
            (String.format("Test1 is %.5f times faster than Test2", (float)(time2Sum/numberOfTests)/(time1Sum/numberOfTests)))
        );
    }

    public static long test1()
    {
        long start = System.nanoTime();
        Matrix3x3 m = createRotationMatrix(1, -1);
        for(long i = 0; i < ITERATIONS_PER_TEST; i++) 
        {
            Vector3.applyMatrix(m, Vector3.UP);
        }

        start = System.nanoTime() - start;
        return start;
    }

    public static long test2()
    {
        long start = System.nanoTime();
        Quaternion q = createRotationQuaternion(1, -1);
        for(long i = 0; i < ITERATIONS_PER_TEST; i++) 
        {
            Vector3.rotate(Vector3.UP, q);
        }

        start = System.nanoTime() - start;
        return start;
    }

    public static String getTime(long nanoseconds)
    {
        double ms = nanoseconds/1000000.0;
        if (ms < 1000)
        {
            return String.format("%.2fms", ms);
        }
        else if (ms >= 60000)
        {
            return String.format("%d min %d seconds", (int)(ms/60000), (int)((ms%6000)/1000));
        }
        else 
        {
            return String.format("%d seconds", (int)(ms/1000));
        }
    }

    public static Quaternion createRotationQuaternion(double pitch, double yaw)
    {
        //x axis rotation first
        pitch = Math.sin(pitch/2);
        double w1 = Math.sqrt(1-pitch*pitch);

        //y axis rotation
        yaw = Math.sin(yaw/2);
        double w2 = Math.sqrt(1-yaw*yaw);

        return new Quaternion(w1*w2, w2*pitch, w1*yaw, pitch*yaw);  
    }

    public static Matrix3x3 createRotationMatrix(double pitch, double yaw)
    {
        final double sinYaw = Math.sin(yaw); 
        final double cosYaw = Math.sqrt(1-sinYaw*sinYaw); //same as math.cos function

        final double sinPitch = Math.sin(pitch); 
        final double cosPitch = Math.sqrt(1-sinYaw*sinYaw); //same as math.cos function

        return new Matrix3x3(
            cosYaw, sinYaw*sinPitch, sinYaw*cosPitch, 
            0, cosPitch, -sinPitch, 
            -sinYaw, cosYaw*sinPitch, cosYaw*cosPitch);
    }

}