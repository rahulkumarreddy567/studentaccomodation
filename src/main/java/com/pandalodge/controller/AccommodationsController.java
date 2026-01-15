package com.pandalodge.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.List;
import com.pandalodge.model.Accommodation;

public class AccommodationsController {

    @FXML
    private TextField searchField;

    @FXML
    private FlowPane cardsContainer;

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dash) {
        this.dashboardController = dash;
    }

    @FXML
    public void initialize() {
        // loadData(); // Removed to avoid double loading and potential exceptions
    }

    public void loadData(String type, String location) {
        try {
            if (cardsContainer == null)
                return;

            cardsContainer.getChildren().clear();

            // Fetch from DB with filters
            List<Accommodation> accommodations = com.pandalodge.dao.AccommodationDAO.findMatches(type, location);

            for (Accommodation a : accommodations) {
                addCard(a);
            }
        } catch (Exception e) {
            System.err.println("ERROR in loadData: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadData() {
        loadData(null, null);
    }

    private void addCard(Accommodation accommodation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/card.fxml"));
            VBox cardView = loader.load();

            CardController controller = loader.getController();
            controller.setData(accommodation);
            controller.setDashboardController(dashboardController);

            cardsContainer.getChildren().add(cardView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onSearch() {
        String q = searchField.getText();

        loadData(null, q); // Search location by default, or we can improve DAO to search both
    }

    @FXML
    public void onBack() {

        if (dashboardController != null) {
            dashboardController.showHome();
        } else {
            // Fallback: navigate to home page
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/com/pandalodge/view/home.fxml"));
                javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
                javafx.stage.Stage stage = (javafx.stage.Stage) cardsContainer.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Panda - Home");
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/accommodation_form.fxml"));
            javafx.stage.Stage dialog = new javafx.stage.Stage();
            dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialog.setTitle("Add New Accommodation");
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            AccommodationFormController c = loader.getController();
            c.setStage(dialog);
            dialog.setScene(scene);
            dialog.showAndWait();
            if (c.isSaved()) {
                loadData(); // Refresh grid
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
