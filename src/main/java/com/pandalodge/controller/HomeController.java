package com.pandalodge.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {

    @FXML
    private Button loginBtn;

    @FXML
    private Button signupBtn;

    @FXML
    private Button profileBtn;

    @FXML
    private Button myBookingsBtn;

    @FXML
    private javafx.scene.layout.HBox featuredContainer;

    @FXML
    public void initialize() {
        if (com.pandalodge.util.UserSession.isLoggedIn() && !com.pandalodge.util.UserSession.isAdmin()) {
            setLoggedIn(true);
        }
        loadFeaturedAccommodations();
    }

    private void loadFeaturedAccommodations() {
        if (featuredContainer == null)
            return;

        featuredContainer.getChildren().clear();
        java.util.List<com.pandalodge.model.Accommodation> featured = com.pandalodge.dao.AccommodationDAO
                .findFeatured(4);

        for (com.pandalodge.model.Accommodation acc : featured) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/card.fxml"));
                javafx.scene.Parent card = loader.load();
                CardController controller = loader.getController();
                controller.setData(acc);
                controller.setCustomAction(() -> navigateToDashboard(acc.getType(), acc.getAddress()));
                featuredContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void onLogin() {
        navigateTo("/com/pandalodge/view/login.fxml", "Login");
    }

    @FXML
    public void onSignup() {
        navigateTo("/com/pandalodge/view/signup.fxml", "Sign Up");
    }

    @FXML
    public void onProfile() {
        navigateTo("/com/pandalodge/view/profile.fxml", "My Profile");
    }

    @FXML
    public void onMyBookings() {
        navigateTo("/com/pandalodge/view/profile.fxml", "My Bookings");
    }

    public void setLoggedIn(boolean loggedIn) {
        if (loggedIn) {
            // Show My Bookings button
            if (myBookingsBtn != null) {
                myBookingsBtn.setVisible(true);
                myBookingsBtn.setManaged(true);
            }

            loginBtn.setText("👤 My Profile");
            loginBtn.setOnAction(e -> onProfile());
            loginBtn.getStyleClass().add("btn-profile");
            signupBtn.setText("Logout");
            signupBtn.setOnAction(e -> handleLogout());
            signupBtn.getStyleClass().add("btn-header-danger");
        }
    }

    private void handleLogout() {
        com.pandalodge.util.UserSession.logout();
        navigateTo("/com/pandalodge/view/login.fxml", "Login");
    }

    @FXML
    private javafx.scene.control.TextField locationField;
    @FXML
    private javafx.scene.control.ComboBox<String> typeCombo;

    @FXML
    public void onSearch() {
        String location = locationField.getText();
        String typeRaw = typeCombo.getValue();
        String type = mapType(typeRaw); // Handle plural -> singular

        System.out.println("Searching for: " + type + " (raw: " + typeRaw + ") in " + location);

        navigateToDashboard(type, location);
    }

    @FXML
    public void onSearchCity(javafx.scene.input.MouseEvent event) {
        Object source = event.getSource();
        if (source instanceof javafx.scene.layout.VBox) {
            javafx.scene.layout.VBox box = (javafx.scene.layout.VBox) source;
            // Look for a label that contains the city name
            for (javafx.scene.Node node : box.getChildren()) {
                if (node instanceof javafx.scene.control.Label) {
                    javafx.scene.control.Label lbl = (javafx.scene.control.Label) node;
                    if (lbl.getStyleClass().contains("city-name-modern")) {
                        navigateToDashboard(null, lbl.getText());
                        return;
                    }
                }
            }
        }
    }

    @FXML
    public void onSearchCity(javafx.event.ActionEvent event) {
        if (event.getSource() instanceof Button) {
            String city = ((Button) event.getSource()).getText();
            navigateToDashboard(null, city);
        }
    }

    @FXML
    public void onViewRooms() {
        navigateToDashboard("Room", null);
    }

    @FXML
    public void onViewStudios() {
        navigateToDashboard("Studio", null);
    }

    @FXML
    public void onViewApartments() {
        navigateToDashboard("Apartment", null);
    }

    @FXML
    public void onListProperty() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/accommodation_form.fxml"));
            javafx.scene.Parent root = loader.load();
            AccommodationFormController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Post Your Accommodation");
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialogStage.initOwner(loginBtn.getScene().getWindow());

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            dialogStage.setScene(scene);

            controller.setStage(dialogStage);
            dialogStage.showAndWait();

            if (controller.isSaved()) {
                // Show success message or refresh
                System.out.println("Property listed successfully!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String mapType(String type) {
        if (type == null)
            return null;
        if (type.equalsIgnoreCase("Rooms"))
            return "Room";
        if (type.equalsIgnoreCase("Studios"))
            return "Studio";
        if (type.equalsIgnoreCase("Apartments"))
            return "Apartment";
        return type;
    }

    private void navigateToDashboard(String type, String location) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/dashboard.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            DashboardController dashboard = loader.getController();
            dashboard.showAccommodations(type, location);

            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panda - Search Results");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateTo(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            // Get current stage
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panda - " + title);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
