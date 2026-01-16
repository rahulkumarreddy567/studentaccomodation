package com.pandalodge.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class ContactController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField subjectField;
    @FXML
    private TextArea messageArea;
    @FXML
    private Label statusLabel;

    @FXML
    public void onHome() {
        navigateTo("/com/pandalodge/view/home.fxml", "Home");
    }

    @FXML
    public void onViewRooms() {
        navigateToDashboard(null, null);
    }

    @FXML
    public void onDashboard() {
        navigateTo("/com/pandalodge/view/dashboard.fxml", "Dashboard");
    }

    @FXML
    public void openFAQs() {
        navigateTo("/com/pandalodge/view/faqs.fxml", "FAQs");
    }

    @FXML
    public void onContact() {
    }

    @FXML
    public void handleSendMessage() {
        String name = nameField.getText();
        String email = emailField.getText();
        String subject = subjectField.getText();
        String message = messageArea.getText();

        if (name.isEmpty() || email.isEmpty() || subject.isEmpty() || message.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            statusLabel.setStyle("-fx-text-fill: #ef4444;");
            statusLabel.setVisible(true);
            return;
        }

        statusLabel.setText("✅ Message sent successfully! We'll be in touch soon.");
        statusLabel.setStyle("-fx-text-fill: #10b981;");
        statusLabel.setVisible(true);

        nameField.clear();
        emailField.clear();
        subjectField.clear();
        messageArea.clear();
    }

    private void navigateTo(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panda - " + title);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToDashboard(String type, String location) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/dashboard.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            DashboardController dashboard = loader.getController();
            dashboard.showAccommodations(type, location);
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panda - Dashboard");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
