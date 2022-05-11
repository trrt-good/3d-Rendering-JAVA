public class Vertex 
{
    private Vector3 worldCoordinate;
    private Vector2 screenCoordinate;
    private double distanceToCamera;
    public boolean inView;

    private Vector2 textureCoordinate;
    private Vector3 normal;

    public Vertex(Vector3 coordinate3d, Vector3 normal, Vector2 textureCoord)
    {
        worldCoordinate = coordinate3d;
    }

    public void setScreenCoordinate(Vector2 screenCoord)
    {
        screenCoordinate = screenCoord;
    }

    public void setCamDistance(double distance)
    {
        distanceToCamera = distance;
    }

    public void setWorldCoordinate(Vector3 worldCoord)
    {
        worldCoordinate = worldCoord;
    }

    public Vector3 getWordCoords()
    {
        return worldCoordinate;
    }

    public Vector2 getScreenCoords()
    {
        return screenCoordinate;
    }

    public double getCamDistance()
    {
        return distanceToCamera;
    }
}
