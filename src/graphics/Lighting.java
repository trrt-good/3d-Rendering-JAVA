package src.graphics;
import java.util.ArrayList;

import src.gameObject.GameObject;
import src.primitives.Vector3;
public class Lighting
{
    /**direction that the light faces. */
    public Vector3 lightDirection;

    /**how bright should the illuminated side of meshes be? */
    public double lightIntensity; 

    /**how dark should the unilluminated side of meshes be? */
    public double shadowIntensity;

    /**
     * creates a lighting object 
     * @param lightDirectionIn direction of light, which will be normalized automatically
     * @param lightIntensityIn the how bright the illuminated side of objects should be 
     * @param shadowIntensityIn how dark the unilluminated side of objects should be
     */
    public Lighting(Vector3 lightDirectionIn, double lightIntensityIn, double shadowIntensityIn)
    {
        lightDirection = lightDirectionIn.getNormalized();
        lightIntensity = lightIntensityIn;
        shadowIntensity = shadowIntensityIn;
    }

    /**
     * goes through the specified meshes and updates all their lightings 
     * @param objects the objects to update
     */
    public void update(ArrayList<GameObject> objects)
    {
        for (int i = 0; i < objects.size(); i++)
        {
            if (objects.get(i).getMesh() != null)
                objects.get(i).getMesh().calculateLighting(this);
        }
    }
}

