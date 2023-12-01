package com.clicks.aivideoeditor.controller;

import com.clicks.aivideoeditor.utils.StageSwitcher;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.scene.input.KeyCode.P;
import static javafx.scene.input.KeyCombination.CONTROL_DOWN;

public class TrimVideoController extends Controller implements Initializable {

    @FXML
    private AnchorPane container;

    @FXML
    private Label currentVideoLabel;

    @FXML
    private Button resetBtn;

    @FXML
    private TextField startTime;

    @FXML
    private TextField stopTime;

    @FXML
    private Button doneBtn;

    @FXML
    private Button submitBtn;

    private String stopTimeValue;

    @FXML
    void done(ActionEvent event) {

       if(doneBtn.getText().trim().equals("Done")) {
           Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save current file?");

           alert.showAndWait().ifPresent(buttonType -> {
               if (buttonType.equals(ButtonType.OK)) {

                   String directory = browseDirectory(event).getAbsolutePath();

                   System.out.println("directory = " + directory);

                   TextInputDialog dialog = new TextInputDialog("output_video");
                   dialog.setTitle("Save Video As");
                   dialog.setHeaderText(null);
                   dialog.setContentText("Enter the name for the saved video file:");

                   Optional<String> result = dialog.showAndWait();

                   result.ifPresent(fileName -> {
                       // Determine the output file path
                       String outputFilePath = directory + "/" + fileName + ".mp4";

                       try {
                           // Copy the selected video file to the output file path
                           Files.copy(tempFile.toPath(), new File(outputFilePath).toPath(), StandardCopyOption.REPLACE_EXISTING);

                           // Show a success message
                           showAlert("Success", "Video file saved to: " + outputFilePath, event);
                       } catch (Exception e) {
                           // Show an error message if an exception occurs
                           showAlert("Error", "Error saving video file: " + e.getMessage(), event);
                       }
                   });
               }
           });
       }
       else StageSwitcher.toWelcomePage(event);
    }

    @FXML
    void reset(ActionEvent event) {
        stopTime.setText(this.stopTimeValue);
        startTime.setText("00:00:00");
        submitBtn.setDisable(false);
        doneBtn.setText("Cancel");

    }

    @FXML
    void submit(ActionEvent event) {

        String start = startTime.getText().trim();
        String stop = stopTime.getText().trim();

        String[] startInfo = start.split(":");
        String[] stopInfo = stop.split(":");

        if (startInfo.length != 3 || stopInfo.length != 3) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter valid start and stop time. Accepted format is hh:mm:ss");
            alert.showAndWait().ifPresent(buttonType -> {
            });
        }

        submitBtn.setDisable(true);

        int stopSeconds = convertHMSToSeconds(stopTime.getText().trim());
        int startSeconds = convertHMSToSeconds(startTime.getText().trim());

        System.out.println("startSeconds = " + startSeconds);
        System.out.println("stopSeconds = " + stopSeconds);

        String output = "src/main/resources/temp/temp.mp4";


        try {
            String result = trimVideo(currentFile.getAbsolutePath(), output, startSeconds, stopSeconds);
            tempFile = new File(output);

            System.out.println("result = " + result);
            doneBtn.setText("Done");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            EventHandler<KeyEvent> keyEventEventHandler = keyEvent -> {
                if (new KeyCodeCombination(P, CONTROL_DOWN).match(keyEvent) ||
                        (keyEvent.getCode().equals(P))) {
                    handlePreview(keyEvent);
                }
            };
            container.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyEventEventHandler);
        });

        String editorScript = "src/Python/video_edit/editor.py";
        String operation = "get_duration";
        String input_file = currentFile.getAbsolutePath();

        ProcessBuilder processBuilder = new ProcessBuilder(
                "python", editorScript, operation, input_file);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                String s = output.toString();
                int duration = (int) Double.parseDouble(s);
                String endTime = convertSecondsToHMS(duration);
                this.stopTimeValue = endTime;
                stopTime.setText(endTime);
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void showAlert(String title, String content, ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType.equals(ButtonType.OK)) {
                StageSwitcher.toWelcomePage(event);
            }
            else event.consume();
        });
    }

    public static String trimVideo(String inputVideo, String outputVideo, double startTime, double endTime)
            throws IOException, InterruptedException {
        // Specify the path to your Python script
        String pythonScriptPath = "src/Python/video_edit/editor.py";

        // Create the process builder
        ProcessBuilder processBuilder = new ProcessBuilder(
                "python", pythonScriptPath, "trim_video", inputVideo, outputVideo, String.valueOf(startTime), String.valueOf(endTime));

        // Start the process
        Process process = processBuilder.start();

        // Read the output of the Python script
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        // Wait for the process to complete
        int exitCode = process.waitFor();
        System.out.println("Python script exited with code " + exitCode);

        // Return the result
        return output.toString().trim();
    }
}
