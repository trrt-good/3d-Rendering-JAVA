import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PlayerController implements KeyListener
{
    private GameObject parentGameObject;

    public PlayerController(GameObject gameObject)
    {
        parentGameObject = gameObject;
    }

    @Override
    public void keyTyped(KeyEvent e) 
    {
        
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        
    }

    @Override
    public void keyReleased(KeyEvent e) 
    {
        
    }
}
