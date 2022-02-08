public class Point3
{
    public Vector3 position = new Vector3(0,0,0);
    
    public Point3(Vector3 positionIn)
    {
        Main.ObjectManager.renderPoint3s.add(this);
        position = new Vector3(positionIn);
    }
}