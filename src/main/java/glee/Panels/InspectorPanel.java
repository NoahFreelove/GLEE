package glee.Panels;

import GLEngine.Core.Interfaces.EditorVisible;
import GLEngine.Core.Objects.Components.Component;
import GLEngine.Core.Objects.GameObject;
import GLEngine.Core.Objects.Transform;
import glee.ComponentField;
import glee.Editor;
import GLEngine.Core.Shaders.MeshRenderProperties;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.joml.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class InspectorPanel extends GridPane {
    private GameObject selectedObject;

    private TextField objectName;
    private TextField objectTag;

    private Accordion componentList;
    private Text emptyText;

    private Button deleteButton;

    private CopiedComponent copiedComponent;

    private ScrollPane sp = new ScrollPane();
    private VBox spHolder = new VBox();

    private TextField componentName;
    private int currentSelection;


    public InspectorPanel() {
        super();
        this.setStyle("-fx-background-color: #545454;");
        this.setPrefSize(420, 650);
        this.setLayoutX(220);
        this.setLayoutY(0);
        sp.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        sp.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);

        sp.setPrefWidth(400);
        sp.setPrefHeight(600);
        // set background color to transparent
        sp.setStyle("-fx-background-color: #545454;");

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
        sp.setContent(componentList);
        spHolder.getChildren().add(sp);
        sp.setStyle("-fx-background: transparent; -fx-background-color: #545454;");
        this.add(spHolder, 0, 2);

        Button addComponent = new Button("Add Component");
        addComponent.setOnMouseClicked(event -> {
            if (selectedObject != null) {
                try {
                    Class clazz = Class.forName(componentName.getText());
                    Constructor<?> constructor = clazz.getConstructor();
                    // Create instance of class without casting
                    if(constructor.newInstance() instanceof Component c){
                        selectedObject.addComponent(c);
                        setSelectedObject(currentSelection);

                        // set the last pane to expanded
                        componentList.setExpandedPane(componentList.getPanes().get(componentList.getPanes().size() - 1));
                    }
                }
                catch (Exception e){
                    System.out.println("Could not add component: " + e.getMessage());
                }
            }
        });
        addComponent.setTranslateX(135);
        componentName = new TextField();
        componentName.setPromptText("Component Package");
        this.add(addComponent,0,4);
        this.add(componentName,0,3);
        emptyText = new Text("empty.");
        emptyText.setTranslateX(10);
        emptyText.setFill(Color.WHITE);
        emptyText.setStyle("-fx-font-size: 15px;");
    }

    public void setSelectedObject(int index){
        currentSelection = index;
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
                operationsBox.getChildren().addAll(deleteButtonComp,new Text("       "));

            }
            operationsBox.getChildren().addAll(copyButtonComp, new Text("       "), pasteComponentButton);

            box.getChildren().add(operationsBox);


            if(box.getChildren().size() == 0){
                box.getChildren().add(new Text("empty."));
            }
            componentList.getPanes().add(new TitledPane(c.getClass().getSimpleName().replaceAll("(.)([ A-Z])", "$1 $2"), box));
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

                                // set field to new instance of copied component
                                Object o = privateField.get(copiedComponent.component);
                                if(o instanceof Vector3f)
                                {
                                    f.set(c, new Vector3f((Vector3f) o));
                                }
                                else if (o instanceof Vector2f){
                                    f.set(c, new Vector2f((Vector2f) o));
                                }
                                else if (o instanceof Vector4f){
                                    f.set(c, new Vector4f((Vector4f) o));
                                }
                                else if (o instanceof Quaternionf){
                                    f.set(c, new Quaternionf((Quaternionf) o));
                                }
                                else if (o instanceof Matrix4f){
                                    f.set(c, new Matrix4f((Matrix4f) o));
                                }
                                else if (o instanceof GLEngine.Core.Shaders.Color){
                                    f.set(c, new  GLEngine.Core.Shaders.Color(( GLEngine.Core.Shaders.Color) o));
                                }
                                else
                                    f.set(c, o);
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
