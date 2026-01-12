package com.pandalodge.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import com.pandalodge.dao.*;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // initialize database (create tables if not exists)
        StudentDAO.init();
        AccommodationDAO.init();
        BookingDAO.init();
        RenterDAO.init();
        AdminDAO.init();
        ReviewDAO.init();
        PhotoDAO.init();
        PaymentDAO.init();
        FAQDAO.init();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/home.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Get screen dimensions for proper sizing
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        stage.setTitle("Panda - Home");
        stage.setScene(scene);

        // Set sensible window dimensions (80% of screen or max 1200x800)
        double windowWidth = Math.min(screenWidth * 0.8, 1200);
        double windowHeight = Math.min(screenHeight * 0.85, 800);

        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setWidth(windowWidth);
        stage.setHeight(windowHeight);

        // Center on screen
        stage.setX((screenWidth - windowWidth) / 2);
        stage.setY((screenHeight - windowHeight) / 2);

        stage.show();
        stage.setMaximized(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
