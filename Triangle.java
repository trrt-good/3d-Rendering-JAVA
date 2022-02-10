import java.awt.Color;
public class Triangle 
{
    public Vector3 point1;
    public Vector3 point2;
    public Vector3 point3;
    public Color color = Color.BLACK; 
    public boolean fill;

    public Triangle(Vector3 p1, Vector3 p2, Vector3 p3)
    {
        Main.ObjectManager.triangles.add(this);
        point1 = p1;
        point2 = p2;
        point3 = p3;
    }

    public Triangle(Vector3 p1, Vector3 p2, Vector3 p3, Color colorIn)
    {
        Main.ObjectManager.triangles.add(this);
        point1 = p1;
        point2 = p2;
        point3 = p3;
        color = colorIn;
    }

    public Triangle(Vector3 p1, Vector3 p2, Vector3 p3, boolean fillIn)
    {
        Main.ObjectManager.triangles.add(this);
        point1 = p1;
        point2 = p2;
        point3 = p3;
        fill = fillIn;
    }

    public Triangle(Vector3 p1, Vector3 p2, Vector3 p3, Color colorIn, boolean fillIn)
    {
        Main.ObjectManager.triangles.add(this);
        point1 = p1;
        point2 = p2;
        point3 = p3;
        fill = fillIn;
        color = colorIn;
    }
}
