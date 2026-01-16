package com.pandalodge.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import com.pandalodge.model.Accommodation;

public class CardController {
    @FXML
    private VBox cardBox;
    @FXML
    private ImageView image;
    @FXML
    private Label price;
    @FXML
    private Label title;
    @FXML
    private Label address;
    @FXML
    private Label typeBadge;
    @FXML
    private Label statusBadge;
    @FXML
    private Label sizeLabel;
    @FXML
    private Label furnishedLabel;
    @FXML
    private Label description;

    private Accommodation accommodation;
    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dash) {
        this.dashboardController = dash;
    }

    public void setData(Accommodation accommodation) {
        this.accommodation = accommodation;

        price.setText("€" + String.format("%.0f", accommodation.getPrice()) + "/mo");

        String accommodationTitle = accommodation.getType();
        if (accommodation.getAddress() != null && accommodation.getAddress().contains(",")) {
            String[] parts = accommodation.getAddress().split(",");
            if (parts.length >= 2) {
                accommodationTitle = accommodation.getType() + " in " + parts[1].trim();
            }
        }
        title.setText(accommodationTitle);

        address.setText(accommodation.getAddress() != null ? accommodation.getAddress() : "Location not specified");

        if (typeBadge != null) {
            typeBadge.setText(accommodation.getType());
            String badgeColor = switch (accommodation.getType()) {
                case "Room" -> "#22c55e";
                case "Studio" -> "#6366f1";
                case "Apartment" -> "#f59e0b";
                default -> "#64748b";
            };
            typeBadge.setStyle("-fx-background-color: " + badgeColor
                    + "; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");
        }

        if (statusBadge != null) {
            String status = accommodation.getStatus() != null ? accommodation.getStatus() : "AVAILABLE";
            boolean isAvailable = "AVAILABLE".equalsIgnoreCase(status);
            statusBadge.setText(isAvailable ? "Available" : "Booked");
            statusBadge.setStyle("-fx-background-color: " + (isAvailable ? "#22c55e" : "#ef4444") +
                    "; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");
        }

        if (furnishedLabel != null) {
            furnishedLabel.setText(accommodation.isFurnished() ? "• Furnished" : "• Unfurnished");
        }

        if (sizeLabel != null) {
            sizeLabel.setText("• " + (accommodation.getSize() != null ? accommodation.getSize() : "15m²"));
        }

        if (description != null) {
            String desc = accommodation.getDescription();
            if (desc != null && desc.length() > 80) {
                desc = desc.substring(0, 77) + "...";
            }
            description.setText(desc != null ? desc : "No description available");
            description.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        }

        if (accommodation.getImageUrl() != null && !accommodation.getImageUrl().isEmpty()) {
            try {
                image.setImage(new Image(accommodation.getImageUrl(), 300, 180, false, true, true));
            } catch (Exception e) {
                image.setStyle("-fx-background-color: #e2e8f0;");
            }
        }
    }

    private Runnable customAction;

    public void setCustomAction(Runnable action) {
        this.customAction = action;
    }

    @FXML
    private void onView() {
        if (customAction != null) {
            customAction.run();
        } else if (dashboardController != null) {
            dashboardController.showDetails(accommodation);
        }
    }
}
