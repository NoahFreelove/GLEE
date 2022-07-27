package com.glee.Panels;

import GLEngine.Core.Interfaces.EditorVisible;
import GLEngine.Core.Objects.Components.Component;
import GLEngine.Core.Objects.GameObject;
import com.glee.ComponentField;
import com.glee.Editor;
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
    private Text emptyText;
    public InspectorPanel() {
        super();
        this.setStyle("-fx-background-color: #868686;");
        this.setPrefSize(420, 650);
        this.setLayoutX(220);
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
        componentList.setPrefWidth(400);

        this.add(componentList, 0, 2);
        emptyText = new Text("empty.");
        emptyText.setTranslateX(10);
        emptyText.setFill(Color.WHITE);
        emptyText.setStyle("-fx-font-size: 15px;");
    }

    public void setSelectedObject(int index){

        try {
            selectedObject = Editor.activeWorld.GameObjects().get(index);
        }catch (Exception ignore)
        {
            selectedObject = null;
            return;
        }
        objectName.setText(selectedObject.getIdentity().getName());
        objectTag.setText(selectedObject.getIdentity().getTag());
        componentList.getPanes().clear();

        for (Component c :
                selectedObject.getComponents()) {
            Field[] allFields = c.getClass().getDeclaredFields();
            VBox box = new VBox();
            box.setStyle("-fx-padding: 0;");
            box.setFillWidth(true);
            box.setPrefWidth(400);

            for (Field f : allFields) {
                setObjectField(c, box, f);
            }

            Field[] componentFields = Component.class.getDeclaredFields();
            for (Field f : componentFields) {
                setObjectField(c, box, f);
            }

            if(box.getChildren().size() == 0){
                box.getChildren().add(new Text("empty."));
            }
            componentList.getPanes().add(new TitledPane(c.getClass().getSimpleName(), box));
        }

        if(selectedObject.getComponents().size() == 0){
            if(!this.getChildren().contains(emptyText)){
                this.add(emptyText,0,3);
            }
        }else{
            this.getChildren().remove(emptyText);
        }
    }

    private void setObjectField(Component c, VBox box, Field f) {
        try {
            int modifiers = f.getModifiers();
            f.setAccessible(true);

            if(f.isAnnotationPresent(EditorVisible.class)) {
                Object obj = f.get(c);
                ComponentField item = new ComponentField(f.getName(), obj.getClass().getSimpleName(), obj, modifiers, c);
                box.getChildren().add(item);
            }
        }
        catch (Exception e){
            System.out.println("Cannot load component field:" + e.getMessage());
        }
    }
}
