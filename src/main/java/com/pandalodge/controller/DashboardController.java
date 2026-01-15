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
    private Button homeBtn;
    @FXML
    private Button browseBtn;
    @FXML
    private Button adminOverviewBtn;
    @FXML
    private Button adminStudentsBtn;
    @FXML
    private Button adminAccommodationsBtn;
    @FXML
    private Button adminBookingsBtn;
    @FXML
    private Button adminReviewsBtn;
    @FXML
    private Button adminFaqsBtn;
    @FXML
    private Button studentBookingsBtn;
    @FXML
    private Button profileBtn;
    @FXML
    private VBox adminMenu;
    @FXML
    private Label welcomeLabel;
    @FXML
    private VBox sidebar;
    @FXML
    private VBox ownerMenu;
    @FXML
    private Button ownerDashboardBtn;
    @FXML
    private Button ownerAccommodationsBtn;
    @FXML
    private Button ownerBookingsBtn;
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
        boolean isRenter = UserSession.isRenter();

        if (adminMenu != null) {
            adminMenu.setVisible(isAdmin);
            adminMenu.setManaged(isAdmin);
        }
        if (ownerMenu != null) {
            ownerMenu.setVisible(isRenter);
            ownerMenu.setManaged(isRenter);
        }
        if (studentBookingsBtn != null) {
            studentBookingsBtn.setVisible(!isAdmin && !isRenter);
            studentBookingsBtn.setManaged(!isAdmin && !isRenter);
        }
        if (profileBtn != null) {
            profileBtn.setVisible(!isAdmin);
            profileBtn.setManaged(!isAdmin);
        }

        // Welcome message logic remains
        if (welcomeLabel != null) {
            if (isAdmin) {
                welcomeLabel.setText("Welcome, Admin");
            } else if (UserSession.isRenter()) {
                welcomeLabel.setText("Welcome, " + UserSession.getCurrentRenter().getName());
            } else if (UserSession.getCurrentStudent() != null) {
                welcomeLabel.setText("Welcome, " + UserSession.getCurrentStudent().getName());
            }
        }
    }

    public void showAdminOverview() {
        setActive(adminOverviewBtn);
        loadCenter("/com/pandalodge/view/admin_overview.fxml");
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
        setActive(profileBtn);
        // Load into center pane instead of replacing stage
        loadCenter("/com/pandalodge/view/profile.fxml");
    }

    @FXML
    public void showAccommodations() {
        setActive(browseBtn);
        showAccommodations(null, null);
    }

    public void showAccommodations(String type, String location) {

        try {
            java.net.URL res = getClass().getResource("/com/pandalodge/view/accommodations.fxml");

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

    private void setActive(Button activeBtn) {
        Button[] btns = { homeBtn, browseBtn, adminOverviewBtn, adminStudentsBtn, adminAccommodationsBtn,
                adminBookingsBtn, adminReviewsBtn, adminFaqsBtn, studentBookingsBtn, profileBtn,
                ownerDashboardBtn, ownerAccommodationsBtn, ownerBookingsBtn };
        for (Button b : btns) {
            if (b != null) {
                b.getStyleClass().removeAll("sidebar-item-active");
                if (b == activeBtn) {
                    b.getStyleClass().add("sidebar-item-active");
                }
            }
        }
    }

    @FXML
    public void showMyBookings() {
        setActive(studentBookingsBtn);
        loadCenter("/com/pandalodge/view/bookings.fxml");
    }

    @FXML
    public void showAdminBookings() {
        setActive(adminBookingsBtn);
        loadCenter("/com/pandalodge/view/bookings.fxml");
    }

    @FXML
    public void showReviewsManagement() {
        setActive(adminReviewsBtn);
        loadCenter("/com/pandalodge/view/reviews_management.fxml");
    }

    @FXML
    public void showFaqsManagement() {
        setActive(adminFaqsBtn);
        loadCenter("/com/pandalodge/view/faqs_management.fxml");
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
    public void showOwnerDashboard() {
        setActive(ownerDashboardBtn);
        // Fallback or specific owner overview FXML
        loadCenter("/com/pandalodge/view/admin_overview.fxml");
    }

    @FXML
    public void showOwnerAccommodations() {
        setActive(ownerAccommodationsBtn);
        loadCenter("/com/pandalodge/view/accommodation_management.fxml");
    }

    @FXML
    public void showOwnerBookings() {
        setActive(ownerBookingsBtn);
        loadCenter("/com/pandalodge/view/bookings.fxml");
    }

    @FXML
    public void showStudents() {
        setActive(adminStudentsBtn);
        loadCenter("/com/pandalodge/view/student.fxml");
    }

    @FXML
    public void showAccommodationManagement() {
        setActive(adminAccommodationsBtn);
        loadCenter("/com/pandalodge/view/accommodation_management.fxml");
    }

    private void loadCenter(String resource) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Node node = loader.load();

            Object controller = loader.getController();

            if (controller instanceof StudentController) {
                ((StudentController) controller).setDashboardController(this);
            } else if (controller instanceof AccommodationManagementController) {
                ((AccommodationManagementController) controller).setDashboardController(this);
            } else if (controller instanceof BookingController) {
                ((BookingController) controller).setDashboardController(this);
            } else if (controller instanceof AdminOverviewController) {
                ((AdminOverviewController) controller).setDashboardController(this);
            } else if (controller instanceof ReviewManagementController) {
                ((ReviewManagementController) controller).setDashboardController(this);
            } else if (controller instanceof FAQManagementController) {
                ((FAQManagementController) controller).setDashboardController(this);
            } else if (controller instanceof ProfileController) {
                ((ProfileController) controller).setAsSubView();
            }

            centerPane.getChildren().setAll(node);
            node.setOpacity(1.0); // Ensure visible even if transition fails

            FadeTransition ft = new FadeTransition(Duration.millis(220), node);
            ft.setFromValue(0.1); // Start slightly above 0 to avoid being completely hidden
            ft.setToValue(1.0);
            ft.play();
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR loading view [" + resource + "]: " + e.getMessage());
            e.printStackTrace();

            // Show error message in center pane
            Label errorLabel = new Label("Failed to load section: " + resource + "\nError: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red; -fx-padding: 40; -fx-font-weight: bold;");
            centerPane.getChildren().setAll(errorLabel);
        }
    }
}
