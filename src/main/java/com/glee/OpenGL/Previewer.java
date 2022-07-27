package com.glee.OpenGL;

import GLEngine.Core.Objects.Components.Rendering.Camera;
import GLEngine.Core.Objects.GameObject;
import GLEngine.Core.Window;
import GLEngine.Core.Worlds.World;
import GLEngine.Core.Worlds.WorldLoader;
import GLEngine.Core.Worlds.WorldManager;
import com.glee.Editor;
import com.glee.GLEngineConnection;

public class Previewer {

    public static void main(String[] args){
        GLEngineConnection.initializeConnection();
        String worldPath = GLEngineConnection.readFile("to").replace("FP:","");
        System.out.println(worldPath);

        Window.CreateWindow(1280, 720, () -> {
            World world = WorldLoader.PreviewWorld(worldPath);
            WorldManager.AddWorldToBuild(world);
            WorldManager.SwitchWorld(world);

            GameObject camera = new GameObject();
            Camera cam = new Camera();
            camera.addComponent(cam);
            camera.addComponent(new CameraController(cam));
            Window.GetInstance().setActiveCamera(cam);
            world.Add(camera);
        });
    }

    public void loadWorld(String worldName) {

        World w = WorldLoader.PreviewWorld(Editor.projectInfo.sourcePath + "/" + worldName);

        WorldManager.AddWorldToBuild(w);
        WorldManager.SwitchWorld(w);

        GameObject camera = new GameObject();
        Camera c = new Camera();
        camera.addComponent(c);
        w.Add(camera);
        Window.GetInstance().setActiveCamera(c);
    }
}
