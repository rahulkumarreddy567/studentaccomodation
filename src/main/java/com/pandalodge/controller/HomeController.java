package com.pandalodge.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {

    @FXML
    private Button loginBtn;

    @FXML
    private Button signupBtn;

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
        if (com.pandalodge.util.UserSession.isLoggedIn()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/dashboard.fxml"));
                javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

                DashboardController dash = loader.getController();
                dash.showProfile();

                Stage stage = (Stage) loginBtn.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Panda - My Profile");
                stage.centerOnScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            onLogin();
        }
    }

    @FXML
    public void onMyBookings() {
        if (com.pandalodge.util.UserSession.isLoggedIn()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/dashboard.fxml"));
                javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

                DashboardController dash = loader.getController();
                dash.showMyBookings();

                Stage stage = (Stage) loginBtn.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Panda - My Bookings");
                stage.centerOnScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            onLogin();
        }
    }

    public void setLoggedIn(boolean loggedIn) {
        if (loggedIn) {
            boolean isAdmin = com.pandalodge.util.UserSession.isAdmin();
            boolean isRenter = com.pandalodge.util.UserSession.isRenter();

            if (myBookingsBtn != null) {
                boolean isStudent = !isAdmin && !isRenter;
                myBookingsBtn.setVisible(isStudent);
                myBookingsBtn.setManaged(isStudent);
                if (isStudent) {
                    myBookingsBtn.setText("My Bookings");
                    myBookingsBtn.setOnAction(e -> onMyBookings());
                } else if (isRenter) {
                    myBookingsBtn.setText("My Properties");
                    myBookingsBtn.setVisible(true);
                    myBookingsBtn.setManaged(true);
                    myBookingsBtn.setOnAction(e -> onDashboard());
                }
            }

            if (!isAdmin && !isRenter) {
                loginBtn.setText("👤 My Profile");
                loginBtn.setOnAction(e -> onProfile());
            } else {
                loginBtn.setText(isAdmin ? "📊 Admin Panel" : "📈 Owner Panel");
                loginBtn.setOnAction(e -> onDashboard());
            }
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
        String type = mapType(typeRaw);

        navigateToDashboard(type, location);
    }

    @FXML
    public void onSearchCity(javafx.scene.input.MouseEvent event) {
        Object source = event.getSource();
        if (source instanceof javafx.scene.layout.VBox) {
            javafx.scene.layout.VBox box = (javafx.scene.layout.VBox) source;
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
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Property Posted!");
                alert.setContentText("Your accommodation has been successfully listed and is now visible to students.");
                alert.showAndWait();
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

            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panda - " + title);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openFAQs() {
        navigateTo("/com/pandalodge/view/faqs.fxml", "FAQs");
    }

    @FXML
    public void onHome() {
    }

    @FXML
    public void onDashboard() {
        if (com.pandalodge.util.UserSession.isLoggedIn()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/dashboard.fxml"));
                Scene scene = new Scene(loader.load());
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

                DashboardController dash = loader.getController();
                if (com.pandalodge.util.UserSession.isAdmin()) {
                    dash.showAdminOverview();
                } else if (com.pandalodge.util.UserSession.isRenter()) {
                    dash.showOwnerDashboard();
                } else {
                    dash.showAccommodations();
                }

                Stage stage = (Stage) loginBtn.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Panda - Dashboard");
                stage.centerOnScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            onLogin();
        }
    }

    @FXML
    public void onContact() {
        navigateTo("/com/pandalodge/view/contact.fxml", "Contact Us");
    }
}
