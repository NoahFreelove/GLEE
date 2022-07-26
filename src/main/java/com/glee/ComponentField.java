package com.glee;

import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.lang.reflect.Modifier;

public class ComponentField extends GridPane {
    private Text componentName;
    private Text componentType;
    private HBox valueBox;
    private Text valueText;
    public ComponentField(String name, String type, Object value, int modifiers){
        super();
        this.setStyle("-fx-background-color: #868686;");
        this.setPrefSize(250, 50);
        this.setLayoutX(350);
        this.setLayoutY(0);

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
        valueBox.getChildren().add(valueText);
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
        spacer.setPrefWidth(250);
        spacer.setTranslateY(20);
        this.add(spacer, 0, 3);
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
