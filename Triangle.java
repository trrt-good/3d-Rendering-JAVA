import java.awt.Color;
public class Triangle 
{
    public Vector3 point1;
    public Vector3 point2;
    public Vector3 point3;
    public Color color = Color.BLACK; 
    public boolean fill;
    public int lineThickness; 

    public Triangle(Vector3 p1, Vector3 p2, Vector3 p3)
    {
        Main.ObjectManager.triangles.add(this);
        point1 = p1;
        point2 = p2;
        point3 = p3;
        fill = true;
        color = Color.BLACK;
        lineThickness = 1;
    }

    public Triangle(Vector3 p1, Vector3 p2, Vector3 p3, Color colorIn)
    {
        Main.ObjectManager.triangles.add(this);
        point1 = p1;
        point2 = p2;
        point3 = p3;
        fill = true;
        color = colorIn;
        lineThickness = 1;
    }

    public Triangle(Vector3 p1, Vector3 p2, Vector3 p3, Color colorIn, int lineThicknessIn)
    {
        Main.ObjectManager.triangles.add(this);
        point1 = p1;
        point2 = p2;
        point3 = p3;
        color = colorIn;
        fill = false;
        lineThickness = lineThicknessIn;
    }

    public Triangle(Vector3 p1, Vector3 p2, Vector3 p3, boolean fillIn)
    {
        Main.ObjectManager.triangles.add(this);
        point1 = p1;
        point2 = p2;
        point3 = p3;
        fill = fillIn;
        lineThickness = 1;
    }

    public Triangle(Vector3 p1, Vector3 p2, Vector3 p3, Color colorIn, boolean fillIn)
    {
        Main.ObjectManager.triangles.add(this);
        point1 = p1;
        point2 = p2;
        point3 = p3;
        fill = fillIn;
        color = colorIn;
        lineThickness = 1;
    }
}
