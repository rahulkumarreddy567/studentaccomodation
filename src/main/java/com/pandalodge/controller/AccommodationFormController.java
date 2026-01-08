package com.pandalodge.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AccommodationFormController {
    @FXML
    public TextField typeField;
    @FXML
    public TextField priceField;
    @FXML
    public Button okBtn;
    @FXML
    public Button cancelBtn;

    private Stage stage;
    private boolean saved = false;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        cancelBtn.setOnAction(e -> {
            saved = false;
            if (stage != null)
                stage.close();
        });
        okBtn.setOnAction(e -> {
            saved = true;
            if (stage != null)
                stage.close();
        });
    }

    public boolean isSaved() {
        return saved;
    }

    public String getType() {
        return typeField.getText();
    }

    public String getPrice() {
        return priceField.getText();
    }

    public void setValues(String type, String price) {
        typeField.setText(type);
        priceField.setText(price);
    }
}










