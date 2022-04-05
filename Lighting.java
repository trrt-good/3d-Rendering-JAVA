import java.util.List;
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

    public void update(List<GameObject> objects)
    {
        for (int i = 0; i < objects.size(); i++)
            objects.get(i).recalculateLighting(this);
    }
}

