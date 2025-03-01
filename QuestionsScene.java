package com.netconnect.Applications.UserApp.UserScenes.SignUpUpdateProcess;

import com.netconnect.Applications.UserApp.User;
import com.netconnect.Applications.UserApp.Details.Question;
import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.Applications.UserApp.UserScenes.MenusLogin.ReportBugScene;
import com.netconnect.QueryExecutor;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import com.netconnect.Applications.UserApp.Helpers.SceneNavigator;
import com.netconnect.Applications.UserApp.UserApplication;
import java.util.stream.Collectors;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionsScene implements Sceneable {

    private final SceneNavigator navigator;
    private final UserApplication userApp;
    private final User currentUser;
    private final static QueryExecutor queryExecutor = new QueryExecutor();

    public QuestionsScene(SceneNavigator navigator, UserApplication userApp, User currentUser) {
        this.navigator = navigator;
        this.userApp = userApp;
        this.currentUser = currentUser;
    }

    @Override
    public Scene getScene() {
        Label headerLabel = new Label("Select Your Questions");
        headerLabel.setFont(Font.font("Arial", 20));
        headerLabel.setTextFill(Color.web("#2E7D32"));

        VBox categoryBox = createCategoryBox();
        ScrollPane questionScrollPane = new ScrollPane();
        VBox questionsBox = new VBox(10);
        questionScrollPane.setContent(questionsBox);
        questionScrollPane.setFitToWidth(true);

        Button confirmButton = createConfirmButton(questionsBox);
        confirmButton.setDisable(true);

        Button backButton = createStyledButton("Back");
        backButton.setOnAction(event -> navigator.goBack());

        Button reportBugButton = createStyledButton("Report Bug");
        reportBugButton.setOnAction(event -> navigator.navigateTo(ReportBugScene.class));

        HBox buttonBox = new HBox(15, backButton, confirmButton, reportBugButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        setupCategorySelectionAction(categoryBox, questionsBox, confirmButton);

        VBox layout = new VBox(15, headerLabel, categoryBox, questionScrollPane, buttonBox);
        layout.setStyle("-fx-background-color: #F1F8E9; -fx-padding: 20;");
        return new Scene(layout, 900, 1000);
    }

    private VBox createCategoryBox() {
        VBox categoryBox = new VBox(10, new Label("Select Categories"));
        List<String> categories = loadCategories();
        if (categories.isEmpty()) {
            categoryBox.getChildren().add(new Label("No categories available."));
        } else {
            categories.stream()
                    .map(CheckBox::new)
                    .forEach(categoryBox.getChildren()::add);
        }
        return categoryBox;
    }

    private List<String> loadCategories() {
        List<String> categories = new ArrayList<>();
        String query = "SELECT category_name FROM categories";

        try (Connection connection = queryExecutor.getAppUserConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                categories.add(resultSet.getString("category_name"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading categories: " + e.getMessage());
        }
        return categories;
    }

    private void setupCategorySelectionAction(VBox categoryBox, VBox questionsBox, Button confirmButton) {
        categoryBox.getChildren().stream()
                .filter(node -> node instanceof CheckBox)
                .map(node -> (CheckBox) node)
                .forEach(categoryCheckBox -> categoryCheckBox.setOnAction(event -> {
                    questionsBox.getChildren().clear();
                    List<String> selectedCategories = categoryBox.getChildren().stream()
                            .filter(node -> node instanceof CheckBox && ((CheckBox) node).isSelected())
                            .map(node -> ((CheckBox) node).getText())
                            .collect(Collectors.toList());

                    selectedCategories.forEach(category -> displayQuestionsByCategory(questionsBox, category, confirmButton));
                }));
    }

    private Button createConfirmButton(VBox questionsBox) {
        Button confirmButton = createStyledButton("Confirm Selection");
        confirmButton.setOnAction(event -> {
            String selectedQuestionEntry = questionsBox.getChildren().stream()
                    .filter(node -> node instanceof CheckBox && ((CheckBox) node).isSelected())
                    .map(node -> (String) ((CheckBox) node).getUserData())
                    .findFirst()
                    .orElse(null);

            if (selectedQuestionEntry != null) {
                String[] parts = selectedQuestionEntry.split(": ", 2);
                if (parts.length == 2) {
                    String category = parts[0].trim();
                    String text = parts[1].trim();

                    int categoryId = getCategoryId(category);


                    Question question = getQuestion(categoryId, text);
                    currentUser.setQuestion(question);
                    userApp.updateQuestions(currentUser.getUsername().getDetails(), selectedQuestionEntry);
                    navigator.navigateTo(ProfilePictureScene.class);
                }
            }

        });
        return confirmButton;
    }

    private void displayQuestionsByCategory(VBox questionsBox, String category, Button confirmButton) {
        List<Question> questions = loadQuestionsByCategory(category);
        if (questions.isEmpty()) {
            questionsBox.getChildren().add(new Label("No questions available for this category."));
        } else {
            Label categoryLabel = new Label("Category: " + category);
            categoryLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            questionsBox.getChildren().add(categoryLabel);

            for (Question question : questions) {
                CheckBox questionBox = new CheckBox(question.getText());
                String questionEntry = category + ": " + question.getText();
                questionBox.setUserData(questionEntry);
                questionsBox.getChildren().add(questionBox);

                questionBox.setOnAction(e -> {
                    if (questionBox.isSelected()) {
                        questionsBox.getChildren().stream()
                                .filter(node -> node instanceof CheckBox && node != questionBox)
                                .forEach(node -> ((CheckBox) node).setDisable(true));
                        confirmButton.setDisable(false);
                    } else {
                        questionsBox.getChildren().stream()
                                .filter(node -> node instanceof CheckBox)
                                .forEach(node -> ((CheckBox) node).setDisable(false));
                        confirmButton.setDisable(true);
                    }
                });
            }
        }
    }

    private int getCategoryId(String categoryName) {
        String query = "SELECT category_id FROM categories WHERE category_name = ?";
        try (Connection connection = queryExecutor.getAppUserConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, categoryName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("category_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching category ID: " + e.getMessage());
        }
        throw new IllegalArgumentException("Invalid category name: " + categoryName);
    }


    public static Question getQuestion(int categoryId, String questionText) {

        String categoryName = mapCategoryIdToName(categoryId);

        List<Question> questions = loadQuestionsByCategory(categoryName);

        return questions.stream()
                .filter(q -> q.getText().equals(questionText))
                .findFirst()
                .orElse(null);
    }

    private static String mapCategoryIdToName(int categoryId) {
        switch (categoryId) {
            case 1: return "Operating Systems";
            case 2: return "Algorithms";
            case 3: return "Data Structures";
            case 4: return "Graph Theory";
            default: return "";
        }
    }



    private static List<Question> loadQuestionsByCategory(String category) {
        List<Question> questions = new ArrayList<>();
        String query = """
                SELECT question_text, option_a, option_b, option_c, option_d, correct_option
                FROM questions q
                JOIN categories c ON q.category_id = c.category_id
                WHERE c.category_name = ?""";

        try (Connection connection = queryExecutor.getAppUserConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, category);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String questionText = resultSet.getString("question_text");
                    List<String> options = List.of(
                            resultSet.getString("option_a"),
                            resultSet.getString("option_b"),
                            resultSet.getString("option_c"),
                            resultSet.getString("option_d")
                    );
                    String correctOption = resultSet.getString("correct_option");
                    questions.add(new Question(questionText, options, correctOption));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading questions for category: " + e.getMessage());
        }
        return questions;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        return button;
    }
}
