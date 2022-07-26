package com.glee;

import GLEngine.Core.Objects.GameObject;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class HierarchyPanel extends GridPane {
    public HierarchyPanel() {
        this.setStyle("-fx-background-color: #000000;");
        this.setPrefSize(350, 650);
        this.setLayoutX(0);
        this.setLayoutY(0);

        Text titleText = new Text("Hierarchy");
        titleText.setFill(Color.WHITE);
        titleText.setTranslateX(10);
        titleText.setTranslateY(10);
        titleText.setStyle("-fx-font-size: 20px;");
        this.add(titleText, 0, 0);

        int i = 0;
        for (GameObject o :
                Editor.activeWorld.GameObjects()) {
            Button hierarchyButton = new Button(o.getIdentity().getName());
            hierarchyButton.setTranslateY(30 + i * 10);
            int finalI = i;
            hierarchyButton.setOnMouseClicked(mouseEvent -> {
                Editor.inspectorPanel.setSelectedObject(finalI);
            });
            this.add(hierarchyButton, 0, i+1);
            i++;
        }
    }
}
