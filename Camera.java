import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JPanel;

public class Camera
{
    //field of view of the camera.
    private double fov; //strictly reffers to the horizontal fov as vertical fov is based off screen height 

    private Vector3 position; //position of camera in world-space
    private double hAngle; //horizontal angle of camera
    private double vAngle; //vertical angle of camera
    private double renderPlaneDistance; //distance from the camera that the rendering plane is
    private double farClipDistance; //how far away should triangles stop being rendered?
    private double nearClipDistance; //how close should triangles stop being rendered?

    //movement controllers. 
    private OrbitCamController orbitController = null;
    private FreeCamController freeCamController = null;

    //width of the render plane based off fov. 
    private double renderPlaneWidth;

    public Camera(Vector3 positionIn, double farClipDistanceIn, double nearClipDistanceIn, double fovIn)
    {
        renderPlaneDistance = 10;
        hAngle = 0;
        vAngle = 0;
        position = positionIn;
        farClipDistance = farClipDistanceIn;
        nearClipDistance = nearClipDistanceIn;
        setFov(fovIn);
    }

    //sets the v and h angles to look at the specified position.
    public void lookAt(Vector3 pos)
    {
        hAngle = (pos.x-position.x < 0)? -Math.toDegrees(Math.atan((pos.z-position.z)/(pos.x-position.x)))-90 : 90-Math.toDegrees(Math.atan((pos.z-position.z)/(pos.x-position.x)));

        vAngle = Math.toDegrees(Math.atan((pos.y-position.y)/(Math.sqrt((pos.x-position.x)*(pos.x-position.x) + (pos.z-position.z)*(pos.z-position.z)))));
        
        hAngle%=360;
        vAngle%=360;
    }

    //camera controller which orbits a specified GameObject. panning the camera will cause it to 
    //circle around that object. The user can also use the scroll wheel to zoom in and out from 
    //the game object. 
    class OrbitCamController implements MouseMotionListener, MouseWheelListener, MouseListener
    {
        private int maxDistance = 3000; //maximum distance the camera can be from the object
        private int minDistance = 300; //minimum distance

        private double distance; //distacne from the object

        private int maxAngle = 80; //the maximum angle the camera can go up to
        private int minAngle = -80; //the minimum angle the camera can go down to.

        private GameObject focusObj; //the game object that the camera is focused on. 
        private double sensitivity; //how fast should the camera pan?

        private int prevX = 0;
        private int prevY = 0;

        private Vector3 difference;
        private Vector3 directionUnit = new Vector3();
        
        public OrbitCamController(GameObject focusObjectIn, double startDistanceIn, double sensitivityIn)
        {
            focusObj = focusObjectIn;
            distance = startDistanceIn;
            sensitivity = sensitivityIn;

            //sets up the position of the camera.
            position = Vector3.add(focusObj.getTransform().getPosition(), new Vector3(0, 0, -distance));
            directionUnit = Vector3.subtract(position, focusObj.getTransform().getPosition()).getNormalized();
            difference = Vector3.multiply(directionUnit, startDistanceIn);
        }

        //changes the distance based on the mouse movement. 
        public void mouseWheelMoved(MouseWheelEvent e) 
        {
            distance = Math.max(minDistance, Math.min(distance + e.getWheelRotation()*30, maxDistance));
            difference = Vector3.multiply(directionUnit, distance);
            updatePosition();
        }

        //pans the camera 
        public void mouseDragged(MouseEvent e) 
        {
            directionUnit = Vector3.rotateAroundYaxis(directionUnit, (e.getX()-prevX)/(2000/sensitivity));
            if (vAngle > -maxAngle && (e.getY()-prevY)/(200/sensitivity) > 0)
                directionUnit = Vector3.rotateAroundYaxis(Vector3.rotateAroundXaxis(Vector3.rotateAroundYaxis(directionUnit, -hAngle*0.017453292519943295), (e.getY()-prevY)/(2000/sensitivity)), hAngle*0.017453292519943295);
            else if (vAngle < -minAngle && (e.getY()-prevY)/(200/sensitivity) < 0)
                directionUnit = Vector3.rotateAroundYaxis(Vector3.rotateAroundXaxis(Vector3.rotateAroundYaxis(directionUnit, -hAngle*0.017453292519943295), (e.getY()-prevY)/(2000/sensitivity)), hAngle*0.017453292519943295);
            difference = Vector3.multiply(directionUnit, distance);
            updatePosition();
            lookAt(focusObj.getTransform().getPosition());
            vAngle = Math.max(-89, Math.min(89, vAngle));
            prevX = e.getX();
            prevY = e.getY();
        }

        public void mousePressed(MouseEvent e) 
        {
            prevX = e.getX();
            prevY = e.getY();
        }

        public void setSensitivity(double sens)
        {
            sensitivity = sens;
        }

        //updates the position of the camera to be around the focusObject.
        public void updatePosition()
        {
            position = Vector3.add(focusObj.getTransform().getPosition(), difference);
        }

        public void mouseMoved(MouseEvent e) {}
        public void mouseClicked(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
    }

    //used mainly for debug, but a simple controller which lets the user fly up down left right forward backward.
    class FreeCamController implements KeyListener, MouseMotionListener, MouseListener
    {
        private int prevX = 0;
        private int prevY = 0;
        private double sensitivity;
        private double movementSpeed;

        public FreeCamController(double sensitivityIn, double movementSpeedIn)
        {
            sensitivity = sensitivityIn;
            movementSpeed = movementSpeedIn;
        }

        //for camera panning. 
        public void mouseDragged(MouseEvent e) 
        {
            hAngle = hAngle + (e.getX()-prevX)/(100/sensitivity);
            vAngle = vAngle - (e.getY()-prevY)/(100/sensitivity);
            if (hAngle < 0)
                hAngle += 360;
            if (vAngle < 0)
                hAngle += 360;
            hAngle%=360;
            vAngle%=360;

            prevX = e.getX();
            prevY = e.getY();
        }

        //checks for keys
        public void keyPressed(KeyEvent e) 
        {
            switch (e.getKeyChar()) 
            {
                case 'w':
                    moveForward(movementSpeed);
                    break;
                case 's':
                    moveForward(-movementSpeed);
                    break;
                case 'a':
                    moveLeft(movementSpeed);
                    break;
                case 'd':
                    moveLeft(-movementSpeed);
                    break;
                case 'e':
                    moveUp(movementSpeed);
                    break;
                case 'q':
                    moveUp(-movementSpeed);
                    break;
            }
        }

        private void moveForward(double distanceIn)
        {
            position.add(Vector3.multiply(Vector3.angleToVector(hAngle*0.017453292519943295, vAngle*0.017453292519943295), distanceIn));
        }

        private void moveLeft(double distanceIn)
        {
            position.add(Vector3.multiply(Vector3.angleToVector(hAngle*0.017453292519943295-Math.PI/2, 0), distanceIn));
        }

        private void moveUp(double distanceIn)
        {
            position.add(Vector3.multiply(Vector3.angleToVector(hAngle*0.017453292519943295, vAngle*0.017453292519943295+Math.PI/2), distanceIn));
        }

        public void mousePressed(MouseEvent e) 
        {
            prevX = e.getX();
            prevY = e.getY();
        }

        public void keyReleased(KeyEvent e) {}
        public void mouseClicked(MouseEvent e) {}

        public void mouseMoved(MouseEvent e) {}
        public void keyTyped(KeyEvent e) {}

        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
    }

    //sets the controls of the camera to orbit mode, with the needed values for the constructor.
    public void setOrbitControls(JPanel panel, GameObject focusObject, double startDistance, double sensitivity)
    {
        orbitController = new OrbitCamController(focusObject, startDistance, sensitivity);
        panel.addMouseListener(orbitController);
        panel.addMouseMotionListener(orbitController);
        panel.addMouseWheelListener(orbitController);
    }

    //sets the controls to free cam mode. 
    public void setFreeControls(JPanel panel, double movementSpeed, double sensitivity)
    {
        freeCamController = new FreeCamController(sensitivity, movementSpeed);
        panel.addKeyListener(freeCamController);
        panel.addMouseListener(freeCamController);
        panel.addMouseMotionListener(freeCamController);
    }

    //calculates the render plane width, which is a slightly expensive method, so it is only called once. 
    private double calculateRenderPlaneWidth()
    {
        return Math.tan(fov*0.017453292519943295/2)*renderPlaneDistance*2;
    }

    //#region getter/setter methods
    public double getFarClipDistancee()
    {
        return farClipDistance;
    }

    public double getNearClipDistance()
    {
        return nearClipDistance;
    }

    public OrbitCamController getOrbitCamController()
    {
        return orbitController;
    }

    public FreeCamController getFreeCamController()
    {
        return freeCamController;
    }

    public void setFov(double fovIn)
    {
        fov = fovIn;
        renderPlaneWidth = calculateRenderPlaneWidth();
    }

    public void setSensitivity(double sense)
    {
        if (orbitController != null)
            orbitController.setSensitivity(sense);
    }

    public double getRenderPlaneDistance()
    {
        return renderPlaneDistance;
    }

    public Vector3 getDirectionVector()
    {
        return Vector3.angleToVector(hAngle*0.017453292519943295, vAngle*0.017453292519943295);
    }

    public double getHorientation()
    {
        return hAngle;
    }

    public double getVorientation()
    {
        return vAngle;
    }

    public Vector3 getPosition()
    {
        return position;
    }

    public double getRenderPlaneWidth()
    {
        return renderPlaneWidth;
    }

    //#endregion
}

