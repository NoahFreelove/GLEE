package com.glee.TestComponents;

import GLEngine.Core.Interfaces.EditorVisible;
import GLEngine.Core.Objects.Components.Component;
public class RegisterTest extends Component {
    @EditorVisible
    public float floatTest = 1;
    @EditorVisible
    public String stringTest = "";
    @EditorVisible
    public boolean boolValue = false;
}