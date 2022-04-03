import java.util.List;
public class Lighting
{
    public Vector3 direction;
    public double intensity; 
    public double shadowIntensity;
    public double shadingHardness;

    public Lighting(Vector3 lightDirectionIn, double lightIntensityIn, double shadowIntensityIn)
    {
        direction = lightDirectionIn;
        intensity = lightIntensityIn;
        shadowIntensity = shadowIntensityIn;
    }

    public void update(List<GameObject> objects)
    {
        for (int i = 0; i < objects.size(); i++)
            objects.get(i).recalculateLighting(this);
    }
}

