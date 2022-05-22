package src.testing;
import java.util.Arrays;

import src.primitives.*;
public class AlgorithmTesting
{
    public static final long ITERATIONS_PER_TEST = (int)Math.pow(2, 2);
    private static int[] array;

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
        array = new int[100000];
        for (int i = 0; i < array.length; i ++)
        {
            array[i] = (int)(Math.random()*500000);
        }

        long start = System.nanoTime();
        
        for(long i = 0; i < ITERATIONS_PER_TEST; i++) 
        {
            int counterArraySize = 0;
            for (int j = 0; j < array.length; j ++)
            {
                counterArraySize = Math.max(array[j], counterArraySize);
            }
            int[] counterArray = new int[counterArraySize+1];
            int[] sortedArray = new int[array.length];
            int counter = 0;
            for (int j = 0; j < array.length; j ++)
            {
                counterArray[array[j]]++;
            }
            for (int j = 0; j < counterArray.length; j++)
            {
                for (int k = 0; k < counterArray[j]; k++)
                {
                    sortedArray[counter] = j;
                    counter++;
                }
            }
        }
        start = System.nanoTime() - start;
        return start;
    }

    public static long test2()
    {
        array = new int[100000];
        for (int i = 0; i < array.length; i ++)
        {
            array[i] = (int)(Math.random()*500000);
        }
        long start = System.nanoTime();
        
        for(long i = 0; i < ITERATIONS_PER_TEST; i++) 
        {
            Arrays.parallelSort(array);
        }
        start = System.nanoTime() - start;
        return start;
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
}