package trrt.rendering3d;
import java.io.File;
import trrt.rendering3d.gameObject.*;
import trrt.rendering3d.graphics.*;
import trrt.rendering3d.primitives.*;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import trrt.testing.*;
public class Main 
{
    public static final File GAMEOBJECT_DIRECTORY = new File("trrt\\rendering3d\\res", "gameObjectFiles");

    private static RenderingPanel renderingPanel;
    private static JFrame mainFrame;

    private static int defaultWidth = 1600;
    private static int defaultHeight = 900;

    private static GameObject testGameObject;

    public static void main(String [] args)
    {   
        startGraphics();
    }

    public static void startGraphics()
    {
        testGameObject = new GameObject
        (
            "suzanne", 
            new Mesh(new File("suzanne.obj"), new Color(100, 100, 100), new Vector3(0, 0, 0), Quaternion.toQuaternion(0, 0, 0), 100, true), 
            Vector3.ZERO
        );

        mainFrame = new JFrame("Main");
        mainFrame.setSize(defaultWidth, defaultHeight);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
        mainFrame.addWindowListener(new WindowCloseListener());

        renderingPanel = new RenderingPanel(1600, 900, Color.BLACK);
        mainFrame.getContentPane().add(renderingPanel);
        renderingPanel.setVisible(true);

        Camera cam = new Camera(new Vector3(0, 0, 0), 3000, 100, 60);
        renderingPanel.setCamera(cam);
        renderingPanel.addGameObject(testGameObject);
        cam.setOrbitControls(renderingPanel, testGameObject, 10, 30);
        renderingPanel.setLighting(new Lighting(new Vector3(0, -1, 0), 70, 30));
        renderingPanel.setFog(1000, 3000, new Color(190, 210, 245));
        renderingPanel.start();
    }
}

class WindowCloseListener implements WindowListener
{
    @Override
    public void windowClosing(WindowEvent e) 
    {
        RenderingPanel.printPreformanceSummary();
    }
    
    @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
}

// package trrt.rendering3d;
// import java.io.File;
// import trrt.rendering3d.gameObject.*;
// import trrt.rendering3d.graphics.*;
// import trrt.rendering3d.primitives.*;
// import javax.swing.JFrame;
// import java.awt.Color;
// import java.awt.event.WindowListener;
// import java.awt.event.WindowEvent;
// public class Main 
// {
//     //public static final File RES = new File("trrt\\rendering3d\\res");
//     public static final File GAMEOBJECT_DIRECTORY = new File("trrt\\rendering3d\\res", "gameObjectFiles");

//     private static RenderingPanel renderingPanel;
//     private static JFrame mainFrame;

//     private static final int DEFAULT_WIDTH = 1600;
//     private static final int DEFAULT_HEIGHT = 900;

//     private static Camera cam;

//     private static GameObject testGameObject;

//     public static void main(String [] args)
//     {   
//         startGraphics();

//         if (args.length > 0)
//         {
//             double scale = 1.0;
//             if (args.length > 1)
//             {
//                 try
//                 {
//                     scale = Double.parseDouble(args[1]);
//                 } catch (NumberFormatException e)
//                 {
//                     System.out.println("use the second argument for the desired scale of your object!");
//                 }
//             }
//             testGameObject = new GameObject
//             (
//                 args[0].substring(0, args[0].length()-4), 
//                 new Mesh(new File(args[0]), Color.LIGHT_GRAY, Vector3.ZERO, Quaternion.IDENTITY, scale, true), 
//                 Vector3.ZERO
//             );
//         }
//         else
//         {
//             // testGameObject = new GameObject
//             // (
//             //     "testObj", 
//             //     new DemoCube(), 
//             //     Vector3.ZERO
//             // );

//             testGameObject = new GameObject
//             (
//                 "testObj", 
//                 new Mesh(new File("suzanne.obj"), Color.LIGHT_GRAY, Vector3.ZERO, Quaternion.IDENTITY, 1, true), 
//                 Vector3.ZERO
//             );
//         }

//         renderingPanel.addGameObject(testGameObject);
//         cam.setOrbitControls(renderingPanel, testGameObject, 10, 30);
//     }

//     public static void startGraphics()
//     {
//         mainFrame = new JFrame("Main");
//         mainFrame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
//         mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         mainFrame.setVisible(true);
//         mainFrame.addWindowListener(new WindowCloseListener());

//         renderingPanel = new RenderingPanel(1600, 900, Color.BLACK);
//         mainFrame.getContentPane().add(renderingPanel);
//         renderingPanel.setVisible(true);

//         cam = new Camera(new Vector3(0, 0, 0), 3000, 100, 60);
//         renderingPanel.setCamera(cam);
//         renderingPanel.setLighting(new Lighting(new Vector3(0.3, -1, 0.5), 70, 30));
//         renderingPanel.setFog(1000, 3000, new Color(190, 210, 245));
//         renderingPanel.start();
//     }
// }

// class WindowCloseListener implements WindowListener
// {
//     @Override
//     public void windowClosing(WindowEvent e) 
//     {
//         RenderingPanel.printPreformanceSummary();
//     }
    
//     @Override
//     public void windowOpened(WindowEvent e) {}
//     @Override
//     public void windowClosed(WindowEvent e) {}
//     @Override
//     public void windowIconified(WindowEvent e) {}
//     @Override
//     public void windowDeiconified(WindowEvent e) {}
//     @Override
//     public void windowActivated(WindowEvent e) {}
//     @Override
//     public void windowDeactivated(WindowEvent e) {}
// }

// class DemoCube extends Mesh
// {
//     public DemoCube()
//     {
//         super(true);

//         Vector3 v1 = new Vector3(-50, -51, -50);
//         Vector3 v2 = new Vector3(50, -50, -50);
//         Vector3 v3 = new Vector3(50, -50, 50);
//         Vector3 v4 = new Vector3(-50, -50, 50);

//         Vector3 v5 = new Vector3(-50, 50, -50);
//         Vector3 v6 = new Vector3(50, 50, -50);
//         Vector3 v7 = new Vector3(50, 50, 50);
//         Vector3 v8 = new Vector3(-50, 50, 50);

//         getTriangles().add(new Triangle(this, v3, v1, v2, Color.LIGHT_GRAY));
//         getTriangles().add(new Triangle(this, v4, v1, v3, Color.LIGHT_GRAY));

//         getTriangles().add(new Triangle(this, v5, v7, v6, Color.LIGHT_GRAY));
//         getTriangles().add(new Triangle(this, v5, v8, v7, Color.LIGHT_GRAY));

//         getTriangles().add(new Triangle(this, v1, v6, v2, Color.LIGHT_GRAY));
//         getTriangles().add(new Triangle(this, v1, v5, v6, Color.LIGHT_GRAY));

//         getTriangles().add(new Triangle(this, v2, v7, v3, Color.LIGHT_GRAY));
//         getTriangles().add(new Triangle(this, v2, v6, v7, Color.LIGHT_GRAY));

//         getTriangles().add(new Triangle(this, v3, v8, v4, Color.LIGHT_GRAY));
//         getTriangles().add(new Triangle(this, v3, v7, v8, Color.LIGHT_GRAY));

//         getTriangles().add(new Triangle(this, v4, v5, v1, Color.LIGHT_GRAY));
//         getTriangles().add(new Triangle(this, v4, v8, v5, Color.LIGHT_GRAY));
//     }
// }

// package trrt.rendering3d;
// import java.io.File;
// import trrt.rendering3d.gameObject.*;
// import trrt.rendering3d.graphics.*;
// import trrt.rendering3d.primitives.*;
// import javax.swing.JFrame;
// import java.awt.Color;
// import java.awt.event.WindowListener;
// import java.awt.event.WindowEvent;
// import trrt.testing.*;
// public class Main 
// {
//     public static final File GAMEOBJECT_DIRECTORY = new File("trrt\\rendering3d\\res", "gameObjectFiles");

//     private static RenderingPanel renderingPanel;
//     private static JFrame mainFrame;

//     private static int defaultWidth = 1600;
//     private static int defaultHeight = 900;

//     private static GameObject testGameObject;

//     public static void main(String [] args)
//     {   
//         startGraphics();
//     }

//     public static void startGraphics()
//     {
//         testGameObject = new GameObject
//         (
//             "suzanne", 
//             new Mesh(new File("suzanne.obj"), new Color(100, 100, 100), new Vector3(0, 0, 0), Quaternion.toQuaternion(0, 0, 0), 100, true), 
//             Vector3.ZERO
//         );

//         mainFrame = new JFrame("Main");
//         mainFrame.setSize(defaultWidth, defaultHeight);
//         mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         mainFrame.setVisible(true);
//         mainFrame.addWindowListener(new WindowCloseListener());

//         renderingPanel = new RenderingPanel(1600, 900, Color.BLACK);
//         mainFrame.getContentPane().add(renderingPanel);
//         renderingPanel.setVisible(true);

//         Camera cam = new Camera(new Vector3(0, 0, 0), 3000, 100, 60);
//         renderingPanel.setCamera(cam);
//         renderingPanel.addGameObject(testGameObject);
//         cam.setOrbitControls(renderingPanel, testGameObject, 10, 30);
//         renderingPanel.setLighting(new Lighting(new Vector3(0, -1, 0), 70, 30));
//         renderingPanel.setFog(1000, 3000, new Color(190, 210, 245));
//         renderingPanel.start();
//     }
// }

// class WindowCloseListener implements WindowListener
// {
//     @Override
//     public void windowClosing(WindowEvent e) 
//     {
//         RenderingPanel.printPreformanceSummary();
//     }
    
//     @Override
//     public void windowOpened(WindowEvent e) {}
//     @Override
//     public void windowClosed(WindowEvent e) {}
//     @Override
//     public void windowIconified(WindowEvent e) {}
//     @Override
//     public void windowDeiconified(WindowEvent e) {}
//     @Override
//     public void windowActivated(WindowEvent e) {}
//     @Override
//     public void windowDeactivated(WindowEvent e) {}
// }