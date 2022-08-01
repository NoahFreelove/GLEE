package glee.Panels;

import glee.Editor;
import glee.GLEngineConnection;
import glee.WorldSaver;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Consumer;

import static glee.Editor.projectInfo;
import static glee.Main.openProjectDialog;

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
        newItem.setOnAction(actionEvent -> {
            TextInputDialog dialog = new TextInputDialog("untitled" + (int)Math.floor(Math.random()*100));
            dialog.setTitle("New World");
            dialog.setHeaderText("New World");
            dialog.setContentText("World Name:");

            dialog.showAndWait().ifPresent(s -> {
                String result = dialog.getEditor().getText();

                if (result != null) {
                    try {
                        StringBuilder sb = new StringBuilder();
                        sb.append(projectInfo.sourcePath.trim());
                        sb.append(result.trim());
                        sb.append(".txt");

                        FileWriter fw = new FileWriter(sb.toString());
                        fw.write("");
                        fw.close();
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
                Editor.refresh();
            });


        });

        MenuItem saveItem = new MenuItem("Save World");
        saveItem.setOnAction(event -> Editor.saveEditor());
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(event -> System.exit(0));

        MenuItem previewWorldWithoutSaving = new MenuItem("Preview World");
        previewWorldWithoutSaving.setOnAction(actionEvent -> preview());

        MenuItem refreshButton = new MenuItem("Refresh");
        refreshButton.setOnAction(actionEvent -> Editor.refresh());

        playMenu.getItems().add(previewWorldWithoutSaving);

        fileMenu.getItems().addAll(newItem, saveItem, exitItem);

        viewMenu.getItems().add(refreshButton);

        this.getMenus().addAll(fileMenu, editMenu, viewMenu, playMenu, helpMenu);
    }

    private void preview(){
        WorldSaver.saveWorld(System.getenv("APPDATA") + "/GLEngine/temp");
        GLEngineConnection.writeFile("FP:" + System.getenv("APPDATA") + "/GLEngine/temp" + "\nBIN:" + new File(projectInfo.binPath).getAbsolutePath() + "/", "to");

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
