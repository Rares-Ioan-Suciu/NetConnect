package com.netconnect.Applications.UserApp.UserScenes.SignUpUpdateProcess;

import com.netconnect.Applications.UserApp.User;
import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.Applications.UserApp.UserScenes.MenusLogin.ReportBugScene;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import com.netconnect.Applications.UserApp.Helpers.SceneNavigator;
import com.netconnect.Applications.UserApp.UserApplication;

import java.util.List;
import java.util.stream.Collectors;

public class UserGeneralDetailsScene implements Sceneable {

    private final SceneNavigator navigator;
    private final UserApplication userApp;
    private final User currentUser;

    public UserGeneralDetailsScene(SceneNavigator navigator, UserApplication userApp, User currentUser) {
        this.navigator = navigator;
        this.userApp = userApp;
        this.currentUser = currentUser;
    }

    public Scene getScene() {
        List<String> lookingForOptions = List.of("Looking for help", "Coordonating Bachelor Thesis", "Coordonating Mastert Disertation","Coordonating PHD Thesis","Offering help", "Offering a job", "Looking for a job",
                "Looking to chat", "Looking to educate myself", "Interested in mentoring", "Seeking mentorship",
                "Volunteering for tech events", "Contributing to open-source projects", "Coordonator for my Bachelor Thesis", "Coordonator for my Master Disertation", "Coordonator for my PHD Thesis");

        List<String> abilitiesOptions = List.of("Problem Solving", "Teamwork", "Creativity", "Leadership", "Time Management",
                "Adaptability", "Communication", "Strategic Thinking", "Project Management", "Attention to Detail");

        List<String> languagesOptions = List.of("Java", "Python", "JavaScript", "C++", "Ruby", "C", "Scratch", "HTML/CSS",
                "PHP", "Go", "R", "Perl", "Rust", "Scala", "Shell Scripting");

        List<String> expertiseOptions = List.of("Machine Learning", "Mobile Development", "Networking", "Database Management",
                "Cloud Computing", "Cybersecurity", "Embedded Systems", "Data Science", "Game Development",
                "Artificial Intelligence", "Natural Language Processing", "DevOps", "AR/VR Development",
                "Systems Architecture");

        List<String> educationOptions = List.of("Bachelor's Degree", "Master's Degree", "PhD", "Bootcamp Certificate",
                "Associate's Degree", "High School Diploma", "Online Course Completion");

        VBox interestsBox = createCheckboxGroup("Interests in Computer Science", lookingForOptions);
        VBox abilitiesBox = createCheckboxGroup("Abilities / Skills", abilitiesOptions);
        VBox languagesBox = createCheckboxGroup("Programming Languages", languagesOptions);
        VBox expertiseBox = createCheckboxGroup("Areas of Expertise", expertiseOptions);
        VBox educationBox = createCheckboxGroup("Education", educationOptions);

        Button saveGeneralInfoButton = new Button("Save General Info");
        styleButton(saveGeneralInfoButton, "#4CAF50");
        saveGeneralInfoButton.setOnAction(event -> {
            if (anyCheckboxGroupEmpty(interestsBox, abilitiesBox, languagesBox, expertiseBox, educationBox)) {
                SceneNavigator.showAlert("Please select at least one option in each category.");
                return;
            }

            List<String> selectedInterests = getSelectedOptions(interestsBox);
            List<String> selectedAbilities = getSelectedOptions(abilitiesBox);
            List<String> selectedLanguages = getSelectedOptions(languagesBox);
            List<String> selectedExpertise = getSelectedOptions(expertiseBox);
            List<String> selectedEducation = getSelectedOptions(educationBox);

            currentUser.setGeneralDetails(selectedInterests, selectedAbilities, selectedLanguages, selectedExpertise, selectedEducation);
            userApp.updateGeneralDetails(
                    currentUser.getUsername().getDetails(),
                    selectedInterests,
                    selectedAbilities,
                    selectedLanguages,
                    selectedExpertise,
                    selectedEducation
            );
            if (!currentUser.getUpdating())
                navigator.navigateTo(QuestionsScene.class);
            else {
                navigator.goBack();
            }
        });

        Button backButton = new Button("Back");
        styleButton(backButton, "#f44336");
        backButton.setOnAction(event -> navigator.goBack());

        Button reportBug = new Button("Report Bug");
        styleButton(reportBug, "#FF9800");
        reportBug.setOnAction(actionEvent -> navigator.navigateTo(ReportBugScene.class));

        VBox layout = new VBox(20,
                createStyledLabel("Enter Your General Details"),
                interestsBox, abilitiesBox, languagesBox, expertiseBox, educationBox,
                saveGeneralInfoButton, backButton, reportBug
        );
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20; -fx-background-color: #ffffff;");

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #f9f9f9; -fx-border-color: #dcdcdc;");

        return new Scene(scrollPane, 900, 1000);
    }

    private VBox createCheckboxGroup(String label, List<String> options) {
        VBox box = new VBox(10);
        box.setStyle("-fx-border-color: #dcdcdc; -fx-border-radius: 5; -fx-padding: 10; -fx-background-color: #f9f9f9;");
        box.getChildren().add(createStyledLabel(label));
        for (String option : options) {
            CheckBox checkBox = new CheckBox(option);
            checkBox.setStyle("-fx-font-size: 14px;");
            box.getChildren().add(checkBox);
        }
        return box;
    }

    private List<String> getSelectedOptions(VBox box) {
        return box.getChildren().stream()
                .filter(node -> node instanceof CheckBox && ((CheckBox) node).isSelected())
                .map(node -> ((CheckBox) node).getText())
                .collect(Collectors.toList());
    }

    private boolean anyCheckboxGroupEmpty(VBox... groups) {
        for (VBox group : groups) {
            if (getSelectedOptions(group).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", 18));
        label.setTextFill(Color.DARKBLUE);
        return label;
    }

    private void styleButton(Button button, String color) {
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #ffffff; -fx-border-color: " + color + "; -fx-text-fill: " + color + "; -fx-font-size: 14px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px;"));
    }
}
