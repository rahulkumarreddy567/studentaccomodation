package com.pandalodge.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StudentFormController {
    @FXML
    public TextField nameField;
    @FXML
    public TextField emailField;
    @FXML
    public Button okBtn;
    @FXML
    public Button cancelBtn;
    @FXML
    private PasswordField passwordField;
    @FXML
    private VBox passwordBox;

    @FXML
    private Label statusLabel;

    private Stage stage;
    private boolean saved = false;
    private com.pandalodge.model.Student student;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        if (cancelBtn != null) {
            cancelBtn.setOnAction(e -> {
                saved = false;
                if (stage != null)
                    stage.close();
            });
        }
        if (okBtn != null) {
            okBtn.setOnAction(e -> handleSave());
        }
    }

    public void setStudent(com.pandalodge.model.Student s) {
        this.student = s;
        if (s != null) {
            nameField.setText(s.getName());
            emailField.setText(s.getEmail());
            if (okBtn != null)
                okBtn.setText("Update Student");
            if (passwordBox != null) {
                passwordBox.setVisible(false);
                passwordBox.setManaged(false);
            }
        }
    }

    private void handleSave() {
        String name = nameField.getText();
        String email = emailField.getText();

        if (name.isBlank() || email.isBlank()) {
            if (statusLabel != null) {
                statusLabel.setText("Please fill in all fields.");
                statusLabel.setStyle("-fx-text-fill: #ef4444;");
            }
            return;
        }

        try {
            if (student == null) {
                String pwd = (passwordField != null && !passwordField.getText().isBlank())
                        ? passwordField.getText()
                        : "password123";
                com.pandalodge.dao.StudentDAO.create(name, email, pwd);
            } else {
                com.pandalodge.dao.StudentDAO.update(student.getId(), name, email);
            }
            saved = true;
            if (stage != null)
                stage.close();
        } catch (java.sql.SQLException ex) {
            if (statusLabel != null) {
                statusLabel.setText("Error: " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: #ef4444;");
            }
            ex.printStackTrace();
        }
    }

    public boolean isSaved() {
        return saved;
    }

    public String getName() {
        return nameField.getText();
    }

    public String getEmail() {
        return emailField.getText();
    }

    public void setValues(String name, String email) {
        nameField.setText(name);
        emailField.setText(email);
    }
}
