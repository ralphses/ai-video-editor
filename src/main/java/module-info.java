module com.clicks.aivideoeditor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.net.http;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    opens com.clicks.aivideoeditor to javafx.fxml;
    opens com.clicks.aivideoeditor.controller to javafx.fxml;
    exports com.clicks.aivideoeditor;
    exports com.clicks.aivideoeditor.controller;
}