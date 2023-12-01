package com.clicks.aivideoeditor.controller;

import com.clicks.aivideoeditor.AiVideoEditorApp;
import com.clicks.aivideoeditor.utils.StageSwitcher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javafx.scene.input.KeyCode.O;
import static javafx.scene.input.KeyCode.P;
import static javafx.scene.input.KeyCombination.CONTROL_DOWN;

public class MainController extends Controller {

    public static final String CONFIG_FILE = "config.properties";
    public Button submitBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private ProgressIndicator progress;
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

    public void showLoader() {
        progress.setVisible(true);
    }

    public void hideLoader() {
        progress.setVisible(false);
    }

    @FXML
    void submit_on(ActionEvent event) {


        String query = queryBox.getText().trim();

        if (!query.isEmpty()) {

            showLoader();

            //Todo: process query to get intent
            String intent = getQueryIntent(query);

            //Todo: call intent function pass query
            callAction(intent, event);

            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(e -> {
                // Your logic after the actions are complete...
                hideLoader();
            });
            delay.play();


        }
    }

    @FXML
    void submit(ActionEvent event) {
        String query = queryBox.getText().trim();

        if (!query.isEmpty()) {
            showLoader();

            // Create a background task to process the query and get the intent
            Task<String> backgroundTask = new Task<>() {
                @Override
                protected String call() throws Exception {
                    // Simulate processing query to get intent (replace this with your actual logic)
                    Thread.sleep(1000); // Simulate work
                    return getQueryIntent(query);
                }
            };

            // Bind the ProgressIndicator's progress property to the task's progress
            progress.progressProperty().bind(backgroundTask.progressProperty());

            // Set actions to be performed after the background task completes
            backgroundTask.setOnSucceeded(event1 -> {
                // Get the intent from the completed task
                String intent = backgroundTask.getValue();

                // Call the intent function passing the query
                callAction(intent, event);

                // Simulate a delay (replace this with your actual logic)
                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(e -> {
                    // Your logic after the actions are complete...
                    hideLoader();
                });
                delay.play();
            });

            // Start the background task in a new thread
            Thread taskThread = new Thread(backgroundTask);
            taskThread.start();
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

        AtomicReference<String> intent = new AtomicReference<>("");

        Properties configProperties = loadConfigProperties();
        String witAccessToken = configProperties.getProperty("wit.accessToken");
        String url = configProperties.getProperty("wit.url");

        HttpClient httpClient = HttpClient.newHttpClient();
        URI witApiUri = URI.create(url + URLEncoder.encode(query, UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(witApiUri)
                .header("Authorization", "Bearer " + witAccessToken)
                .build();

        CompletableFuture<HttpResponse<String>> responseFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        // Handle the response when it is available
        responseFuture.thenAccept(response -> {
            if (response.statusCode() == 200) {
                // Parse the response body to extract the intent
                String responseBody = response.body();
                intent.set(extractIntentFromResponse(responseBody));
            } else {
                System.err.println("Error: " + response.statusCode() + " - " + response.body());
            }
        });

        // Keep the program running to wait for the asynchronous response
        try {
            Thread.sleep(5000); // Adjust the time as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("intent = " + intent.get());
        return intent.get();
    }

    private String extractIntentFromResponse(String responseBody) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // Check if "intents" array is present
            if (jsonNode.has("intents") && jsonNode.get("intents").isArray()) {
                JsonNode intentsArray = jsonNode.get("intents");

                // Check if there's at least one intent
                if (intentsArray.size() > 0) {
                    JsonNode firstIntent = intentsArray.get(0);

                    // Check if "name" field is present in the first intent
                    if (firstIntent.has("name")) {
                        return firstIntent.get("name").asText();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return a default value or handle the case where extraction fails
        return "Intent extraction failed";
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

    private Properties loadConfigProperties() {
        Properties properties = new Properties();
        try (InputStream input = AiVideoEditorApp.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            } else {
                System.err.println("Unable to find " + CONFIG_FILE);
            }
        } catch (IOException e) {
            System.err.println("Error loading " + CONFIG_FILE + ": " + e.getMessage());
        }
        return properties;
    }


}
