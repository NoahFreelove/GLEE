package glee.Panels;

import glee.Editor;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.File;

public class WorldPanel extends GridPane {
    public WorldPanel() {
        super();
        this.setStyle("-fx-background-color: #3a3a3a;");
        this.setPrefSize(600, 150);
        this.setLayoutX(0);
        this.setLayoutY(650);

        Text titleText = new Text("Worlds");
        titleText.setFill(Color.WHITE);
        titleText.setTranslateX(10);
        titleText.setTranslateY(10);
        titleText.setStyle("-fx-font-size: 20px;");
        this.add(titleText, 0, 0);

        GridPane gridPane = new GridPane();
        ScrollPane sp = new ScrollPane(gridPane);
        sp.setPrefSize(380, 130);
        sp.setTranslateY(20);

        gridPane.setPrefWidth(380);
        gridPane.setPrefHeight(130);
        gridPane.setLayoutX(0);
        gridPane.setLayoutY(0);
        gridPane.setStyle("-fx-background-color: #545454;");

        this.add(sp, 0, 1);

        //foreach file in directory
        String dir = Editor.projectInfo.sourcePath;
        File[] files = new File(new File(dir).getParent()).listFiles();
        if(files == null)
            return;

        int i = 0;
        for (File file : files) {

            if (file.isFile()) {
                Button fileText = new Button(file.getName());
                fileText.setOnMouseClicked(mouseEvent -> Editor.loadWorld(file));
                fileText.setMaxWidth(100);
                fileText.setPrefWidth(100);
                fileText.setTranslateX(10 + i%3 * 15);
                fileText.setTranslateY(10 + i/3 * 10);
                gridPane.add(fileText, i%3, i/3+1);
                i++;
            }
        }
        gridPane.add(new Text(), 0, i/3+2);
    }
}
