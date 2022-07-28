package com.glee.Panels;

import com.glee.Editor;
import com.glee.GLEngineConnection;
import com.glee.WorldSaver;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import java.io.File;

import static com.glee.Editor.projectInfo;
import static com.glee.Main.openProjectDialog;

public class EditorToolbar extends MenuBar {
    public EditorToolbar() {
        this.setStyle("-fx-background-color: #868686;");
        // add elements
        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu viewMenu = new Menu("View");
        Menu playMenu = new Menu("Play");
        Menu helpMenu = new Menu("Help");

        MenuItem newItem = new MenuItem("New World");
        MenuItem openItem = new MenuItem("Open World");
        openItem.setOnAction(event -> {
            File file = openProjectDialog();
            if(file != null){
                Editor.openEditor(file);
            }
        });
        MenuItem saveItem = new MenuItem("Save World");
        saveItem.setOnAction(event -> Editor.saveEditor());
        MenuItem saveAsItem = new MenuItem("Save World As");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(event -> System.exit(0));

        MenuItem previewWorldWithoutSaving = new MenuItem("Preview World");
        previewWorldWithoutSaving.setOnAction(actionEvent -> preview());

        MenuItem refreshButton = new MenuItem("Refresh");
        refreshButton.setOnAction(actionEvent -> Editor.refresh());

        playMenu.getItems().add(previewWorldWithoutSaving);

        fileMenu.getItems().addAll(newItem, openItem, saveItem, saveAsItem, exitItem);

        viewMenu.getItems().add(refreshButton);

        this.getMenus().addAll(fileMenu, editMenu, viewMenu, playMenu, helpMenu);
    }

    private void preview(){
        WorldSaver.saveWorld(System.getenv("APPDATA") + "/GLEngine/temp");
        GLEngineConnection.writeFile("FP:" + System.getenv("APPDATA") + "/GLEngine/temp" + "\nBIN:" + new File(projectInfo.binPath).getAbsolutePath(), "to");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("GLEE");
        alert.setHeaderText("World saved to temporary file");
        alert.setContentText("Press F5 in the previewer to refresh and show your changes.");
        alert.showAndWait().ifPresent(rs -> {
            /*if (rs == ButtonType.OK) {
                System.out.println("Pressed OK.");
            }*/
        });
    }
}
