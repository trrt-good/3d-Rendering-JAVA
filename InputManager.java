import java.awt.event.*;
public class InputManager implements KeyListener, MouseMotionListener, MouseListener
{
    public boolean L_Down;
    public boolean R_Down;

    public int mouseX;
    public int mouseY;

    public int R_mouseClickedX;
    public int L_mouseClickedY;

    public boolean forward;
    public boolean backward;
    public boolean left;
    public boolean right;
    public boolean upward;
    public boolean downward;

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) 
    {
        switch (e.getKeyChar()) {
            case KEYBINDS.FORWARD:
                forward = true;
                break;
            case KEYBINDS.BACKWARD:
                backward = true;
                break;
            case KEYBINDS.LEFTWARD:
                left = true;
                break;
            case KEYBINDS.RIGHTWARD:
                right = true;
                break;
            case KEYBINDS.UPWARD:
                upward = true;
                break;
            case KEYBINDS.DOWNWARD:
                downward = true;
                break;
        }   
    }

    @Override
    public void keyReleased(KeyEvent e) 
    {
        switch (e.getKeyChar()) {
            case KEYBINDS.FORWARD:
                forward = false;
                break;
            case KEYBINDS.BACKWARD:
                backward = false;
                break;
            case KEYBINDS.LEFTWARD:
                left = false;
                break;
            case KEYBINDS.RIGHTWARD:
                right = false;
                break;
            case KEYBINDS.UPWARD:
                upward = false;
                break;
            case KEYBINDS.DOWNWARD:
                downward = false;
                break;
        }   
    }

    public static final class KEYBINDS
    {
        public static final char FORWARD = 'w'; 
        public static final char BACKWARD = 's';
        public static final char LEFTWARD = 'a';  
        public static final char RIGHTWARD = 'd'; 
        public static final char UPWARD = 'e'; 
        public static final char DOWNWARD = 'q'; 
    }

    @Override
    public void mouseDragged(MouseEvent e) 
    {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) 
    {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mousePressed(MouseEvent e) 
    {
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                L_Down = true;
                break;
            case MouseEvent.BUTTON3:
                R_mouseClickedX = e.getX();
                L_mouseClickedY = e.getY();
                R_Down = true;
                break;
        }

        
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                L_Down = false;
                break;
            case MouseEvent.BUTTON3:
                R_Down = false;
                break;
        }
    }


    @Override
    public void mouseClicked(MouseEvent e){}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}