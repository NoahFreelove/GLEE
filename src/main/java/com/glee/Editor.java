package com.glee;

import GLEngine.Core.Objects.Components.Physics.Rigidbody;
import GLEngine.Core.Objects.GameObject;
import GLEngine.Core.Worlds.World;
import GLEngine.Core.Worlds.WorldLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.File;

import static com.glee.Main.mainStage;

public class Editor {
    public static File currentFile = new File("default.glengine");
    public static ProjectInfo projectInfo = new ProjectInfo(currentFile);

    public static InspectorPanel inspectorPanel;
    public static WorldPanel worldPanel;
    public static HierarchyPanel hierarchyPanel;

    private static Group root = new Group();

    public static World activeWorld = new World();

    public static void openEditor(File openFile){
        currentFile = openFile;
        GLEngineConnection.initializeConnection();
        root = new Group();
        Scene scene = new Scene(root, 600, 800);
        scene.setFill(Color.WHITE);
        mainStage.setScene(scene);
        mainStage.setTitle("GLEE Editor");
        mainStage.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() == KeyCode.F5){
                refresh();
            }
            if(e.getCode() == KeyCode.F1){
                inspectorPanel.setSelectedObject(0);
            }
        });

        System.out.println(projectInfo.name);
        System.out.println(projectInfo.sourcePath);

        Text title = new Text();
        if(projectInfo.name.length() > 30){
            title.setText(projectInfo.name.substring(0, 20) + "...");
        }
        else{
            title.setText(projectInfo.name);
        }
        title.setLayoutX(10);
        title.setLayoutY(40);
        title.setFill(Color.BLACK);
        title.setStyle("-fx-font-size: 30px;");

        inspectorPanel = new InspectorPanel();
        worldPanel = new WorldPanel();
        hierarchyPanel = new HierarchyPanel();

        root.getChildren().addAll(title, inspectorPanel, worldPanel, hierarchyPanel);
        addSampleGameObject();
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

    private static void refresh(){
        root.getChildren().remove(worldPanel);
        worldPanel = new WorldPanel();
        root.getChildren().add(worldPanel);

        root.getChildren().remove(hierarchyPanel);
        hierarchyPanel = new HierarchyPanel();
        root.getChildren().add(hierarchyPanel);
    }

    public static void loadWorld(File worldFile){
        System.out.println(worldFile.getAbsolutePath());
        World w = WorldLoader.DummyWorld(worldFile.getAbsolutePath());
        activeWorld = w;
        refresh();
        inspectorPanel.setSelectedObject(0);
    }
}
