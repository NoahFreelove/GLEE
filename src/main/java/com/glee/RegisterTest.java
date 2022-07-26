package com.glee;

import GLEngine.Core.Interfaces.EditorVisible;
import GLEngine.Core.Objects.Components.Component;
import org.joml.Vector3f;
public class RegisterTest extends Component {
    @EditorVisible
    private Vector3f Position = new Vector3f(0,0,0);
    @EditorVisible
    public Vector3f Rotation = new Vector3f(0,0,0);
    @EditorVisible
    public Vector3f Scale = new Vector3f(0,0,0);

}