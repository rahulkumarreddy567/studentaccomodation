package com.pandalodge.controller;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import com.pandalodge.util.UserSession;
import java.io.IOException;

public class DashboardController {
    @FXML
    private Button backToHomeBtn;
    @FXML
    private Button adminStudentsBtn;
    @FXML
    private Button adminAccommodationsBtn;
    @FXML
    private Button adminBookingsBtn;
    @FXML
    private Button studentBookingsBtn;
    @FXML
    private Button profileBtn;
    @FXML
    private Label userRoleLabel;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label welcomeLabel;
    @FXML
    private VBox sidebar;
    @FXML
    private StackPane centerPane;
    @FXML
    private ToggleButton menuToggle;

    @FXML
    public void initialize() {
        if (sidebar != null) {
            sidebar.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.widthProperty().addListener((o, oldW, newW) -> {
                        boolean show = newW.doubleValue() > 720;
                        setSidebarVisible(show, false);
                    });
                }
            });
        }
        if (menuToggle != null) {
            menuToggle.selectedProperty().addListener((obs, oldV, newV) -> {
                boolean show = !newV.booleanValue();
                setSidebarVisible(show, true);
            });
        }

        // Role-based visibility
        boolean isAdmin = UserSession.isAdmin();
        if (adminStudentsBtn != null)
            adminStudentsBtn.setVisible(isAdmin);
        if (adminAccommodationsBtn != null)
            adminAccommodationsBtn.setVisible(isAdmin);
        if (adminBookingsBtn != null) {
            adminBookingsBtn.setVisible(isAdmin);
            adminBookingsBtn.setManaged(isAdmin);
        }
        if (studentBookingsBtn != null) {
            studentBookingsBtn.setVisible(!isAdmin);
            studentBookingsBtn.setManaged(!isAdmin);
        }
        if (profileBtn != null) {
            profileBtn.setVisible(!isAdmin);
            profileBtn.setManaged(!isAdmin);
        }

        // Set welcome message based on user
        if (welcomeLabel != null) {
            if (isAdmin) {
                welcomeLabel.setText("Welcome, Admin");
            } else if (UserSession.getCurrentStudent() != null) {
                welcomeLabel.setText("Welcome, " + UserSession.getCurrentStudent().getName());
            } else {
                welcomeLabel.setText("Welcome");
            }
        }

        // Removed showAccommodations() to allow caller (e.g. HomeController) to set
        // filters first
    }

    @FXML
    public void onLogout() {
        com.pandalodge.util.UserSession.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/login.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            javafx.stage.Stage stage = (javafx.stage.Stage) sidebar.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panda - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/home.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            javafx.stage.Stage stage = (javafx.stage.Stage) sidebar.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panda - Home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/profile.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            javafx.stage.Stage stage = (javafx.stage.Stage) sidebar.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panda - My Profile");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showAccommodations() {
        showAccommodations(null, null);
    }

    public void showAccommodations(String type, String location) {
        System.out.println("DEBUG: showAccommodations(type=" + type + ", loc=" + location + ")");
        try {
            java.net.URL res = getClass().getResource("/com/pandalodge/view/accommodations.fxml");
            System.out.println("DEBUG: Loading FXML from: " + res);
            if (res == null) {
                System.err.println("ERROR: Could not find /view/accommodations.fxml");
                return;
            }
            FXMLLoader loader = new FXMLLoader(res);
            Node node = loader.load();

            AccommodationsController controller = loader.getController();
            controller.setDashboardController(this); // Pass dashboard reference
            controller.loadData(type, location);

            centerPane.getChildren().setAll(node);
            System.out.println("DEBUG: Accommodations view set to centerPane");

            FadeTransition ft = new FadeTransition(Duration.millis(220), node);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        } catch (IOException e) {
            System.err.println("ERROR loading accommodations view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showDetails(com.pandalodge.model.Accommodation accommodation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/details.fxml"));
            Node node = loader.load();
            DetailController controller = loader.getController();
            controller.setDashboardController(this);
            controller.setData(accommodation);

            centerPane.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showMyBookings() {
        loadCenter("/com/pandalodge/view/bookings.fxml");
    }

    private void setSidebarVisible(boolean visible, boolean animate) {

        if (sidebar == null)
            return;
        if (animate) {
            TranslateTransition tt = new TranslateTransition(Duration.millis(260), sidebar);
            if (visible) {
                sidebar.setManaged(true);
                sidebar.setVisible(true);
                tt.setFromX(-sidebar.getWidth());
                tt.setToX(0);
            } else {
                tt.setFromX(0);
                tt.setToX(-sidebar.getWidth());
                tt.setOnFinished(e -> {
                    sidebar.setVisible(false);
                    sidebar.setManaged(false);
                });
            }
            tt.play();
        } else {
            sidebar.setVisible(visible);
            sidebar.setManaged(visible);
        }
    }

    @FXML
    public void showStudents() {
        System.out.println("**** DEBUG: showStudents() called ****");
        loadCenter("/com/pandalodge/view/student.fxml");
    }

    @FXML
    public void showAccommodationManagement() {
        System.out.println("**** DEBUG: showAccommodationManagement() called ****");
        loadCenter("/com/pandalodge/view/accommodation_management.fxml");
    }

    private void loadCenter(String resource) {
        System.out.println("**** DEBUG: loadCenter called with resource: " + resource + " ****");
        System.err.println("**** STDERR DEBUG: loadCenter called ****");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Node node = loader.load();

            Object controller = loader.getController();
            System.out.println("**** DEBUG: Controller loaded: "
                    + (controller != null ? controller.getClass().getName() : "null") + " ****");

            if (controller instanceof StudentController) {
                System.out.println("**** DEBUG: Setting dashboardController on StudentController ****");
                ((StudentController) controller).setDashboardController(this);
            } else if (controller instanceof AccommodationManagementController) {
                System.out.println("**** DEBUG: Setting dashboardController on AccommodationManagementController ****");
                ((AccommodationManagementController) controller).setDashboardController(this);
            } else if (controller instanceof BookingController) {
                System.out.println("**** DEBUG: Setting dashboardController on BookingController ****");
                ((BookingController) controller).setDashboardController(this);
            }

            centerPane.getChildren().setAll(node);
            FadeTransition ft = new FadeTransition(Duration.millis(220), node);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
