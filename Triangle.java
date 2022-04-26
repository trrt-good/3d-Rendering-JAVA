import java.awt.Color;
public class Triangle 
{
    //3d verticies of the triangle. 
    public Vector3 point1;
    public Vector3 point2;
    public Vector3 point3;

    //the default color of the triangle before lighting
    private Color color;

    //the mesh the this triangle is a part of (might be null)
    private Mesh parentMesh;

    //the color of the triangle with lighting calculations. 
    private Color colorWithLighting;

    public Triangle(Mesh parentMeshIn, Vector3 p1, Vector3 p2, Vector3 p3)
    {
        point1 = p1;
        point2 = p2;
        point3 = p3;
        color = Color.BLACK;
        parentMesh = parentMeshIn;
    }

    public Triangle(Mesh parentMeshIn, Vector3 p1, Vector3 p2, Vector3 p3, Color colorIn)
    {
        point1 = p1;
        point2 = p2;
        point3 = p3;
        color = colorIn;
        parentMesh = parentMeshIn;
    }

    public Mesh getMesh()
    {
        return parentMesh;
    }

    public Plane getPlane()
    {
        return new Plane(point1, point2, point3);
    }

    public Vector3 getCenter()
    {
        return new Vector3((point1.x + point2.x + point3.x)/3, (point1.y + point2.y + point3.y)/3, (point1.z + point2.z + point3.z)/3);
    }

    public void setBaseColor(Color colorIn)
    {
        color = colorIn;
    }

    public Color getBaseColor()
    {
        return color;
    }

    public Color getColorWithLighting()
    {
        return colorWithLighting;
    }

    //calculates the color of the triangle accounting for lighting, using the lighting object parameter.
    public void calculateLightingColor(Lighting lighting)
    {
        int brightness = 0;
        int darkness = 0;
        //get the angle between the normal of the triangle face and the direction of the light. 
        double angle = Vector3.getAngleBetween(lighting.lightDirection, Vector3.crossProduct(Vector3.subtract(point1, point2), Vector3.subtract(point2, point3)));

        //determine brightness and darkness.
        if (angle > Math.PI/2)
            brightness = (int)(Math.abs(angle/(Math.PI)-0.5)*(lighting.lightIntensity/100)*255);
        
        if (angle < Math.PI/2)
            darkness = (int)(Math.abs(angle/(Math.PI)-0.5)*(lighting.shadowIntensity/100)*255);
        
        int red = color.getRed() + brightness - darkness;
        int green = color.getGreen() + brightness - darkness;
        int blue = color.getBlue() + brightness - darkness;

        //clamp values
        red = Math.max(0, Math.min(red, 255));
        green = Math.max(0, Math.min(green, 255));
        blue = Math.max(0, Math.min(blue, 255));

        colorWithLighting = new Color(red, green, blue);
    }
}
