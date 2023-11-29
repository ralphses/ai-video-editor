package com.clicks.aivideoeditor.controller;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

import static java.lang.String.format;

public class Controller {

    public static final String ROTATE = "rotate";
    public static final String TRIM = "trim";
    public static final String REPLACE_AUDIO = "replaceAudio";
    public static final String LOAD_VIDEO = "loadVideo";
    public static final String PREVIEW = "preview";

    public static File currentFile;
    public static File tempFile;


    protected File browseDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Destination folder");
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        return directoryChooser.showDialog(stage);
    }

    protected File browseFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");

        // Set an initial directory (optional)
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Add an extension filter (only video files)
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Video files",
                "*.mp4", "*.mov", "*.avi", "*.mkv", "*.wmv", "*.flv", "*.webm", "*.m4v", "*.mpg", "*.mpeg",
                "*.3gp", "*.ogg", "*.ts", "*.m2ts", "*.vob", "*.swf", "*.rm", "*.asf");

        fileChooser.getExtensionFilters().add(extFilter);

        // Get the stage (window) to show the file dialog

        // Show the file dialog and get the selected file
        return fileChooser.showOpenDialog(stage);
    }

    protected String convertSecondsToHMS(int seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("Seconds must be non-negative.");
        }

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        // Format the time as "hh:mm:ss"
        return format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    public static int convertHMSToSeconds(String timeString) {
        if (timeString == null || !timeString.matches("\\d{2}:\\d{2}:\\d{2}")) {
            throw new IllegalArgumentException("Invalid time string format. Use 'hh:mm:ss'.");
        }

        String[] timeComponents = timeString.split(":");
        int hours = Integer.parseInt(timeComponents[0]);
        int minutes = Integer.parseInt(timeComponents[1]);
        int seconds = Integer.parseInt(timeComponents[2]);

        // Calculate the total seconds
        return hours * 3600 + minutes * 60 + seconds;
    }
}
