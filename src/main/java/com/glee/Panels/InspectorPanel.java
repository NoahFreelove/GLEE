package com.glee.Panels;

import GLEngine.Core.Interfaces.EditorVisible;
import GLEngine.Core.Objects.Components.Component;
import GLEngine.Core.Objects.GameObject;
import GLEngine.Core.Objects.Transform;
import com.glee.ComponentField;
import com.glee.Editor;
import GLEngine.Core.Shaders.MeshRenderProperties;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.lang.reflect.Field;

public class InspectorPanel extends GridPane {
    private GameObject selectedObject;

    private TextField objectName;
    private TextField objectTag;

    private Accordion componentList;
    private Text emptyText;

    private Button deleteButton;

    private CopiedComponent copiedComponent;


    public InspectorPanel() {
        super();
        this.setStyle("-fx-background-color: #868686;");
        this.setPrefSize(420, 650);
        this.setLayoutX(220);
        this.setLayoutY(0);

        objectName = new TextField();
        objectTag = new TextField();
        deleteButton = new Button("Delete");
        objectName.setStyle("-fx-background-color: #545454; -fx-text-fill: #ffffff; -fx-font: 10 arial;");
        objectName.setMaxWidth(200);
        objectName.setMaxHeight(10);
        objectName.setTranslateX(10);
        objectName.setTranslateY(0);

        objectTag.setStyle("-fx-background-color: #545454; -fx-text-fill: #ffffff; -fx-font: 10 arial;");;
        objectTag.setMaxWidth(150);
        objectTag.setTranslateX(10);
        objectTag.setTranslateY(0);
        objectName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (selectedObject != null) {
                selectedObject.setName(newValue);
            }
        });
        objectTag.textProperty().addListener((observable, oldValue, newValue) -> {
            if (selectedObject != null) {
                selectedObject.setTag(newValue);
            }
        });

        this.add(objectName, 0, 0);
        this.add(objectTag, 0, 1);
        this.getChildren().add(deleteButton);
        objectName.setStyle("-fx-font-size: 20px;");
        objectTag.setStyle("-fx-font-size: 15px;");


        componentList = new Accordion();
        componentList.setPrefWidth(400);
        deleteButton.setVisible(false);

        this.add(componentList, 0, 2);
        emptyText = new Text("empty.");
        emptyText.setTranslateX(10);
        emptyText.setFill(Color.WHITE);
        emptyText.setStyle("-fx-font-size: 15px;");
    }

    public void setSelectedObject(int index){

        try {
            selectedObject = Editor.activeWorld.GameObjects().get(index);
            deleteButton.setVisible(true);
        }catch (Exception ignore)
        {
            selectedObject = null;
            deleteButton.setVisible(false);
            return;
        }
        objectName.setText(selectedObject.getIdentity().getName());
        objectTag.setText(selectedObject.getIdentity().getTag());
        componentList.getPanes().clear();

        for (Component c :
                selectedObject.getComponents()) {
            if(c == null)
                continue;
            Field[] allFields = c.getClass().getDeclaredFields();
            VBox box = new VBox();
            HBox operationsBox = new HBox();

            operationsBox.setStyle("-fx-background-color: #868686;");
            Button deleteButtonComp = new Button("Delete Comp");
            deleteButtonComp.setOnMouseClicked(mouseEvent -> deleteComponent(index, c));

            Button copyButtonComp = new Button("Copy Values");
            copyButtonComp.setOnMouseClicked(mouseEvent -> copyComponent(c));

            Button pasteComponentButton = new Button("Paste Values");
            pasteComponentButton.setOnMouseClicked(mouseEvent -> pasteComponent(index, c));

            operationsBox.getChildren().addAll(deleteButtonComp,new Text("       "),copyButtonComp, new Text("       "), pasteComponentButton);
            box.setStyle("-fx-padding: 0;");
            box.setFillWidth(true);
            box.setPrefWidth(400);

            for (Field f : allFields) {
                setObjectField(c, box, f);
            }

            if(c.getClass() != Transform.class && c.getClass() != MeshRenderProperties.class){
                Field[] componentFields = Component.class.getDeclaredFields();
                for (Field f : componentFields) {
                    setObjectField(c, box, f);
                }
            }

            box.getChildren().add(operationsBox);


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
        deleteButton.setTranslateY(10);
        deleteButton.setTranslateX(300);

        deleteButton.setOnMouseClicked(mouseEvent -> {
            if(selectedObject !=null){
                Editor.activeWorld.Remove(selectedObject);
                deleteButton.setVisible(false);
                Editor.refresh();
                setSelectedObject(0);
            }
        });

        if(componentList.getPanes().size() > 0){
            componentList.setExpandedPane(componentList.getPanes().get(0));
        }


    }

    private void deleteComponent(int index, Component c) {
        if(selectedObject !=null)
        {
            selectedObject.removeComponent(c);
            setSelectedObject(index);
        }
    }

    private void copyComponent(Component c) {
        if(selectedObject !=null)
        {
            copiedComponent = new CopiedComponent(c, c.getClass());
        }
    }

    private void pasteComponent(int index, Component c) {
        if(selectedObject !=null)
        {
            if(copiedComponent != null){
                if(c.getClass() == copiedComponent.classType){
                    int i = 0;
                    for (Field f : copiedComponent.classType.getDeclaredFields()) {
                        try {
                            f.setAccessible(true);
                            if(f.getName().equals(copiedComponent.classType.getDeclaredFields()[i].getName())){
                                Field privateField = copiedComponent.classType.getDeclaredFields()[i];
                                privateField.setAccessible(true);
                                //System.out.println("New value:" + f.get(c).toString());
                                f.set(c, privateField.get(copiedComponent.component));
                            }
                        } catch (Exception e) {
                            System.out.println("Error pasting component values: " + e.getMessage());
                        }
                        i++;
                    }
                }
            }
        }
        setSelectedObject(index);
    }

    private void setObjectField(Component c, VBox box, Field f) {
        try {
            int modifiers = f.getModifiers();
            f.setAccessible(true);

            if(f.isAnnotationPresent(EditorVisible.class)) {
                Object obj = f.get(c);
                if(obj == null)
                    return;
                ComponentField item = new ComponentField(f.getName(), obj.getClass().getSimpleName(), obj, modifiers, c);
                box.getChildren().add(item);
            }
        }
        catch (Exception e){
            System.out.println("Cannot load component field:" + e.getMessage());
        }
    }
}
