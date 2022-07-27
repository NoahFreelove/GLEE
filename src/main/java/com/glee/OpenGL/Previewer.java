package com.glee.OpenGL;

import GLEngine.Core.Input.KeyEvent;
import GLEngine.Core.Objects.Components.Rendering.Camera;
import GLEngine.Core.Objects.GameObject;
import GLEngine.Core.Window;
import GLEngine.Core.Worlds.World;
import GLEngine.Core.Worlds.WorldLoader;
import GLEngine.Core.Worlds.WorldManager;
import com.glee.Editor;
import com.glee.GLEngineConnection;
import org.joml.Vector3f;

import java.io.File;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F5;

public class Previewer {
    public static GameObject cameraObject;

    public static String worldPath = "";

    public static void main(String[] args){
        GLEngineConnection.initializeConnection();
        String worldPath = GLEngineConnection.readFile("to").replace("FP:","");
        System.out.println(worldPath);

        Window.CreateWindow(1280, 720, () -> {
            loadWorld(worldPath, true);
            Window.GetInstance().keyCallbacks.add(new KeyEvent() {
                @Override
                public void keyPressed(int key, int mods) {

                }

                @Override
                public void keyReleased(int key, int mods) {
                    if(key == GLFW_KEY_F5){
                        refreshWorld();
                    }
                }
            });
        });
    }

    public static void loadWorld(String worldName, boolean absolutePath) {
        worldPath = (absolutePath)? worldName : Editor.projectInfo.sourcePath + "/" + worldName;
        if(!new File(worldPath).exists())
            return;
        World w = WorldLoader.PreviewWorld(worldPath);
        WorldManager.AddWorldToBuild(w);
        WorldManager.SwitchWorld(w);
        cameraObject = new GameObject();
        Camera cam = new Camera();
        cameraObject.addComponent(cam);
        cameraObject.addComponent(new CameraController(cam));
        Window.GetInstance().setActiveCamera(cam);
        w.Add(cameraObject);
    }

    public static void refreshWorld(){
        worldPath = GLEngineConnection.readFile("to").replace("FP:","");
        if(!new File(worldPath).exists())
            return;
        WorldManager.getCurrentWorld().Remove(cameraObject);
        World w2 = WorldLoader.PreviewWorld(worldPath);
        WorldManager.AddWorldToBuild(w2);
        WorldManager.SwitchWorld(w2);
        WorldManager.getCurrentWorld().Add(cameraObject);
    }
}
