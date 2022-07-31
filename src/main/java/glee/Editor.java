package glee;

import GLEngine.Core.Objects.Components.Physics.Rigidbody;
import GLEngine.Core.Objects.GameObject;
import GLEngine.Core.Worlds.World;
import GLEngine.Core.Worlds.WorldLoader;
import glee.Panels.EditorToolbar;
import glee.Panels.HierarchyPanel;
import glee.Panels.InspectorPanel;
import glee.Panels.WorldPanel;
import glee.TestComponents.RegisterTest;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.io.File;

import static glee.Main.mainStage;

public class Editor {
    public static File projectFile = new File("default.glengine");
    public static ProjectInfo projectInfo = new ProjectInfo(projectFile);

    public static InspectorPanel inspectorPanel;
    public static WorldPanel worldPanel;
    public static HierarchyPanel hierarchyPanel;
    public static EditorToolbar editorToolbar;
    public static String worldName = "";

    private static Group root = new Group();

    public static World activeWorld = new World();

    public static void openEditor(File openFile){
        projectFile = openFile;
        projectInfo = new ProjectInfo(openFile);
        GLEngineConnection.initializeConnection();
        GLEngineConnection.startReadingFile();

        root = new Group();
        activeWorld = new World();
        Scene scene = new Scene(root, 600, 800);
        scene.setFill(Color.BLACK);
        mainStage.setScene(scene);
        mainStage.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() == KeyCode.F5){
                refresh();
            }
            if(e.getCode() == KeyCode.F1){
                inspectorPanel.setSelectedObject(0);
            }
        });

        inspectorPanel = new InspectorPanel();
        worldPanel = new WorldPanel(true);
        hierarchyPanel = new HierarchyPanel();
        editorToolbar = new EditorToolbar();

        root.getChildren().addAll(inspectorPanel, worldPanel, hierarchyPanel, editorToolbar);

        refresh();
    }

    private static void addSampleGameObject(){
        GameObject go = new GameObject();
        go.getIdentity().setName("Sample Game Object");
        go.getIdentity().setTag("(Sample Tag)");

        go.addComponent(new RegisterTest());
        go.addComponent(new Rigidbody());
        activeWorld.Add(go);
        inspectorPanel.setSelectedObject(0);
    }

    public static void refresh(){
        root.getChildren().remove(worldPanel);
        worldPanel = new WorldPanel();
        root.getChildren().add(worldPanel);

        root.getChildren().remove(hierarchyPanel);
        hierarchyPanel = new HierarchyPanel();
        root.getChildren().add(hierarchyPanel);

        root.getChildren().remove(inspectorPanel);
        inspectorPanel = new InspectorPanel();
        root.getChildren().add(inspectorPanel);

        String titleText = "GLEE Editor";
        if(projectInfo.name.length() > 30){
            titleText = titleText + ": " + projectInfo.name.substring(0, 20) + "...";
        }
        else{
            titleText = titleText + ": " + projectInfo.name;
        }
        mainStage.setTitle(titleText);
    }

    public static void loadWorld(File worldFile, boolean refresh){
        World w = WorldLoader.DummyWorld(worldFile.getAbsolutePath());
        String[] fileParts = worldFile.getAbsolutePath().replace("\\", "/").split("/");
        worldName = fileParts[fileParts.length - 1].split("\\.")[0].replace("\n","");
        //System.out.println(worldName);
        activeWorld = w;
        if(refresh) refresh();
        inspectorPanel.setSelectedObject(0);
        GLEngineConnection.writeFile("FP:" + worldFile.getAbsolutePath()+"\nBIN:" + new File(projectInfo.binPath).getAbsolutePath(), "to");
    }

    public static void saveEditor() {
        //System.out.println("1: " + projectInfo.sourcePath);
        //System.out.println("2: " + worldName);
        StringBuilder sb = new StringBuilder();
        sb.append(projectInfo.sourcePath.trim());
        sb.append(worldName.trim());
        sb.append(".txt");
        //System.out.println("3: " + sb);
        // Save to both file and temp file in case the previewer is currently previewing the temp file
        WorldSaver.saveWorld(sb.toString());
        WorldSaver.saveWorld(System.getenv("APPDATA") + "/GLEngine/temp");
    }
}
