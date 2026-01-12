package com.pandalodge.controller;

import com.pandalodge.dao.StudentDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.pandalodge.util.IconUtil;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button loginButton;
    @FXML
    private Button signupButton;
    @FXML
    private VBox branding;
    @FXML
    private Label iconUser;
    @FXML
    private Label iconLock;

    @FXML
    public void initialize() {
        // replace placeholders with FontIcon when available
        try {
            Node u = IconUtil.createIcon("mdi-account", "👤");
            Node l = IconUtil.createIcon("mdi-lock", "🔒");
            if (u != null && iconUser != null) {

                // parent is HBox -> VBox, so replace in HBox
                ((javafx.scene.layout.HBox) iconUser.getParent()).getChildren().set(0, u);
            }
            if (l != null && iconLock != null) {
                ((javafx.scene.layout.HBox) iconLock.getParent()).getChildren().set(0, l);
            }
        } catch (Exception ignored) {
        }

        loginButton.setOnAction(e -> handleLogin());
        signupButton.setOnAction(e -> showSignup());

        // responsive: hide branding when width small
        // branding may be null if not present
        if (branding != null) {
            // wait until scene is available
            branding.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.widthProperty().addListener((o, oldW, newW) -> {
                        branding.setVisible(newW.doubleValue() > 720);
                        branding.setManaged(newW.doubleValue() > 720);
                    });
                }
            });
        }
    }

    public void handleLogin() {
        String u = usernameField.getText();
        String p = passwordField.getText();
        if (u == null || u.isBlank()) {
            errorLabel.setText("Enter username or email");
            return;
        }
        // admin login - check multiple admin credentials
        if ((u.equals("admin") || u.equals("admin@panda.com")) &&
                p != null && (p.equals("admin") || p.equals("admin123"))) {
            com.pandalodge.util.UserSession.loginAdmin();
            openUnifiedDashboard();
            return;
        }
        // student login by email + password
        if (p == null || p.isBlank()) {
            errorLabel.setText("Enter password for student login");
            return;
        }
        com.pandalodge.model.Student s = StudentDAO.verify(u, p);
        if (s != null) {
            com.pandalodge.util.UserSession.login(s);
            openDashboard();
        } else {
            errorLabel.setText("Invalid credentials or unknown email");
        }
    }

    private void openUnifiedDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/dashboard.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panda Stays - Dashboard");
            stage.centerOnScreen();
        } catch (IOException e) {
            errorLabel.setText("Failed to load dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/signup.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Sign Up");
        } catch (IOException e) {
            errorLabel.setText("Failed to load signup: " + e.getMessage());
        }
    }

    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/home.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            // Get controller and set logged in state
            HomeController home = loader.getController();
            home.setLoggedIn(true);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panda - Home");
            stage.centerOnScreen();
        } catch (IOException e) {
            errorLabel.setText("Failed to load home: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void back() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/home.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panda - Home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
