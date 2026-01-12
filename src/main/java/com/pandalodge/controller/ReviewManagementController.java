package com.pandalodge.controller;

import com.pandalodge.dao.ReviewDAO;
import com.pandalodge.dao.AccommodationDAO;
import com.pandalodge.model.Review;
import com.pandalodge.model.Accommodation;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReviewManagementController {

    @FXML
    private TableView<Review> reviewTable;
    @FXML
    private TableColumn<Review, Integer> idColumn;
    @FXML
    private TableColumn<Review, String> studentColumn;
    @FXML
    private TableColumn<Review, String> accommodationColumn;
    @FXML
    private TableColumn<Review, String> ratingColumn;
    @FXML
    private TableColumn<Review, String> reviewColumn;
    @FXML
    private TableColumn<Review, String> dateColumn;
    @FXML
    private TextField searchField;
    @FXML
    private Label statusLabel;

    private DashboardController dashboardController;
    private ObservableList<Review> masterData = FXCollections.observableArrayList();

    public void setDashboardController(DashboardController dash) {
        this.dashboardController = dash;
    }

    @FXML
    public void initialize() {
        setupTable();
        loadData();
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter(newVal));
        }
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        studentColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getStudentName() != null ? data.getValue().getStudentName()
                        : "Student #" + data.getValue().getStudentId()));

        accommodationColumn.setCellValueFactory(data -> {
            Accommodation a = AccommodationDAO.findById(data.getValue().getAccommodationId());
            return new SimpleStringProperty(a != null ? a.getType() + " - " + a.getAddress() : "Unknown Acc");
        });

        ratingColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStarRating()));
        reviewColumn.setCellValueFactory(new PropertyValueFactory<>("reviewData"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("reviewDate"));

        TableColumn<Review, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");
            private final HBox box = new HBox(8, deleteBtn);
            {
                box.setAlignment(Pos.CENTER);
                deleteBtn.getStyleClass().add("btn-small-danger");
                deleteBtn.setOnAction(e -> {
                    Review r = getTableView().getItems().get(getIndex());
                    deleteReviewConfirm(r);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
        actionsCol.setPrefWidth(100);
        reviewTable.getColumns().add(actionsCol);
    }

    private void loadData() {
        List<Review> list = ReviewDAO.findAll();
        masterData.setAll(list);
        reviewTable.setItems(FXCollections.observableArrayList(list));
    }

    private void deleteReviewConfirm(Review r) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete this reviewpermanently?", ButtonType.YES,
                ButtonType.NO);
        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            if (ReviewDAO.delete(r.getId())) {
                statusLabel.setText("✅ Review deleted successfully.");
                loadData();
            }
        }
    }

    private void applyFilter(String query) {
        if (query == null || query.isBlank()) {
            reviewTable.setItems(masterData);
            return;
        }
        String lc = query.toLowerCase();
        List<Review> filtered = masterData.stream()
                .filter(r -> (r.getStudentName() != null && r.getStudentName().toLowerCase().contains(lc)) ||
                        r.getReviewData().toLowerCase().contains(lc) ||
                        String.valueOf(r.getId()).contains(lc))
                .collect(Collectors.toList());
        reviewTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    public void onBack() {
        if (dashboardController != null) {
            dashboardController.showAdminOverview();
        }
    }
}
