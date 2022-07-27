package com.glee;

import GLEngine.Core.Objects.Components.Component;
import GLEngine.Core.Objects.GameObject;
import GLEngine.Core.Objects.Transform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ComponentField extends GridPane {
    private Text componentName;
    private Text componentType;
    private HBox valueBox;
    private Text valueText;
    private Component parentComp;

    private double textFieldOffset = 10;
    public ComponentField(String name, String type, Object value, int modifiers, Component parentComp){
        super();
        this.setStyle("-fx-background-color: #868686;");
        this.setPrefSize(250, 50);
        this.setLayoutX(350);
        this.setLayoutY(0);
        this.parentComp = parentComp;

        //increase vertical max height
        this.setPrefHeight(100);
        this.setPadding(new javafx.geometry.Insets(5,0,5,0));

        componentName = new Text();
        componentType = new Text();
        valueText = new Text(convertValueToString(value));

        componentName.setFill(Color.WHITE);
        componentType.setFill(Color.WHITE);
        valueText.setFill(Color.BLACK);

        componentName.setTranslateX(10);
        componentType.setTranslateX(10);
        componentType.setTranslateY(10);
        valueText.setTranslateX(10);

        this.add(componentName, 0, 0);
        this.add(componentType, 0, 2);
        valueBox = new HBox();
        Node valueNode = generateSpecificField(value, name);
        valueBox.getChildren().add(valueNode);

        this.add(valueBox, 0, 1);
        componentName.setStyle("-fx-font-size: 20px;");
        componentType.setStyle("-fx-font-size: 15px;");
        valueText.setStyle("-fx-font-size: 15px;");

        String modifierStr = generateModifierString(modifiers);
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        componentName.setText(name);
        componentType.setText(type + " :" + modifierStr);
        // add spacer
        Separator spacer = new Separator();
        spacer.setPrefWidth(400);
        spacer.setTranslateY(20);
        this.add(spacer, 0, 3);
    }

    private Node generateSpecificField(Object value, String fieldName) {
        Node n;
        if(value instanceof Integer || value instanceof Float){
            n = new TextField(value.toString());
            // Set value after finishing typing
            ((TextField)n).textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    Field f = parentComp.getClass().getField(fieldName);
                    f.set(parentComp, Float.parseFloat(newValue));
                }catch (Exception ignore){}
            });
        }
        else if(value instanceof Boolean){
            n = new CheckBox();
            n.setTranslateX(10);
            ((CheckBox)n).setSelected((Boolean)value);
            if(fieldName.equals("enabled"))
                ((CheckBox)n).selectedProperty().addListener((observableValue, aBoolean, newValue) -> parentComp.setEnabled(newValue));
            else {
                ((CheckBox)n).selectedProperty().addListener((observableValue, aBoolean, newValue) ->{
                    for (Field f :
                            parentComp.getClass().getDeclaredFields()) {
                        if(f.getName().equals(fieldName)){
                            try {
                                f.set(parentComp, newValue);
                            }catch (Exception e){
                                //ignore
                            }
                        }
                    }
                });
            }

        }
        else if(value instanceof Vector2f){
            HBox hbox = new HBox();
            hbox.setSpacing(5);
            Text x = new Text("x");
            x.setTranslateX(3);
            Text y = new Text("y");
            TextField xField = new TextField(((Vector2f)value).x + "");
            xField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    Field f = parentComp.getClass().getDeclaredField(fieldName);
                    f.setAccessible(true);
                    Vector2f vec = (Vector2f) f.get(parentComp);
                    vec.x = Float.parseFloat(newValue);
                    f.set(parentComp, vec);
                } catch (Exception ignore){
                }
            });
            TextField yField = new TextField(((Vector2f)value).y + "");
            yField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    Field f = parentComp.getClass().getDeclaredField(fieldName);
                    f.setAccessible(true);
                    Vector2f vec = (Vector2f) f.get(parentComp);
                    vec.y = Float.parseFloat(newValue);
                    f.set(parentComp, vec);
                } catch (Exception ignore){

                }
            });
            xField.setPrefWidth(65);
            yField.setPrefWidth(65);
            hbox.getChildren().addAll(x, xField, y, yField);
            n = hbox;
        }
        else if(value instanceof Vector3f){
            HBox hbox = new HBox();
            hbox.setSpacing(5);
            Text x = new Text("x");
            x.setTranslateX(3);
            Text y = new Text("y");
            Text z = new Text("z");
            TextField xField = new TextField(((Vector3f)value).x + "");

            xField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    Field f = parentComp.getClass().getDeclaredField(fieldName);
                    f.setAccessible(true);
                    Vector3f vec = (Vector3f) f.get(parentComp);
                    vec.x = Float.parseFloat(newValue);
                    f.set(parentComp, vec);

                } catch (Exception ignore){}
            });
            TextField yField = new TextField(((Vector3f)value).y + "");
            yField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    Field f = parentComp.getClass().getDeclaredField(fieldName);
                    f.setAccessible(true);
                    Vector3f vec = (Vector3f) f.get(parentComp);
                    vec.y = Float.parseFloat(newValue);
                    f.set(parentComp, vec);

                } catch (Exception ignore){}
            });

            TextField zField = new TextField(((Vector3f)value).z + "");
            zField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    Field f = parentComp.getClass().getDeclaredField(fieldName);
                    f.setAccessible(true);
                    Vector3f vec = (Vector3f) f.get(parentComp);
                    vec.z = Float.parseFloat(newValue);
                    f.set(parentComp, vec);

                } catch (Exception ignore){}
            });

            xField.setPrefWidth(65);
            yField.setPrefWidth(65);
            zField.setPrefWidth(65);

            hbox.getChildren().addAll(x, xField, y, yField, z, zField);
            n = hbox;
        }
        else if(value instanceof String){
            n = new TextField((String)value);
            n.setTranslateX(textFieldOffset);
            ((TextField)n).textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    Field f = parentComp.getClass().getField(fieldName);
                    f.set(parentComp, newValue);
                } catch (Exception ignore){}
            });
        }
        else{
            n = new TextField(value.toString());
            ((TextField)n).textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    Field f = parentComp.getClass().getField(fieldName);
                    f.set(parentComp, newValue);
                } catch (Exception ignore){}
            });
        }
        return n;
    }


    private String convertValueToString(Object value){
        String converted = "";

        if(value instanceof Integer){
            converted = ((Integer)value).toString();
        }else if(value instanceof Float){
            converted = value.toString();
        }else if(value instanceof Boolean){
            converted = ((Boolean)value).toString();
        }else if(value instanceof String){
            converted = (String)value;
        }
        else if (value instanceof Vector3f){
            converted = value.toString();
        }
        else if (value instanceof Vector2f){
            converted = value.toString();
        }
        else {
            converted = value.toString();
        }

        return converted;
    }

    private String generateModifierString(int modifiers){
        String modifierString = "";
        if(Modifier.isPublic(modifiers)){
            modifierString += " public";
        }
        if(Modifier.isProtected(modifiers)){
            modifierString += " protected";
        }
        if(Modifier.isPrivate(modifiers)){
            modifierString += " private";
        }
        if(Modifier.isStatic(modifiers)){
            modifierString += " static";
        }
        if(Modifier.isFinal(modifiers)){
            modifierString += " final";
        }
        if(Modifier.isTransient(modifiers)){
            modifierString += " transient";
        }
        if(Modifier.isVolatile(modifiers)){
            modifierString += " volatile";
        }
        if(Modifier.isNative(modifiers)){
            modifierString += " native";
        }
        if(Modifier.isSynchronized(modifiers)){
            modifierString += " synchronized";
        }
        if(Modifier.isInterface(modifiers)){
            modifierString += " interface";
        }
        if(Modifier.isAbstract(modifiers)){
            modifierString += " abstract";
        }
        if(Modifier.isStrict(modifiers)){
            modifierString += " strict";
        }
        if(Modifier.isSynchronized(modifiers)){
            modifierString += " synchronized";
        }
        return modifierString;
    }
}
