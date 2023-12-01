package com.clicks.aivideoeditor.controller;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class VideoFileSaver extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create a FileChooser for selecting a video file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Video File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mkv")
        );

        // Show the FileChooser dialog to select a video file
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            // Ask the user for the desired file name
            TextInputDialog dialog = new TextInputDialog("output_video");
            dialog.setTitle("Save Video As");
            dialog.setHeaderText(null);
            dialog.setContentText("Enter the name for the saved video file:");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(fileName -> {
                // Determine the output file path
                String outputFilePath = selectedFile.getParent() + File.separator + fileName + ".mp4";

                try {
                    // Copy the selected video file to the output file path
                    Files.copy(selectedFile.toPath(), new File(outputFilePath).toPath(), StandardCopyOption.REPLACE_EXISTING);

                    // Show a success message
                    showAlert("Success", "Video file saved to: " + outputFilePath);
                } catch (Exception e) {
                    // Show an error message if an exception occurs
                    showAlert("Error", "Error saving video file: " + e.getMessage());
                }
            });
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
