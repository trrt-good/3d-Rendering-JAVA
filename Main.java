import java.util.List;
import java.util.ArrayList;
public class Main 
{
    public static void main(String [] args)
    {
        RenderingManager.createWindowAndPanel("Test");
    }

    public static class ObjectManager
    {
        public static List<Point3> renderPoint3s = new ArrayList<Point3>();
    }
}
