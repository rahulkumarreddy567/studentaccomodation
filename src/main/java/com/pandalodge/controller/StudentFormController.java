package com.pandalodge.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class StudentFormController {
    @FXML public TextField nameField;
    @FXML public TextField emailField;
    @FXML public Button okBtn;
    @FXML public Button cancelBtn;

    private Stage stage;
    private boolean saved = false;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        cancelBtn.setOnAction(e -> { saved = false; if (stage != null) stage.close(); });
        okBtn.setOnAction(e -> { saved = true; if (stage != null) stage.close(); });
    }

    public boolean isSaved() { return saved; }

    public String getName() { return nameField.getText(); }
    public String getEmail() { return emailField.getText(); }

    public void setValues(String name, String email) {
        nameField.setText(name);
        emailField.setText(email);
    }
}











