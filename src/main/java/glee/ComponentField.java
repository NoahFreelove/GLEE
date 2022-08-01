package glee;

import GLEngine.Core.Interfaces.EditorVariableAttribute;
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
import java.text.DecimalFormat;

public class ComponentField extends GridPane {
    private Text componentName;
    private Text componentType;
    private HBox valueBox;
    private Component parentComp;
    private float min;
    private float max;
    private boolean intLock;
    private boolean piLock;
    private boolean hasRangeAttributes;
    private String header = "";
    private String tooltip = "";

    private double fieldOffset = 12;
    public ComponentField(String name, String originalName, String type, Object value, int modifiers, Component parentComp, EditorVariableAttribute attributes) {
        super();

        if(attributes != null)
        {
            hasRangeAttributes = true;
            min = attributes.min();
            max = attributes.max();
            intLock = attributes.intLock();
            piLock = attributes.piLock();
            header = attributes.header();
            tooltip = attributes.tooltip();

            if(min == Float.MIN_VALUE && max == Float.MAX_VALUE){
                hasRangeAttributes = false;
            }
        }

        this.setStyle("-fx-background-color: #868686;");
        this.setPrefSize(250, 50);
        this.setLayoutX(350);
        this.setLayoutY(0);
        this.parentComp = parentComp;

        //increase vertical max height
        this.setPrefHeight(80);
        this.setPadding(new javafx.geometry.Insets(5,0,5,0));
        if(header.equals("")){
            this.setPrefHeight(80);
        }
        else {
            this.setPrefHeight(110);
            Text headerText = new Text(header);
            headerText.setFill(Color.WHITE);
            headerText.setStyle("-fx-font-size: 18px;");
            this.add(headerText, 0, 0);
        }

        componentName = new Text();
        componentType = new Text();

        componentName.setFill(Color.WHITE);
        componentType.setFill(Color.WHITE);

        componentName.setTranslateX(10);
        componentType.setTranslateX(10);
        componentType.setTranslateY(10);

        this.add(componentName, 0, 1);
        //this.add(componentType, 1, 0);
        valueBox = new HBox();
        Node valueNode = generateSpecificField(value, originalName);
        valueBox.getChildren().add(valueNode);

        // Make text take up less space
        componentName.setWrappingWidth(200);
        componentType.setWrappingWidth(200);

        this.add(valueBox, 0,2);
        componentName.setStyle("-fx-font-size: 10px;");
        componentType.setStyle("-fx-font-size: 10px;");

        String modifierStr = generateModifierString(modifiers);
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


        if(value instanceof Float fv){
            n = generateFloat(value, fieldName, fv);
        }
        else if(value instanceof Integer iv){
            n = generateInt(value, fieldName, iv);
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
            TextField t = new TextField((String)value);
            if(!tooltip.equals("")){
                t.tooltipProperty().setValue(new Tooltip(tooltip));
            }
            t.setTranslateX(fieldOffset);
            t.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    Field f = parentComp.getClass().getField(fieldName);
                    f.set(parentComp, newValue);
                } catch (Exception ignore){}
            });
            n = t;
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

    private Node generateFloat(Object value, String fieldName, Float fv) {
        Node n;
        // Format pi to 2 decimals, everything else we just want 6 decimals
        DecimalFormat df = new DecimalFormat("######.######");
        if(hasRangeAttributes){
            HBox container = new HBox();
            Text minText = new Text(min + "  ");
            minText.setFill(Color.WHITE);
            Text maxText = new Text("  " + max);
            maxText.setFill(Color.WHITE);

            Slider slider = new Slider();

            TextField valueField = new TextField(df.format(fv));
            if(!tooltip.equals("")){
                valueField.tooltipProperty().setValue(new Tooltip(tooltip));
            }
            valueField.setPrefWidth(100);
            valueField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    if(Float.parseFloat(newValue) >= min && Float.parseFloat(newValue) <= max){
                        Field f = parentComp.getClass().getDeclaredField(fieldName);
                        f.setAccessible(true);
                        f.set(parentComp, Float.parseFloat(newValue));
                    }

                }catch (Exception ignore){}
            });
            slider.setMin(min);
            slider.setMax(max);
            slider.setValue((float) value);
            slider.setTranslateX(fieldOffset);
            slider.setTranslateY(fieldOffset);
            slider.setPrefWidth(200);
            slider.setPrefHeight(30);

            // set slider progress
            slider.setValue(fv);
            slider.valueProperty().addListener(event -> {
                try {
                    float newValue = (float)slider.getValue();
                    if(piLock){
                        newValue = (float) ((float)Math.round(newValue / Math.PI * 4) * Math.PI / 4);
                        slider.setValue(Double.parseDouble(new DecimalFormat("###.##").format(newValue)));
                    }
                    else if(intLock)
                    {
                        newValue = (int)newValue;
                        slider.setValue(newValue);
                    }

                    Field f = parentComp.getClass().getDeclaredField(fieldName);
                    f.setAccessible(true);
                    f.set(parentComp, newValue);
                    valueField.setText(df.format(newValue));
                }
                catch (Exception ignore){
                    System.out.println(ignore);
                }
            });

            container.getChildren().addAll(minText, valueField, maxText, slider);


            n = container;
        }
        else{

            TextField t = new TextField(value.toString());
            t.setTranslateX(fieldOffset);
            if(!tooltip.equals("")){
                t.tooltipProperty().setValue(new Tooltip(tooltip));
            }
            // Set value after finishing typing
            t.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    Field f = parentComp.getClass().getField(fieldName);
                    f.set(parentComp, Float.parseFloat(newValue));
                }catch (Exception ignore){}
            });
            n = t;
        }
        return n;
    }

    private Node generateInt(Object value, String fieldName, Integer iv) {
        Node n;
        if(hasRangeAttributes) {
            HBox container = new HBox();
            Text minText = new Text(min + "  ");
            minText.setFill(Color.WHITE);
            Text maxText = new Text("  " + max);
            maxText.setFill(Color.WHITE);

            Slider slider = new Slider();

            TextField valueField = new TextField(String.valueOf(iv));
            if(!tooltip.equals("")){
                valueField.tooltipProperty().setValue(new Tooltip(tooltip));
            }
            valueField.setPrefWidth(100);
            valueField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    if (Float.parseFloat(newValue) >= min && Integer.parseInt(newValue) <= max) {
                        Field f = parentComp.getClass().getDeclaredField(fieldName);
                        f.setAccessible(true);
                        f.set(parentComp, Float.parseFloat(newValue));
                    }

                } catch (Exception ignore) {
                }
            });
            slider.setMin(Math.floor(min));
            slider.setMax(Math.floor(max));
            slider.setValue((int) value);
            slider.setTranslateX(fieldOffset);
            slider.setTranslateY(fieldOffset);
            slider.setPrefWidth(200);
            slider.setPrefHeight(30);
            slider.setBlockIncrement(1);

            // set slider progress
            slider.setValue(iv);
            slider.valueProperty().addListener((event, oldval, newval) -> {
                slider.setValue(newval.intValue());
                if(oldval.intValue() != (int)slider.getValue()){
                    try {
                        int newValue = (int) slider.getValue();
                        Field f = parentComp.getClass().getDeclaredField(fieldName);
                        f.setAccessible(true);
                        f.set(parentComp, newValue);
                        valueField.setText(newValue + "");
                    } catch (Exception ignore) {
                        System.out.println(ignore);
                    }
                }
            });

            container.getChildren().addAll(minText, valueField, maxText, slider);


            n = container;
        }
        else{

            TextField t = new TextField(value.toString());
            t.setTranslateX(fieldOffset);
            if(!tooltip.equals("")){
                t.tooltipProperty().setValue(new Tooltip(tooltip));
            }
            // Set value after finishing typing
            t.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    Field f = parentComp.getClass().getField(fieldName);
                    f.set(parentComp, Integer.parseInt(newValue));
                }catch (Exception ignore){}
            });
            n = t;
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
