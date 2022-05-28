package trrt.testing;
import java.util.List;
import java.util.ArrayList;
public class TimingHelper 
{
    private static List<TimingHelper> timingHelpers = new ArrayList<TimingHelper>();
    private String processName;
    private long startTime;
    private long mostRecentProcessTime;
    private long lastProcessTime;
    private long averageProcessTime; 

    public TimingHelper(String processNameIn)
    {   
        TimingHelper.timingHelpers.add(this);
        mostRecentProcessTime = 0;
        lastProcessTime = 0;
        averageProcessTime = 0;
        startTime = 0;
        processName = processNameIn;
    }

    public void startClock()
    {
        startTime = System.nanoTime();
    }

    public void stopClock()
    {
        lastProcessTime = mostRecentProcessTime;
        mostRecentProcessTime = System.nanoTime()-startTime;
        if (lastProcessTime == 0)
            averageProcessTime = mostRecentProcessTime;
        if (lastProcessTime != 0)
            averageProcessTime = (averageProcessTime + mostRecentProcessTime)/2;
    }

    public double getDeltaTime()
    {
        return mostRecentProcessTime/1000000.0;
    }

    public static void printSummary()
    {
        System.out.printf("\n\n%-30s|  %-10s\n\n", "process name", "avg time");
        for (int i = 0; i < timingHelpers.size(); i++)
        {
            System.out.printf("%-30s|  %-10.4fms\n", timingHelpers.get(i).processName, (double)timingHelpers.get(i).averageProcessTime/1000000);
        }
        System.out.println();
    }
}
