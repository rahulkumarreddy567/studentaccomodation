package com.pandalodge.controller;

import com.pandalodge.dao.StudentDAO;
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
import com.pandalodge.model.Student;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StudentController {
    @FXML
    private TableView<Student> studentTable;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField searchField;
    @FXML
    private Label statusLabel;

    private DashboardController dashboardController;
    private AdminController adminController;

    public void setDashboardController(DashboardController dash) {
        this.dashboardController = dash;
        System.out.println("DEBUG StudentController: dashboardController set = " + (dash != null));
    }

    public void setAdminController(AdminController admin) {
        this.adminController = admin;
        System.out.println("DEBUG StudentController: adminController set = " + (admin != null));
    }

    @FXML
    public void onBack() {
        System.out.println("DEBUG StudentController.onBack() called");
        if (dashboardController != null) {
            dashboardController.showAccommodations();
        } else if (adminController != null) {
            // Navigate back to admin dashboard
            javafx.stage.Stage stage = (javafx.stage.Stage) studentTable.getScene().getWindow();
            adminController.goToAdmin(stage);
        } else {
            // Fallback: navigate to home page if no controller is set
            System.err.println("WARN: No parent controller set in StudentController.onBack(), navigating to home");
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/pandalodge/view/home.fxml"));
                javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
                javafx.stage.Stage stage = (javafx.stage.Stage) studentTable.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Panda - Home");
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }

    private ObservableList<Student> students = FXCollections.observableArrayList();

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        // setup columns if not defined in FXML
        if (studentTable.getColumns().isEmpty()) {
            TableColumn<Student, Integer> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            idCol.setPrefWidth(60);

            TableColumn<Student, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            nameCol.setPrefWidth(200);

            TableColumn<Student, String> emailCol = new TableColumn<>("Email");
            emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
            emailCol.setPrefWidth(240);

            TableColumn<Student, Void> actionsCol = new TableColumn<>("Actions");
            actionsCol.setCellFactory(col -> new TableCell<>() {
                private final Button editBtn = new Button("Edit");
                private final Button delBtn = new Button("Delete");
                private final HBox box = new HBox(6, editBtn, delBtn);
                {
                    editBtn.setOnAction(e -> {
                        Student s = getTableView().getItems().get(getIndex());
                        showStudentForm(s);
                    });
                    delBtn.setOnAction(e -> {
                        Student s = getTableView().getItems().get(getIndex());
                        deleteStudentConfirm(s);
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

            // noinspection unchecked
            studentTable.getColumns().addAll(idCol, nameCol, emailCol, actionsCol);
        }

        refresh();
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldV, newV) -> applyFilter(newV));
        }
    }

    private void showStudentForm(Student s) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/student_form.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(s == null ? "Add student" : "Edit student");
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            StudentFormController c = loader.getController();
            c.setStage(dialog);
            if (s != null)
                c.setValues(s.getName(), s.getEmail());
            dialog.setScene(scene);
            dialog.showAndWait();
            if (c.isSaved()) {
                String name = c.getName();
                String email = c.getEmail();
                if (s == null) {
                    try {
                        Student created = StudentDAO.create(name, email);
                        statusLabel.setText(created != null ? "Added" : "Failed");
                    } catch (SQLException ex) {
                        if (ex.getMessage().toLowerCase().contains("unique")
                                || ex.getMessage().toLowerCase().contains("constraint")) {
                            statusLabel.setText("Email already exists");
                        } else {
                            statusLabel.setText("DB error: " + ex.getMessage());
                        }
                    }
                } else {
                    try {
                        boolean ok = StudentDAO.update(s.getId(), name, email);
                        statusLabel.setText(ok ? "Updated" : "Update failed");
                    } catch (SQLException ex) {
                        if (ex.getMessage().toLowerCase().contains("unique")
                                || ex.getMessage().toLowerCase().contains("constraint")) {
                            statusLabel.setText("Email already exists");
                        } else {
                            statusLabel.setText("DB error: " + ex.getMessage());
                        }
                    }
                }
                refresh();
            }
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to open form");
        }
    }

    private void deleteStudentConfirm(Student s) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Delete student " + s.getName() + "?", ButtonType.YES,
                ButtonType.NO);
        Optional<ButtonType> res = a.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            boolean ok = StudentDAO.delete(s.getId());
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
            studentTable.getItems().setAll(students);
            return;
        }
        String lc = q.toLowerCase();
        List<Student> filtered = students.stream()
                .filter(s -> s.getName().toLowerCase().contains(lc) || s.getEmail().toLowerCase().contains(lc))
                .collect(Collectors.toList());
        studentTable.getItems().setAll(filtered);
    }

    public void refresh() {
        List<Student> list = StudentDAO.findAll();
        students.setAll(list);
        studentTable.getItems().setAll(list);
    }

    @FXML
    public void addStudent() {
        showStudentForm(null);
    }
}










