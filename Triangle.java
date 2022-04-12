import java.awt.Color;
public class Triangle 
{
    public Vector3 point1;
    public Vector3 point2;
    public Vector3 point3;
    public Color color = Color.BLACK; 
    public boolean fill;
    public int lineThickness; 
    public GameObject parentGameObject;

    private Color colorWithLighting;

    public Triangle(GameObject parentGameObjectIn, Vector3 p1, Vector3 p2, Vector3 p3)
    {
        point1 = p1;
        point2 = p2;
        point3 = p3;
        fill = true;
        color = Color.BLACK;
        lineThickness = 1;
        parentGameObject = parentGameObjectIn;
    }

    public Triangle(GameObject parentGameObjectIn, Vector3 p1, Vector3 p2, Vector3 p3, Color colorIn)
    {
        point1 = p1;
        point2 = p2;
        point3 = p3;
        fill = true;
        color = colorIn;
        lineThickness = 1;
        parentGameObject = parentGameObjectIn;
    }

    public Triangle(GameObject parentGameObjectIn, Vector3 p1, Vector3 p2, Vector3 p3, Color colorIn, int lineThicknessIn)
    {
        point1 = p1;
        point2 = p2;
        point3 = p3;
        color = colorIn;
        fill = false;
        lineThickness = lineThicknessIn;
        parentGameObject = parentGameObjectIn;
    }

    public Triangle(GameObject parentGameObjectIn, Vector3 p1, Vector3 p2, Vector3 p3, boolean fillIn)
    {
        point1 = p1;
        point2 = p2;
        point3 = p3;
        fill = fillIn;
        lineThickness = 1;
        parentGameObject = parentGameObjectIn;
    }

    public Triangle(GameObject parentGameObjectIn, Vector3 p1, Vector3 p2, Vector3 p3, Color colorIn, boolean fillIn)
    {
        point1 = p1;
        point2 = p2;
        point3 = p3;
        fill = fillIn;
        color = colorIn;
        lineThickness = 1;
        parentGameObject = parentGameObjectIn;
    }

    public Plane getPlane()
    {
        return new Plane(point1, point2, point3);
    }

    public void calculateLightingColor(Lighting lighting)
    {
        int brightness = 0;
        int darkness = 0;
        double angle = Vector3.getAngleBetween(lighting.lightDirection, Vector3.crossProduct(Vector3.subtract(point1, point2), Vector3.subtract(point2, point3)));
        if (angle > Math.PI/2)
            brightness = (int)(Math.abs(angle/(Math.PI)-0.5)*(lighting.lightIntensity/100)*255);
        
        if (angle < Math.PI/2)
            darkness = (int)(Math.abs(angle/(Math.PI)-0.5)*(lighting.shadowIntensity/100)*255);
        
        int red = color.getRed() + brightness - darkness;
        int green = color.getGreen() + brightness - darkness;
        int blue = color.getBlue() + brightness - darkness;

        red = Math.max(0, Math.min(red, 255));
        green = Math.max(0, Math.min(green, 255));
        blue = Math.max(0, Math.min(blue, 255));

        colorWithLighting = new Color(red, green, blue);
    }

    public Color getColorWithLighting()
    {
        return colorWithLighting;
    }
}
