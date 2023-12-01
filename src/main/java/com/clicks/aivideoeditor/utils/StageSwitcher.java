package com.clicks.aivideoeditor.utils;

import com.clicks.aivideoeditor.AiVideoEditorApp;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class StageSwitcher {

    public static void toPage(FXMLLoader loader, Event event, int width, int height, String pageTitle) {
        try {
            Parent load = loader.load();
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setOnCloseRequest(winEvent -> new Alert(Alert.AlertType.CONFIRMATION, "Exit " + pageTitle)
                    .showAndWait()
                    .ifPresent(present -> {
                        if (present == ButtonType.OK) window.close();
                        else winEvent.consume();
                    })
            );


            setWindow(window, load, width, height, pageTitle);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setWindow(Stage stage, Parent root, int width, int height, String stageTitle) {
        Scene scene = new Scene(root, width, height);
        stage.setScene(scene);

        double centerX = (Screen.getPrimary().getVisualBounds().getWidth() - stage.getWidth()) / 2;
        double centerY = (Screen.getPrimary().getVisualBounds().getHeight() - stage.getHeight()) / 2;

        // Set the stage position to center it on the screen
        stage.setX(centerX);
        stage.setY(centerY);
        stage.setTitle(stageTitle);
        stage.setResizable(false);
        stage.show();
    }

    public static void toWelcomePage(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(AiVideoEditorApp.class.getResource("main.fxml"));
        StageSwitcher.toPage(fxmlLoader, event, 500, 300, "Welcome Page");
    }
}
