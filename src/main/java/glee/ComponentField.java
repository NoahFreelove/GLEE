package glee;

import GLEngine.Core.Objects.Components.Component;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.joml.Quaternionf;
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

    private double fieldOffset = 12;
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
        componentName.setText(name.replaceAll("(.)([ A-Z])", "$1 $2"));
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
            n.setTranslateX(fieldOffset);
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
            n.setTranslateX(fieldOffset);
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
            n = generateVector2((Vector2f) value, fieldName);
        }
        else if(value instanceof Vector3f){
            n = generateVector3((Vector3f) value, fieldName);
        }
        else if(value instanceof Quaternionf){
            n = generateQuaternion((Quaternionf) value, fieldName);
        }
        else if(value instanceof String){
            n = new TextField((String)value);
            n.setTranslateX(fieldOffset);
            ((TextField)n).textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    Field f = parentComp.getClass().getField(fieldName);
                    f.set(parentComp, newValue);
                } catch (Exception ignore){}
            });
        }
        else if(value instanceof GLEngine.Core.Shaders.Color c){
            n = generateColor(c, fieldName);
        }
        else{
            n = new TextField(value.toString());
            n.setTranslateX(fieldOffset);
            ((TextField)n).textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    Field f = parentComp.getClass().getField(fieldName);
                    f.set(parentComp, newValue);
                } catch (Exception ignore){}
            });
        }
        return n;
    }

    public Node generateColor(GLEngine.Core.Shaders.Color c, String fieldName){
        HBox hbox = new HBox();
        hbox.setSpacing(5);
        Text r = new Text("r");
        r.setTranslateX(3);
        Text g = new Text("g");
        Text b = new Text("b");
        Text a = new Text("a");
        ColorPicker colorPicker = new ColorPicker();

        TextField rField = new TextField(c.R() + "");

        rField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Field f = parentComp.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                GLEngine.Core.Shaders.Color color = (GLEngine.Core.Shaders.Color) f.get(parentComp);
                color.setR(Float.parseFloat(newValue));
                f.set(parentComp, color);
                colorPicker.setValue(new Color(color.R(), color.G(), color.B(), color.A()));

            } catch (Exception ignore){}
        });
        TextField gField = new TextField(c.G() + "");
        gField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Field f = parentComp.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                GLEngine.Core.Shaders.Color color = (GLEngine.Core.Shaders.Color) f.get(parentComp);
                color.setG(Float.parseFloat(newValue));
                f.set(parentComp, color);
                colorPicker.setValue(new Color(color.R(), color.G(), color.B(), color.A()));
            } catch (Exception ignore){}
        });

        TextField bField = new TextField(c.B() + "");
        bField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Field f = parentComp.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                GLEngine.Core.Shaders.Color color = (GLEngine.Core.Shaders.Color) f.get(parentComp);
                color.setB(Float.parseFloat(newValue));
                f.set(parentComp, color);
                colorPicker.setValue(new Color(color.R(), color.G(), color.B(), color.A()));

            } catch (Exception ignore){}
        });

        TextField aField = new TextField(c.A() + "");
        bField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Field f = parentComp.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                GLEngine.Core.Shaders.Color color = (GLEngine.Core.Shaders.Color) f.get(parentComp);
                color.setA(Float.parseFloat(newValue));
                f.set(parentComp, color);
                colorPicker.setValue(new Color(color.R(), color.G(), color.B(), color.A()));

            } catch (Exception ignore){}
        });
        colorPicker.setOnAction(event1 -> {
            try {
                Field f = parentComp.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                GLEngine.Core.Shaders.Color color = (GLEngine.Core.Shaders.Color) f.get(parentComp);
                color.setColor((float) colorPicker.getValue().getRed(), (float) colorPicker.getValue().getGreen(), (float) colorPicker.getValue().getBlue(), (float) colorPicker.getValue().getOpacity());
                rField.setText(color.R() + "");
                gField.setText(color.G() + "");
                bField.setText(color.B() + "");
                aField.setText(color.A() + "");

                f.set(parentComp, color);
            } catch (Exception ignore){}
        });

        // set the color picker to the current color
        colorPicker.setValue(Color.color(c.R(), c.G(), c.B(), c.A()));

        rField.setPrefWidth(65);
        gField.setPrefWidth(65);
        bField.setPrefWidth(65);
        aField.setPrefWidth(65);

        hbox.getChildren().addAll(r, rField, g, gField, b, bField, a, aField, colorPicker);
        return hbox;
    }

    private Node generateVector2(Vector2f value, String fieldName) {
        HBox hbox = new HBox();
        hbox.setSpacing(5);
        Text x = new Text("x");
        x.setTranslateX(3);
        Text y = new Text("y");
        TextField xField = new TextField(value.x + "");
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
        TextField yField = new TextField(value.y + "");
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
        return hbox;
    }

    private Node generateVector3(Vector3f value, String fieldName) {
        HBox hbox = new HBox();
        hbox.setSpacing(5);
        Text x = new Text("x");
        x.setTranslateX(3);
        Text y = new Text("y");
        Text z = new Text("z");
        TextField xField = new TextField(value.x + "");

        xField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Field f = parentComp.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                Vector3f vec = (Vector3f) f.get(parentComp);
                vec.x = Float.parseFloat(newValue);
                f.set(parentComp, vec);

            } catch (Exception ignore){}
        });
        TextField yField = new TextField(value.y + "");
        yField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Field f = parentComp.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                Vector3f vec = (Vector3f) f.get(parentComp);
                vec.y = Float.parseFloat(newValue);
                f.set(parentComp, vec);

            } catch (Exception ignore){}
        });

        TextField zField = new TextField(value.z + "");
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
        return hbox;
    }

    private Node generateQuaternion(Quaternionf value, String fieldName) {
        Node n;
        HBox hbox = new HBox();
        hbox.setSpacing(5);
        Text x = new Text("x");
        x.setTranslateX(3);
        Text y = new Text("y");
        Text z = new Text("z");
        Text w = new Text("w");

        TextField xField = new TextField(value.x + "");

        xField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Field f = parentComp.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                Quaternionf vec = (Quaternionf) f.get(parentComp);
                vec.x = Float.parseFloat(newValue);
                f.set(parentComp, vec);

            } catch (Exception ignore){}
        });
        TextField yField = new TextField(value.y + "");
        yField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Field f = parentComp.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                Quaternionf vec = (Quaternionf) f.get(parentComp);
                vec.y = Float.parseFloat(newValue);
                f.set(parentComp, vec);

            } catch (Exception ignore){}
        });

        TextField zField = new TextField(value.z + "");
        zField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Field f = parentComp.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                Quaternionf vec = (Quaternionf) f.get(parentComp);
                vec.z = Float.parseFloat(newValue);
                f.set(parentComp, vec);

            } catch (Exception ignore){}
        });

        TextField wField = new TextField(value.w + "");
        zField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Field f = parentComp.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
                Quaternionf vec = (Quaternionf) f.get(parentComp);
                vec.w = Float.parseFloat(newValue);
                f.set(parentComp, vec);

            } catch (Exception ignore){}
        });

        xField.setPrefWidth(65);
        yField.setPrefWidth(65);
        zField.setPrefWidth(65);
        wField.setPrefWidth(65);

        hbox.getChildren().addAll(x, xField, y, yField, z, zField, w, wField);
        return hbox;
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