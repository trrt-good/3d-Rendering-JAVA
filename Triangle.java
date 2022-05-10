import java.awt.Color;
public class Triangle 
{
    //3d verticies of the triangle. 
    public Vertex vertex1;
    public Vertex vertex2;
    public Vertex vertex3;

    //the default color of the triangle before lighting
    private Color color;

    //the mesh the this triangle is a part of (might be null)
    private Mesh parentMesh;

    //the color of the triangle with lighting calculations. 
    private Color colorWithLighting;

    public Triangle(Mesh parentMeshIn, Vertex v1, Vertex v2, Vertex v3)
    {
        vertex1 = v1;
        vertex2 = v2;
        vertex3 = v3;
        color = Color.BLACK;
        parentMesh = parentMeshIn;
    }

    public Triangle(Mesh parentMeshIn, Vertex v1, Vertex v2, Vertex v3, Color colorIn)
    {
        vertex1 = v1;
        vertex2 = v2;
        vertex3 = v3;
        color = colorIn;
        parentMesh = parentMeshIn;
    }

    public Mesh getMesh()
    {
        return parentMesh;
    }

    public Plane getPlane()
    {
        return new Plane(vertex1.getWordCoords(), vertex2.getWordCoords(), vertex3.getWordCoords());
    }

    public boolean inView()
    {
        return vertex1.inView && vertex2.inView && vertex3.inView;
    }

    public Vector3 getCenter()
    {
        return new Vector3
        (
            (vertex1.getWordCoords().x + vertex2.getWordCoords().x + vertex3.getWordCoords().x)/3, 
            (vertex1.getWordCoords().y + vertex2.getWordCoords().y + vertex3.getWordCoords().y)/3,
            (vertex1.getWordCoords().z + vertex2.getWordCoords().z + vertex3.getWordCoords().z)/3
        );
    }

    public double getDistanceToCam()
    {
        return (vertex1.getCamDistance() + vertex2.getCamDistance() + vertex3.getCamDistance())/3;
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
        double angle = Vector3.getAngleBetween(lighting.lightDirection, Vector3.crossProduct(Vector3.subtract(vertex1.getWordCoords(), vertex2.getWordCoords()), Vector3.subtract(vertex2.getWordCoords(), vertex3.getWordCoords())));

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
