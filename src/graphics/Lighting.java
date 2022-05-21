package src.graphics;
import java.util.ArrayList;

import src.GameObject;
import src.primitives.Vector3;
public class Lighting
{
    //direction that the light faces. 
    public Vector3 lightDirection;

    //how bright should the illuminated side of meshes be?
    public double lightIntensity; 

    //how dark should the unilluminated side of meshes be? 
    public double shadowIntensity;

    public Lighting(Vector3 lightDirectionIn, double lightIntensityIn, double shadowIntensityIn)
    {
        lightDirection = lightDirectionIn;
        lightIntensity = lightIntensityIn;
        shadowIntensity = shadowIntensityIn;
    }

    //goes through the specified meshes and updates all their lighting's 
    public void update(ArrayList<GameObject> objects)
    {
        for (int i = 0; i < objects.size(); i++)
            objects.get(i).getMesh().calculateLighting(this);
    }
}

