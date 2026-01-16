package com.pandalodge.controller;

import com.pandalodge.dao.AccommodationDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.pandalodge.model.Accommodation;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AccommodationManagementController {
    @FXML
    private TableView<Accommodation> accommodationTable;
    @FXML
    private TextField typeField;
    @FXML
    private TextField rentField;
    @FXML
    private TextField searchField;
    @FXML
    private Label statusLabel;

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dash) {
        this.dashboardController = dash;

    }

    @FXML
    public void onBack() {

        if (dashboardController != null) {
            dashboardController.showAdminOverview();
        } else {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/com/pandalodge/view/home.fxml"));
                javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
                javafx.stage.Stage stage = (javafx.stage.Stage) accommodationTable.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Panda - Home");
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }

    private ObservableList<Accommodation> rooms = FXCollections.observableArrayList();

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        accommodationTable.getColumns().clear();

        TableColumn<Accommodation, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Accommodation, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(100);

        TableColumn<Accommodation, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        addressCol.setPrefWidth(220);

        TableColumn<Accommodation, Double> rentCol = new TableColumn<>("Rent (€)");
        rentCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        rentCol.setPrefWidth(90);
        rentCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("€" + String.format("%.0f", item));
                }
            }
        });

        TableColumn<Accommodation, String> sizeCol = new TableColumn<>("Size");
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));
        sizeCol.setPrefWidth(80);

        TableColumn<Accommodation, Boolean> furnishedCol = new TableColumn<>("Furnished");
        furnishedCol.setCellValueFactory(new PropertyValueFactory<>("furnished"));
        furnishedCol.setPrefWidth(90);
        furnishedCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Yes" : "No");
                }
            }
        });

        TableColumn<Accommodation, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("AVAILABLE".equals(item)) {
                        setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold;");
                    } else if ("BOOKED".equals(item)) {
                        setStyle("-fx-text-fill: #6366f1; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    }
                }
            }
        });

        TableColumn<Accommodation, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button delBtn = new Button("Delete");
            private final HBox box = new HBox(6, editBtn, delBtn);
            {
                box.setAlignment(javafx.geometry.Pos.CENTER);
                editBtn.getStyleClass().add("btn-small");
                delBtn.getStyleClass().add("btn-small-danger");
                editBtn.setOnAction(e -> {
                    Accommodation a = getTableView().getItems().get(getIndex());
                    showRoomForm(a);
                });
                delBtn.setOnAction(e -> {
                    Accommodation a = getTableView().getItems().get(getIndex());
                    deleteRoomConfirm(a);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setGraphic(null);
                else
                    setGraphic(box);
            }
        });
        actionsCol.setPrefWidth(140);

        accommodationTable.getColumns().addAll(idCol, typeCol, addressCol, rentCol, sizeCol, furnishedCol, statusCol,
                actionsCol);

        refresh();
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldV, newV) -> applyFilter(newV));
        }
    }

    private void showRoomForm(Accommodation a) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/accommodation_form.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(a == null ? "Add New Accommodation" : "Edit Accommodation");

            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            AccommodationFormController c = loader.getController();
            c.setStage(dialog);
            c.setAccommodation(a);

            dialog.setScene(scene);
            dialog.showAndWait();

            if (c.isSaved()) {
                statusLabel.setText(
                        a == null ? "Accommodation added successfully." : "Accommodation updated successfully.");
                refresh();
            }
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to open form: " + e.getMessage());
        }
    }

    private void deleteRoomConfirm(Accommodation a) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete accommodation " + a.getId() + "?", ButtonType.YES,
                ButtonType.NO);
        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            boolean ok = AccommodationDAO.delete(a.getId());
            if (ok) {
                statusLabel.setText("Deleted");
                refresh();
            } else {
                statusLabel.setText("Delete failed");
            }
        }
    }

    private void applyFilter(String q) {
        if (q == null || q.isBlank()) {
            accommodationTable.getItems().setAll(rooms);
            return;
        }
        String lc = q.toLowerCase();
        List<Accommodation> filtered = rooms.stream()
                .filter(a -> a.getType().toLowerCase().contains(lc)
                        || String.valueOf(a.getId()).contains(lc)
                        || (a.getAddress() != null && a.getAddress().toLowerCase().contains(lc))
                        || (a.getStatus() != null && a.getStatus().toLowerCase().contains(lc))
                        || (a.getSize() != null && a.getSize().toLowerCase().contains(lc)))
                .collect(Collectors.toList());
        accommodationTable.getItems().setAll(filtered);
    }

    public void refresh() {
        List<Accommodation> list = AccommodationDAO.findAll();
        rooms.setAll(list);
        accommodationTable.getItems().setAll(list);
    }

    @FXML
    public void onAddAccommodation() {
        showRoomForm(null);
    }
}
