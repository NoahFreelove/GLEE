package com.glee.Panels;

import GLEngine.Core.Objects.Components.Component;

import java.lang.reflect.Field;

public class CopiedComponent {
    public Component component;
    public Class classType;

    public CopiedComponent(Component component, Class classType) {
        this.component = component;
        this.classType = classType;
    }
}
