package com.clicks.aivideoeditor.controller;

import com.clicks.aivideoeditor.AiVideoEditorApp;
import com.clicks.aivideoeditor.utils.StageSwitcher;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static javafx.scene.input.KeyCode.O;
import static javafx.scene.input.KeyCode.P;
import static javafx.scene.input.KeyCombination.CONTROL_DOWN;

public class MainController extends Controller {

    public Button submitBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private AnchorPane container;

    @FXML
    private Label currentVideoLabel;

    @FXML
    private TextArea queryBox;

    @FXML
    private Button resetBtn;

    @FXML
    private Button selectVideoBtn;


    @FXML
    void cancel(ActionEvent event) {

    }

    @FXML
    void reset(ActionEvent event) {

    }

    @FXML
    void selectVideo(ActionEvent event) {

    }


    @FXML
    void submit(ActionEvent event) {

        String query = queryBox.getText().trim();

        if (!query.isEmpty()) {

            //Todo: process query to get intent
            String intent = getQueryIntent(query);

            //Todo: call intent function pass query
            callAction(intent, event);
        }
    }

    private void callAction(String intent, ActionEvent event) {

        switch (intent) {
            case TRIM -> showTrimVideo(event);
            case ROTATE -> showRotateVideo(event);
            case LOAD_VIDEO -> showFileBrowser(event);
            case REPLACE_AUDIO -> showReplaceVideoAudio(event);
            case PREVIEW -> handlePreview(event);
        }
    }

    private void showFileBrowser(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        File file = browseFile(stage);
        if (file != null) {
            currentFile = file;
            Controller.tempFile = file;
            setUpVideoFile();
        }
    }

    private void setUpVideoFile() {
        String currentFileName = Controller.currentFile.getName();
        currentVideoLabel.setText(currentVideoLabel.getText().trim().concat(" ").concat(currentFileName));
        queryBox.clear();
    }


    private void showReplaceVideoAudio(ActionEvent event) {
    }

    private void showTrimVideo(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(AiVideoEditorApp.class.getResource("trimMenu.fxml"));
        StageSwitcher.toPage(fxmlLoader, event, 350, 300, "Trim Video Menu");
    }

    private void showRotateVideo(ActionEvent event) {

    }

    private String getQueryIntent(String query) {
        if(query.equals("l"))
            return LOAD_VIDEO;
        return TRIM;
    }

    @FXML
    public void initialize() {

        Platform.runLater(() -> {
            EventHandler<KeyEvent> keyEventEventHandler = keyEvent -> {
                if (new KeyCodeCombination(P, CONTROL_DOWN).match(keyEvent) ||
                        (keyEvent.getCode().equals(P) && !queryBox.isFocused())) {
                    handlePreview(keyEvent);
                } else if (new KeyCodeCombination(O, CONTROL_DOWN).match(keyEvent)) {
                    handleSelectNewFile();
                }
            };
            container.getScene().addEventHandler(KeyEvent.KEY_PRESSED, keyEventEventHandler);
        });
    }

    private void handleSelectNewFile() {
        Scene scene = container.getScene();
        Stage stage = (Stage) scene.getWindow();
        File file = browseFile(stage);
        if (file != null) {
            currentFile = file;
            Controller.tempFile = file;
            currentVideoLabel.setText(currentVideoLabel.getText().concat(" ").concat(currentFile.getName()));
        }
    }

    private void handlePreview(Event event) {
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
}
