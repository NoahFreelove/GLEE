package com.glee.TestComponents;

import GLEngine.Core.Interfaces.EditorVisible;
import GLEngine.Core.Objects.Components.Component;
import GLEngine.Core.Shaders.Color;
import org.joml.Quaternionf;

public class RegisterTest extends Component {
    @EditorVisible
    public float floatTest = 1;
    @EditorVisible
    public String stringTest = "";
    @EditorVisible
    public boolean boolValue = false;
    @EditorVisible
    public Quaternionf quaternionTest = new Quaternionf();
    @EditorVisible
    public Color colorTest = new Color();
}