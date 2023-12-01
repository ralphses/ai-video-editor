package com.clicks.aivideoeditor.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

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

    protected void saveToDirectory(String videoFilePath, String outputDirectory) {

        File videoFile = new File(videoFilePath);
        File outputDir = new File(outputDirectory);

        // Create the output directory if it doesn't exist
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Prepare the output file path
        String outputFilePath = outputDirectory + File.separator + videoFile.getName();

        try {
            // Copy the video file to the output directory
            Files.copy(videoFile.toPath(), new File(outputFilePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Video file saved to: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Error saving video file: " + e.getMessage());
        }
    }

    protected void handlePreview(Event event) {
        if (tempFile != null) {
            //check if VLC player is installed
            String vlcPath = isVlcInstalled();
            if (!vlcPath.isEmpty()) {
                playWithVlc(tempFile, vlcPath);
            } else playWithWindowsPlayer(tempFile);
        }
    }

    private void playWithWindowsPlayer(File tempFile) {
        try {
            Desktop.getDesktop().open(tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void playWithVlc(File tempFile, String vlcPath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(vlcPath, tempFile.getAbsolutePath());
            processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String isVlcInstalled() {
        // Check if VLC executable exists
        String vlcPath1 = "C:\\Program Files\\VideoLAN\\VLC\\vlc.exe"; // Adjust the path as per your installation
        String vlcPath2 = "C:\\Program Files (x86)\\VideoLAN\\VLC\\vlc.exe"; // Adjust the path as per your installation
        File vlcExecutable = new File(vlcPath1);
        File vlcExecutable2 = new File(vlcPath2);

        return (vlcExecutable.exists()) ? vlcPath1 : (vlcExecutable2.exists()) ? vlcPath2 : "";
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
