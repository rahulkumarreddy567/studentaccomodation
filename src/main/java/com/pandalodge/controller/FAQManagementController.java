package com.pandalodge.controller;

import com.pandalodge.dao.FAQDAO;
import com.pandalodge.model.FAQ;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FAQManagementController {

    @FXML
    private TableView<FAQ> faqTable;
    @FXML
    private TableColumn<FAQ, Integer> idColumn;
    @FXML
    private TableColumn<FAQ, String> questionColumn;
    @FXML
    private TableColumn<FAQ, String> categoryColumn;
    @FXML
    private TextField searchField;
    @FXML
    private Label statusLabel;

    private DashboardController dashboardController;
    private ObservableList<FAQ> masterData = FXCollections.observableArrayList();

    public void setDashboardController(DashboardController dash) {
        this.dashboardController = dash;
    }

    @FXML
    public void initialize() {
        setupTable();
        loadData();
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldV, newV) -> applyFilter(newV));
        }
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        questionColumn.setCellValueFactory(new PropertyValueFactory<>("question"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<FAQ, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox box = new HBox(8, editBtn, deleteBtn);
            {
                box.setAlignment(Pos.CENTER);
                editBtn.getStyleClass().add("btn-small");
                deleteBtn.getStyleClass().add("btn-small-danger");

                editBtn.setOnAction(e -> showFAQForm(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> deleteFAQConfirm(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
        actionsCol.setPrefWidth(150);
        faqTable.getColumns().add(actionsCol);
    }

    private void loadData() {
        List<FAQ> list = FAQDAO.findAll();
        masterData.setAll(list);
        faqTable.setItems(FXCollections.observableArrayList(list));
    }

    private void deleteFAQConfirm(FAQ f) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete this FAQ?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            if (FAQDAO.delete(f.getId())) {
                statusLabel.setText("✅ FAQ deleted.");
                loadData();
            }
        }
    }

    private void showFAQForm(FAQ f) {
        Dialog<FAQ> dialog = new Dialog<>();
        dialog.setTitle(f == null ? "Add FAQ" : "Edit FAQ");
        dialog.setHeaderText(f == null ? "Create a new frequently asked question" : "Modify existing FAQ");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField question = new TextField();
        question.setPromptText("Question");
        TextArea answer = new TextArea();
        answer.setPromptText("Answer");
        TextField category = new TextField();
        category.setPromptText("Category (GENERAL, PAYMENT, etc.)");

        if (f != null) {
            question.setText(f.getQuestion());
            answer.setText(f.getAnswer());
            category.setText(f.getCategory());
        }

        grid.add(new Label("Question:"), 0, 0);
        grid.add(question, 1, 0);
        grid.add(new Label("Answer:"), 0, 1);
        grid.add(answer, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(category, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    if (f == null) {
                        return FAQDAO.create(question.getText(), answer.getText(), category.getText());
                    } else {
                        FAQDAO.update(f.getId(), question.getText(), answer.getText(), category.getText());
                        return f;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });

        Optional<FAQ> result = dialog.showAndWait();
        result.ifPresent(faq -> {
            statusLabel.setText("✅ FAQ saved.");
            loadData();
        });
    }

    @FXML
    public void onAddFAQ() {
        showFAQForm(null);
    }

    private void applyFilter(String q) {
        if (q == null || q.isBlank()) {
            faqTable.setItems(masterData);
            return;
        }
        String lc = q.toLowerCase();
        List<FAQ> filtered = masterData.stream()
                .filter(f -> f.getQuestion().toLowerCase().contains(lc) || f.getCategory().toLowerCase().contains(lc))
                .collect(Collectors.toList());
        faqTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    public void onBack() {
        if (dashboardController != null) {
            dashboardController.showAdminOverview();
        }
    }
}
