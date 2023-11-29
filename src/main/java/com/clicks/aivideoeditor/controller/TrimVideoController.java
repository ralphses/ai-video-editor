package com.clicks.aivideoeditor.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

public class TrimVideoController extends Controller implements Initializable {

    @FXML
    private Button cancelBtn;

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
    private Button submitBtn;

    private String stopTimeValue;

    @FXML
    void cancel(ActionEvent event) {

    }

    @FXML
    void reset(ActionEvent event) {
        stopTime.setText(this.stopTimeValue);
        startTime.setText("00:00:00");
    }

    @FXML
    void submit(ActionEvent event) {
        String start = startTime.getText().trim();
        String stop = stopTime.getText().trim();

        String[] startInfo = start.split(":");
        String[] stopInfo = stop.split(":");

        if(startInfo.length != 3 || stopInfo.length != 3) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter valid start and stop time. Accepted format is hh:mm:ss");
            alert.showAndWait().ifPresent(buttonType -> {});
        }

        int stopSeconds = convertHMSToSeconds(stopTime.getText().trim());
        int startSeconds = convertHMSToSeconds(startTime.getText().trim());

        String output = "src/main/resources/temp/temp.mp4";

        try {
            String result = trimVideo(currentFile.getAbsolutePath(), output, startSeconds, stopSeconds);
            System.out.println("result = " + result);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

    public static String trimVideo(String inputVideo, String outputVideo, double startTime, double endTime)
            throws IOException, InterruptedException {
        // Specify the path to your Python script
        String pythonScriptPath = "path/to/video_operations.py";

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
