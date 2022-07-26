package com.glee;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.File;

public class WorldPanel extends GridPane {
    public WorldPanel() {
        super();
        this.setStyle("-fx-background-color: #414141;");
        this.setPrefSize(350, 150);
        this.setLayoutX(0);
        this.setLayoutY(650);

        Text titleText = new Text("Worlds");
        titleText.setFill(Color.WHITE);
        titleText.setTranslateX(10);
        titleText.setTranslateY(10);
        titleText.setStyle("-fx-font-size: 20px;");
        this.add(titleText, 0, 0);

        //foreach file in directory
        String dir = Editor.projectInfo.sourcePath + "/";
        File folder = new File(dir);
        File[] files = folder.listFiles();
        if(files == null)
            return;

        int i = 0;
        for (File file : files) {
            if(i>8)
                break;

            if (file.isFile()) {
                Button fileText = new Button(file.getName());
                fileText.setOnMouseClicked(mouseEvent -> Editor.loadWorld(file));
                fileText.setMaxWidth(100);
                fileText.setPrefWidth(100);
                fileText.setTranslateX(10 + i/3 * 15);
                fileText.setTranslateY(10 + i%3 * 10);
                this.add(fileText, i/3, i%3+1);
                i++;
            }
        }
    }
}
