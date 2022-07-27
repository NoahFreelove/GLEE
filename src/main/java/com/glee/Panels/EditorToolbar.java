package com.glee.Panels;

import com.glee.Editor;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import java.io.File;

import static com.glee.Main.openProjectDialog;

public class EditorToolbar extends MenuBar {
    public EditorToolbar() {
        this.setStyle("-fx-background-color: #868686;");
        // add elements
        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu viewMenu = new Menu("View");
        Menu play = new Menu("Play");
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
        saveItem.setOnAction(event -> {
            Editor.saveEditor();
        });
        MenuItem saveAsItem = new MenuItem("Save World As");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(event -> {
            System.exit(0);
        });
        fileMenu.getItems().addAll(newItem, openItem, saveItem, saveAsItem, exitItem);

        this.getMenus().addAll(fileMenu, editMenu, viewMenu, play, helpMenu);
    }
}
