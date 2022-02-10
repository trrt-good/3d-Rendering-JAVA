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
        public static List<Triangle> triangles = new ArrayList<Triangle>();
    }
}
