package com.glee;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.File;

import static com.glee.Main.mainStage;

public class Editor {
    public static File currentFile = new File("default.glengine");
    public static ProjectInfo projectInfo = new ProjectInfo(currentFile);
    public static void openEditor(File openFile){
        currentFile = openFile;
        GLEngineConnection.initializeConnection();
        Group root = new Group();
        Scene scene = new Scene(root, 600, 800);
        scene.setFill(Color.WHITE);
        mainStage.setScene(scene);
        mainStage.setTitle("GLEE Editor");

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

        root.getChildren().add(title);
    }
}
