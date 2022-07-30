package glee.OpenGL;

import GLEngine.Core.Input.KeyEvent;
import GLEngine.Core.Objects.Components.Rendering.Camera;
import GLEngine.Core.Objects.GameObject;
import GLEngine.Core.Window;
import GLEngine.Core.Worlds.World;
import GLEngine.Core.Worlds.WorldLoader;
import GLEngine.Core.Worlds.WorldManager;
import glee.GLEngineConnection;

import java.io.File;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F5;

public class Previewer {
    public static GameObject cameraObject;

    public static String worldPath = "";
    public static String binPath = "";

    public static void main(String[] args){
        GLEngineConnection.initializeConnection();
        String[] fpAndBin = GLEngineConnection.readFile("to").replace("FP:","").split("BIN:");
        worldPath = fpAndBin[0];
        binPath = fpAndBin[1];

        /*System.out.println(worldPath);
        System.out.println(binPath);*/

        Window.CreateWindow(1920, 1080, () -> {
            loadWorld(worldPath, binPath);
            Window.GetInstance().keyCallbacks.add(new KeyEvent() {
                @Override
                public void keyPressed(int key, int mods) {}

                @Override
                public void keyReleased(int key, int mods) {
                    if(key == GLFW_KEY_F5){
                        refreshWorld();
                    }
                }
            });
        });
    }

    public static void loadWorld(String worldName, String binPath) {
        worldName = worldName.replace("\n","");
        binPath = binPath.replace("\n","");
        World w = WorldLoader.PreviewWorld(worldName, binPath);
        WorldManager.AddWorldToBuild(w);
        WorldManager.SwitchWorld(w);
        cameraObject = new GameObject();
        Camera cam = new Camera();
        cameraObject.addComponent(cam);
        cameraObject.addComponent(new CameraController(cam));
        Window.GetInstance().setActiveCamera(cam);
        w.Add(cameraObject);
        for (GameObject go :
                WorldManager.getCurrentWorld().GameObjects()) {
            go.OnCreated();
        }
    }

    public static void refreshWorld(){
        String[] fpAndBin = GLEngineConnection.readFile("to").replace("FP:","").split("BIN:");
        worldPath = fpAndBin[0];
        binPath = fpAndBin[1];
        worldPath = worldPath.replace("\n","");
        binPath = binPath.replace("\n","");
        if(!new File(worldPath).exists())
            return;
        WorldManager.getCurrentWorld().Remove(cameraObject);
        World w2 = WorldLoader.PreviewWorld(worldPath, binPath);
        WorldManager.AddWorldToBuild(w2);
        WorldManager.SwitchWorld(w2);
        WorldManager.getCurrentWorld().Add(cameraObject);

        for (GameObject go :
                WorldManager.getCurrentWorld().GameObjects()) {
            go.OnCreated();
        }
    }
}
