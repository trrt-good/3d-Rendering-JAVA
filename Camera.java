import javax.swing.*;
import java.awt.event.*;
import java.awt.MouseInfo;
public class Camera implements KeyListener
{
    public double h_fov = 60;
    public double v_fov = 33.75;
    public Vector3 position = new Vector3(0, 10, 0);
    public double h_orientation = 0;
    public double v_orientation = 0;

    @Override
    public void keyTyped(KeyEvent e) 
    {
        if (e.getKeyChar() == 'e')
            h_orientation += 0.01;    
        if (e.getKeyChar() == 'q')
            h_orientation -=0.01;

        if (e.getKeyChar() == 'r')
            v_orientation +=0.01;
        if (e.getKeyChar() == 'f')
            v_orientation -=0.01;

        if (e.getKeyChar() == 'w')
        {
            position.x += 1;
        }
        if (e.getKeyChar() == 'd')
        {
            position.z += 1;
        }
        if (e.getKeyChar() == 'a')
        {
            position.z -= 1;
        }
        if (e.getKeyChar() == 's')
        {
            position.x -= 1;
        }
    }
    @Override
    public void keyPressed(KeyEvent e) {

    }
    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }
}