package com.pandalodge.controller;

import com.pandalodge.dao.FAQDAO;
import com.pandalodge.model.FAQ;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.animation.RotateTransition;
import javafx.util.Duration;

import java.util.List;
import java.io.IOException;
import java.util.stream.Collectors;

public class FAQController {

    @FXML
    private TextField searchField;
    @FXML
    private VBox faqContainer;
    @FXML
    private HBox categoryBox;
    @FXML
    private Button btnAll;
    @FXML
    private Label totalFaqsLabel;

    private List<FAQ> allFaqs;
    private String currentCategory = "ALL";

    @FXML
    public void initialize() {
        loadFAQs();

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterFAQs());
        }
    }

    private void loadFAQs() {
        allFaqs = FAQDAO.findAll();
        if (totalFaqsLabel != null) {
            totalFaqsLabel.setText(String.valueOf(allFaqs.size()));
        }
        displayFAQs(allFaqs);
    }

    private void displayFAQs(List<FAQ> faqs) {
        faqContainer.getChildren().clear();

        if (faqs.isEmpty()) {
            Label noResults = new Label("No FAQs found matching your criteria.");
            noResults.getStyleClass().add("text-muted");
            noResults.setStyle("-fx-font-size: 16px; -fx-padding: 40 0;");
            faqContainer.getChildren().add(noResults);
            return;
        }

        for (FAQ faq : faqs) {
            VBox faqCard = createFAQCard(faq);
            faqContainer.getChildren().add(faqCard);
        }
    }

    private VBox createFAQCard(FAQ faq) {
        VBox card = new VBox(0);
        card.getStyleClass().add("faq-card");
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 0, 3); " +
                "-fx-cursor: hand; -fx-border-color: #e2e8f0; -fx-border-radius: 12;");
        card.setPadding(new Insets(0));
        card.setMaxWidth(Double.MAX_VALUE);

        // Question Header (clickable)
        HBox questionBox = new HBox(12);
        questionBox.setStyle("-fx-padding: 20 24; -fx-background-color: white; -fx-background-radius: 12;");
        questionBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label expandIcon = new Label("▶");
        expandIcon.setStyle("-fx-font-size: 10px; -fx-text-fill: #6366f1; -fx-font-weight: bold;");

        Label questionLabel = new Label(faq.getQuestion());
        questionLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        questionLabel.setWrapText(true);
        HBox.setHgrow(questionLabel, javafx.scene.layout.Priority.ALWAYS);

        Label categoryLabel = new Label(faq.getFormattedCategory());
        categoryLabel.setStyle("-fx-font-size: 12px; -fx-padding: 5 12; -fx-background-color: #f1f5f9; " +
                "-fx-background-radius: 15; -fx-text-fill: #475569;");

        questionBox.getChildren().addAll(expandIcon, questionLabel, categoryLabel);

        // Answer Section (hidden by default)
        VBox answerBox = new VBox(10);
        answerBox.setStyle("-fx-padding: 0 24 20 48; -fx-background-color: #fafbfc; -fx-background-radius: 0 0 12 12;");
        answerBox.setManaged(false);
        answerBox.setVisible(false);

        Label answerLabel = new Label(faq.getAnswer());
        answerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #475569; -fx-line-spacing: 6;");
        answerLabel.setWrapText(true);

        answerBox.getChildren().add(answerLabel);

        // Toggle answer visibility on click
        questionBox.setOnMouseClicked(e -> {
            boolean isVisible = answerBox.isVisible();
            answerBox.setVisible(!isVisible);
            answerBox.setManaged(!isVisible);
            expandIcon.setText(isVisible ? "▶" : "▼");

            // Update card style
            if (!isVisible) {
                card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(99,102,241,0.15), 15, 0, 0, 5); " +
                        "-fx-cursor: hand; -fx-border-color: #6366f1; -fx-border-radius: 12;");
                expandIcon.setStyle("-fx-font-size: 10px; -fx-text-fill: #6366f1; -fx-font-weight: bold;");
            } else {
                card.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 0, 3); " +
                        "-fx-cursor: hand; -fx-border-color: #e2e8f0; -fx-border-radius: 12;");
            }
        });

        // Hover effect
        questionBox.setOnMouseEntered(e -> {
            if (!answerBox.isVisible()) {
                questionBox.setStyle("-fx-padding: 20 24; -fx-background-color: #f8fafc; -fx-background-radius: 12;");
            }
        });
        questionBox.setOnMouseExited(e -> questionBox
                .setStyle("-fx-padding: 20 24; -fx-background-color: white; -fx-background-radius: 12;"));

        card.getChildren().addAll(questionBox, answerBox);
        return card;
    }

    private void filterFAQs() {
        String searchText = searchField.getText().toLowerCase().trim();

        List<FAQ> filtered = allFaqs.stream()
                .filter(faq -> {
                    boolean matchesSearch = searchText.isEmpty() ||
                            faq.getQuestion().toLowerCase().contains(searchText) ||
                            faq.getAnswer().toLowerCase().contains(searchText);

                    boolean matchesCategory = currentCategory.equals("ALL") ||
                            faq.getCategory().equalsIgnoreCase(currentCategory);

                    return matchesSearch && matchesCategory;
                })
                .collect(Collectors.toList());

        displayFAQs(filtered);
    }

    private void setActiveCategory(String category) {
        currentCategory = category;

        // Update button styles
        for (javafx.scene.Node node : categoryBox.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                boolean isActive = (category.equals("ALL") && btn == btnAll) ||
                        (!category.equals("ALL") && btn.getText().toUpperCase().contains(category.toUpperCase()));

                btn.getStyleClass().removeAll("btn-pill", "btn-pill-active", "btn-category", "btn-category-active");
                btn.getStyleClass().add(isActive ? "btn-pill-active" : "btn-pill");
            }
        }

        filterFAQs();
    }

    @FXML
    public void filterAll() {
        setActiveCategory("ALL");
    }

    @FXML
    public void filterGeneral() {
        setActiveCategory("GENERAL");
    }

    @FXML
    public void filterBooking() {
        setActiveCategory("BOOKING");
    }

    @FXML
    public void filterSecurity() {
        setActiveCategory("SECURITY");
    }

    @FXML
    public void filterAccommodation() {
        setActiveCategory("ACCOMMODATION");
    }

    @FXML
    public void goBack() {
        onHome();
    }

    @FXML
    public void onHome() {
        navigateTo("/com/pandalodge/view/home.fxml", "Home");
    }

    @FXML
    public void onViewRooms() {
        onBrowse();
    }

    @FXML
    public void onDashboard() {
        navigateTo("/com/pandalodge/view/dashboard.fxml", "Dashboard");
    }

    @FXML
    public void openFAQs() {
        // Already on FAQ page, could refresh or scroll up
        System.out.println("Already on FAQ page.");
    }

    @FXML
    public void onEmailSupport() {
        onContact();
    }

    @FXML
    public void onLiveChat() {
        // Feature removed as requested
    }

    @FXML
    public void onContact() {
        navigateTo("/com/pandalodge/view/contact.fxml", "Contact Us");
    }

    @FXML
    public void onBrowse() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pandalodge/view/dashboard.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            DashboardController dash = loader.getController();
            dash.showAccommodations();
            Stage stage = (Stage) faqContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panda - Browse");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateTo(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            Stage stage = (Stage) faqContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panda - " + title);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
