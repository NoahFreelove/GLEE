module com.glee.glee {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.joml;
    requires org.lwjgl.openal;
    requires org.lwjgl.stb;
    requires jbullet;
    requires org.lwjgl.opengl;
    requires vecmath;
    requires org.lwjgl.glfw;

    opens glee to javafx.fxml;
    exports glee;
    exports glee.Panels;
    opens glee.Panels to javafx.fxml;
    opens glee.TestComponents to javafx.fxml;
    exports glee.TestComponents;
    exports GLEngine.Core.Shaders;
    opens GLEngine.Core.Shaders to javafx.fxml;
}