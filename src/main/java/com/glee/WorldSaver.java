package com.glee;

import GLEngine.Core.Interfaces.EditorVisible;
import GLEngine.Core.Objects.Components.Component;
import GLEngine.Core.Objects.GameObject;
import GLEngine.Core.Objects.GameObjectSaveData;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;

public class WorldSaver {
    public static void saveWorld(String path) {
        File oldFile = new File(path);
        if(oldFile.exists())
            oldFile.delete();
        File newFile = new File(path);

        try {
            FileWriter w = new FileWriter(newFile);
            for (GameObject go :
                    Editor.activeWorld.GameObjects()) {
                GameObjectSaveData data = go.getSaveData();

                w.write("///START GAMEOBJECT///\n");
                w.write("POS (" + go.getPosition().x + "," + go.getPosition().y + "," + go.getPosition().z + ")\n");
                w.write("ROT (" + go.getRotation().x + "," + go.getRotation().y + "," + go.getRotation().z + ")\n");
                w.write("SCA (" + go.getScale().x + "," + go.getScale().y + "," + go.getScale().z + ")\n");
                w.write("MOD \"" + data.modelPath + "\"\n");
                w.write("TEX \"" + data.texturePath + "\"\n");
                w.write("NAME " + data.name + "\n");
                w.write("TAG " + data.tag + "\n");

                for (Component c :
                        go.getComponents()) {
                    w.write("\n///COMP///\n");
                    w.write("CLASS \"" + c.getClass().getName() + "\"\n");

                    Field[] fields = c.getClass().getDeclaredFields();

                    for (Field f :
                            fields) {
                        if(f.isAnnotationPresent(EditorVisible.class)){
                            f.setAccessible(true);
                            String objectValue = getObjectValue(f.get(c));

                            w.write("FIELD \"" + f.getName() + "\":" + objectValue + ":"  + f.get(c).getClass().getSimpleName() + "\n");
                        }
                    }

                    w.write("FIELD \"enabled\":" + c.isEnabled() + ":boolean\n");
                    w.write("///END COMP///\n\n");
                }

                w.write("///END GAMEOBJECT///\n\n");

            }
            w.close();
        }catch (Exception e){
            System.out.println("Error while saving world file: " + e.getMessage());
        }
    }

    private static String getObjectValue(Object o) {
        if(o instanceof Vector3f v){
            return v.x + "f," + v.y + "f," + v.z + "f";
        }
        else if (o instanceof Vector2f v)
        {
            return v.x + "f," + v.y + "f";
        }
        else if (o instanceof Boolean b)
        {
            return b.toString();
        }
        else if (o instanceof Integer i)
        {
            return i.toString();
        }
        else if (o instanceof Float f)
        {
            return f.toString();
        }

        return o.toString();
    }
}
