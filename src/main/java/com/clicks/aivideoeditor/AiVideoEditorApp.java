package com.clicks.aivideoeditor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AiVideoEditorApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AiVideoEditorApp.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 300);

        stage.setTitle("AI Video Editor Application");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }



    public static void main(String[] args) {
//        new MediaPlayerWaiter<>()
        launch();
    }
}