package com.pandalodge.controller;

import com.pandalodge.dao.AccommodationDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.File;
import java.sql.SQLException;

public class AccommodationFormController {
    @FXML
    private TextField titleField;
    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private TextField priceField;
    @FXML
    private ComboBox<String> statusCombo;
    @FXML
    private TextField sizeField;
    @FXML
    private TextField addressField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private CheckBox furnishedCheck;
    @FXML
    private TextField imageUrlField;
    @FXML
    private TextField ownerNameField;
    @FXML
    private TextField ownerEmailField;
    @FXML
    private TextField ownerPhoneField;
    @FXML
    private Button okBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Label statusLabel;

    private Stage stage;
    private boolean saved = false;
    private com.pandalodge.model.Accommodation accommodation; // For editing

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

    public void setAccommodation(com.pandalodge.model.Accommodation acc) {
        this.accommodation = acc;
        if (acc != null) {
            titleField.setText(acc.getType() + " - " + acc.getAddress()); // Title isn't in model but we can put
                                                                          // something
            typeCombo.setValue(acc.getType());
            priceField.setText(String.valueOf(acc.getPrice()));
            sizeField.setText(acc.getSize());
            addressField.setText(acc.getAddress());
            descriptionField.setText(acc.getDescription());
            furnishedCheck.setSelected(acc.isFurnished());
            imageUrlField.setText(acc.getImageUrl());
            ownerNameField.setText(acc.getOwnerName());
            ownerEmailField.setText(acc.getOwnerEmail());
            ownerPhoneField.setText(acc.getOwnerPhone());
            if (statusCombo != null)
                statusCombo.setValue(acc.getStatus());
            if (okBtn != null)
                okBtn.setText("Update Listing");
        }
    }

    private void handleSave() {
        if (validate()) {
            try {
                String type = typeCombo.getValue();
                double price = Double.parseDouble(priceField.getText());
                String size = sizeField.getText();
                String address = addressField.getText();
                String imageUrl = imageUrlField.getText();
                boolean furnished = furnishedCheck.isSelected();
                String description = descriptionField.getText();
                String ownerName = ownerNameField.getText();
                String ownerEmail = ownerEmailField.getText();
                String ownerPhone = ownerPhoneField.getText();

                if (accommodation == null) {
                    // Create new
                    String status = (statusCombo != null && statusCombo.getValue() != null) ? statusCombo.getValue()
                            : "AVAILABLE";
                    AccommodationDAO.create(type, price, address, imageUrl, furnished, description, status,
                            ownerName,
                            ownerEmail, ownerPhone, size);
                } else {
                    // Update existing
                    accommodation.setType(type);
                    accommodation.setPrice(price);
                    accommodation.setAddress(address);
                    accommodation.setImageUrl(imageUrl);
                    accommodation.setSize(size);
                    accommodation.setFurnished(furnished);
                    accommodation.setDescription(description);
                    if (statusCombo != null && statusCombo.getValue() != null)
                        accommodation.setStatus(statusCombo.getValue());
                    accommodation.setOwnerName(ownerName);
                    accommodation.setOwnerEmail(ownerEmail);
                    accommodation.setOwnerPhone(ownerPhone);

                    AccommodationDAO.update(accommodation);
                }

                saved = true;
                if (stage != null)
                    stage.close();
            } catch (NumberFormatException e) {
                statusLabel.setText("Invalid price format");
                statusLabel.setStyle("-fx-text-fill: #ef4444;");
            } catch (SQLException e) {
                statusLabel.setText("Database error: " + e.getMessage());
                statusLabel.setStyle("-fx-text-fill: #ef4444;");
                e.printStackTrace();
            }
        }
    }

    private boolean validate() {
        if (typeCombo.getValue() == null || priceField.getText().isBlank() ||
                addressField.getText().isBlank() || ownerNameField.getText().isBlank()) {
            statusLabel.setText("Please fill in all required fields.");
            statusLabel.setStyle("-fx-text-fill: #ef4444;");
            return false;
        }
        return true;
    }

    public boolean isSaved() {
        return saved;
    }

    public String getType() {
        return typeCombo.getValue();
    }

    public String getPrice() {
        return priceField.getText();
    }

    public void setValues(String type, String price) {
        if (typeCombo != null)
            typeCombo.setValue(type);
        if (priceField != null)
            priceField.setText(price);
    }

    @FXML
    public void onBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Property Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp"));

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            imageUrlField.setText(selectedFile.toURI().toString());
        }
    }
}
