import java.util.List;
import java.util.ArrayList;
public class Lighting
{
    public Vector3 lightDirection;
    public double lightIntensity; 
    public double shadowIntensity;

    public Lighting(Vector3 lightDirectionIn, double lightIntensityIn, double shadowIntensityIn)
    {
        lightDirection = lightDirectionIn;
        lightIntensity = lightIntensityIn;
        shadowIntensity = shadowIntensityIn;
    }

    public void update(ArrayList<Mesh> meshes)
    {
        for (int i = 0; i < meshes.size(); i++)
            meshes.get(i).recalculateLighting(this);
    }
}

