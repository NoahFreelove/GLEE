package com.glee.Panels;

import GLEngine.Core.Objects.GameObject;
import com.glee.Editor;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class HierarchyPanel extends GridPane {

    ArrayList<Node> children = new ArrayList<>();
    ScrollPane sp = new ScrollPane();
    GridPane gp = new GridPane();
    Button searchButton;
    Button clearButton;
    public HierarchyPanel() {
        this.setStyle("-fx-background-color: #000000;");
        this.setPrefSize(350, 625);
        this.setLayoutX(0);
        this.setLayoutY(25);

        Text titleText = new Text("Hierarchy");
        titleText.setFill(Color.WHITE);
        titleText.setTranslateX(10);
        titleText.setTranslateY(10);
        titleText.setStyle("-fx-font-size: 20px;");
        this.add(titleText, 0, 0);


        TextField searchBox = new TextField();
        this.add(searchBox, 0, 1);
        searchBox.setTranslateX(10);
        searchBox.setTranslateY(10);
        searchBox.setPrefWidth(200);
        searchBox.setStyle("-fx-background-color: #545454; -fx-text-fill: #ffffff;");

        searchButton = new Button("Search");
        this.add(searchButton, 0, 2);
        searchButton.setTranslateX(10);
        searchButton.setTranslateY(10);
        searchButton.setPrefWidth(200);
        searchButton.setStyle("-fx-background-color: #313131; -fx-text-fill: #ffffff;");

        searchButton.setOnMouseClicked(event -> {
            filter(searchBox.getText());
        });

        //clear button
        clearButton = new Button("Clear");

        this.add(clearButton, 0, 3);
        clearButton.setTranslateX(10);
        clearButton.setTranslateY(10);
        clearButton.setPrefWidth(200);
        clearButton.setStyle("-fx-background-color: #313131; -fx-text-fill: #ffffff;");
        clearButton.setOnMouseClicked(event -> {
            searchBox.setText("");
            filter("");
        });
        sp.setTranslateY(25);
        sp.setPrefWidth(200);
        sp.setPrefHeight(450);
        sp.setTranslateX(10);
        sp.setContent(gp);
        // remove horizontal scrollbar
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);


        gp.setStyle("-fx-background-color: #313131;");
        gp.setPrefWidth(200);
        gp.setPrefHeight(450);
        this.add(sp, 0, 4);
        int i = 0;
        for (GameObject o :
                Editor.activeWorld.GameObjects()) {
            Button hierarchyButton = new Button(o.getIdentity().getName());
            int finalI = i;
            hierarchyButton.setOnMouseClicked(mouseEvent -> {
                Editor.inspectorPanel.setSelectedObject(finalI);
            });
            gp.add(hierarchyButton, 0, i);
            i++;
        }

        children.addAll(gp.getChildren());
    }

    private void filter(String text){
        for (Node node :
                children) {
            if (node instanceof Button) {
                if(!gp.getChildren().contains(node)){
                    gp.getChildren().add(node);
                }
            }

        }
        for(int i = 0; i < gp.getChildren().size(); i++){
            if(gp.getChildren().get(i) instanceof Button){
                Button button = (Button) gp.getChildren().get(i);
                if(button == searchButton || button == clearButton)
                    continue;
                if(!button.getText().toLowerCase().contains(text.toLowerCase())){
                    gp.getChildren().remove(i);
                    i--;
                }
            }
        }
    }
}