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

    opens com.glee to javafx.fxml;
    exports com.glee;
}