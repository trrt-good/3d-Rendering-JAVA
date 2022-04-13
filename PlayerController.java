import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PlayerController implements KeyListener
{
    private GameObject parentGameObject;
    private boolean enabled;

    public PlayerController(GameObject gameObject)
    {
        enabled = true;
        parentGameObject = gameObject;
    }

    public void enable()
    {
        enabled = true;
    }

    public void dissable()
    {
        enabled = false;
    }

    @Override
    public void keyTyped(KeyEvent e) 
    {   
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        switch (e.getKeyChar()) 
        {
            case 'w':
                parentGameObject.forwardControl();
                break;
            case 's':
                parentGameObject.backwardControl();
                break;
            case 'a':
                parentGameObject.leftControl();
                break;
            case 'd':
                parentGameObject.rightControl();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) 
    {
        
    }
}
