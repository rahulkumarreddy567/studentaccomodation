package com.pandalodge.controller;

import com.pandalodge.dao.StudentDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.pandalodge.model.Student;

import java.io.IOException;
import java.sql.SQLException;

public class SignupController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label statusLabel;

    @FXML
    public void handleSignup() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        if (name == null || name.isBlank() || email == null || email.isBlank()) {
            statusLabel.setText("Fill all fields");
            return;
        }
        try {
            Student s = StudentDAO.create(name, email, password);
            if (s != null) {
                com.pandalodge.util.UserSession.login(s);
                redirectToHome();
            } else {
                statusLabel.setText("Failed to create account (unknown error)");
            }
        } catch (SQLException ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "Database error";
            if (msg.toLowerCase().contains("unique") || msg.toLowerCase().contains("constraint")) {
                statusLabel.setText("Email already exists");
            } else {
                statusLabel.setText("DB error: " + msg);
            }
        }
    }

    private void redirectToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/home.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            HomeController home = loader.getController();
            home.setLoggedIn(true);

            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panda - Home");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void back() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/home.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panda - Home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void backToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panda - Login");
        } catch (IOException e) {
            statusLabel.setText("Failed to load login: " + e.getMessage());
        }
    }
}










