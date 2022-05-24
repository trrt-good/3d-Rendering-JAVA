package src.primitives;
import java.awt.Color;

import src.gameObject.Mesh;
import src.graphics.Lighting;
public class Triangle 
{
    //3d verticies of the triangle. 
    public Vector3 vertex1;
    public Vector3 vertex2;
    public Vector3 vertex3;

    //textureCoords of the triangle. 
    public Vector2 textureCoord1;
    public Vector2 textureCoord2;
    public Vector2 textureCoord3;
    
    //the default color of the triangle before lighting
    private Color color;

    //the mesh the this triangle is a part of (might be null)
    private Mesh parentMesh;

    //the color of the triangle with lighting calculations. 
    private Color colorWithLighting;

    public Triangle(Mesh parentMeshIn, Vector3 v1, Vector3 v2, Vector3 v3)
    {
        vertex1 = v1;
        vertex2 = v2;
        vertex3 = v3;
        color = Color.MAGENTA;
        colorWithLighting = Color.MAGENTA;
        parentMesh = parentMeshIn;
    }

    public Triangle(Mesh parentMeshIn, Vector3 v1, Vector3 v2, Vector3 v3, Color colorIn)
    {
        vertex1 = v1;
        vertex2 = v2;
        vertex3 = v3;
        color = colorIn;
        colorWithLighting = colorIn;
        parentMesh = parentMeshIn;
    }

    public Triangle(Mesh parentMeshIn, Vector3 v1, Vector3 v2, Vector3 v3, Vector2 t1, Vector2 t2, Vector2 t3)
    {
        vertex1 = v1;
        vertex2 = v2;
        vertex3 = v3;
        textureCoord1 = t1;
        textureCoord2 = t2;
        textureCoord3 = t3;
        parentMesh = parentMeshIn;
        color = calculateTextureColor();
        colorWithLighting = color;
    }
    public Mesh getMesh()
    {
        return parentMesh;
    }

    public Plane getPlane()
    {
        return new Plane(vertex1, vertex2, vertex3);
    }

    public Vector3 getCenter()
    {
        return new Vector3
        (
            (vertex1.x + vertex2.x + vertex3.x)/3, 
            (vertex1.y + vertex2.y + vertex3.y)/3,
            (vertex1.z + vertex2.z + vertex3.z)/3
        );
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
        double angle = Vector3.getAngleBetween(lighting.lightDirection, Vector3.crossProduct(Vector3.subtract(vertex1, vertex2), Vector3.subtract(vertex2, vertex3)));

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

    private Color calculateTextureColor()
    {
        double centerX = (textureCoord1.x + textureCoord2.x + textureCoord3.x)/3;
        double centerY = (textureCoord1.y + textureCoord2.y + textureCoord3.y)/3;
        int[] color = new int[4];
        color = parentMesh.getTextureRaster().getPixel((int)(centerX*parentMesh.getTextureRaster().getWidth()), parentMesh.getTextureRaster().getHeight() - (int)(centerY*parentMesh.getTextureRaster().getHeight()), color);
        return new Color(color[0], color[1], color[2]);
    }
}
