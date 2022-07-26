package com.glee;

import GLEngine.Core.Interfaces.EditorVisible;
import GLEngine.Core.Objects.Components.Component;
import GLEngine.Core.Objects.GameObject;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.lang.reflect.Field;

public class InspectorPanel extends GridPane {
    private GameObject selectedObject;

    private Text objectName;
    private Text objectTag;

    private Accordion componentList;

    public InspectorPanel() {
        super();
        this.setStyle("-fx-background-color: #868686;");
        this.setPrefSize(250, 800);
        this.setLayoutX(350);
        this.setLayoutY(0);

        objectName = new Text();
        objectTag = new Text();
        objectName.setFill(Color.WHITE);
        objectTag.setFill(Color.WHITE);
        objectTag.setTranslateX(10);
        objectName.setTranslateX(10);

        this.add(objectName, 0, 0);
        this.add(objectTag, 0, 1);

        objectName.setStyle("-fx-font-size: 20px;");
        objectTag.setStyle("-fx-font-size: 15px;");


        componentList = new Accordion();

        this.add(componentList, 0, 2);
    }

    public void setSelectedObject(int index){
        selectedObject = Editor.activeWorld.GameObjects().get(index);
        objectName.setText(selectedObject.getIdentity().getName());
        objectTag.setText(selectedObject.getIdentity().getTag());
        componentList.getPanes().clear();

        for (Component c :
                selectedObject.getComponents()) {
            Field[] allFields = c.getClass().getDeclaredFields();
            VBox box = new VBox();
            box.setStyle("-fx-padding: 0;");
            box.setFillWidth(false);

            for (Field f : allFields) {
                try {
                    int modifiers = f.getModifiers();
                    f.setAccessible(true);

                    if(f.isAnnotationPresent(EditorVisible.class)) {
                        Object obj = f.get(c);
                        ComponentField item = new ComponentField(f.getName(), obj.getClass().getSimpleName(), obj, modifiers);
                        box.getChildren().add(item);
                    }
                }
                catch (Exception e){
                    System.out.println("Cannot load component field:" + e.getMessage());
                }
            }
            if(box.getChildren().size() == 0){
                box.getChildren().add(new Text("empty."));
            }
            componentList.getPanes().add(new TitledPane(c.getClass().getSimpleName(), box));

        }
    }
}
