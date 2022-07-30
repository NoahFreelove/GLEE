package glee;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;

public class Main extends Application {
    public static Stage mainStage;
    @Override
    public void start(Stage stage) {
        mainStage = stage;
        Group root = new Group();
        Scene empty = new Scene(root, 400, 400);
        empty.setFill(Color.BLACK);
        mainStage.setResizable(false);
        mainStage.setTitle("GLEE");
        mainStage.setScene(empty);
        mainStage.show();
        stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> System.exit(0));

        //loadMenu();
        Editor.openEditor(Editor.projectFile);
    }

    public static void loadMenu(){
        Group root = new Group();
        Scene startupScene = new Scene(root, 400, 400);
        startupScene.setFill(Color.BLACK);
        mainStage.setResizable(false);
        mainStage.setTitle("GLEE Startup");
        mainStage.setScene(startupScene);
        mainStage.show();


        // Create quit and start buttons
        Button quitButton = new Button("Quit");
        quitButton.setOnAction(e -> {
            System.exit(0);
        });
        Button openProject = new Button("Open Project");
        openProject.setOnAction(e -> {
            // Open file chooser and get file
            File file = openProjectDialog();
            if(file != null){
                Editor.openEditor(file);
            }
        });

        // Set position of buttons
        quitButton.setLayoutX(185);
        quitButton.setLayoutY(120);
        openProject.setLayoutX(160);
        openProject.setLayoutY(90);

        // Create title text
        Text title = new Text("G.L.E.E");
        title.setLayoutX(160);
        title.setLayoutY(65);
        title.setFill(Color.WHITE);
        title.setStyle("-fx-font-size: 30px;");

        root.getChildren().addAll(quitButton, openProject, title);
    }
    public static File openProjectDialog(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Project");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GLEE Project", "*.glengine")
        );
        return fileChooser.showOpenDialog(mainStage);
    }

    public static void main(String[] args) {
        launch();
    }

}