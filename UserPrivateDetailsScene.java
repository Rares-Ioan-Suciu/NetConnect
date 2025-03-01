package com.netconnect.Applications.UserApp.UserScenes.SignUpUpdateProcess;

import com.netconnect.Applications.UserApp.Details.DateOfBirthDetails;
import com.netconnect.Applications.UserApp.Details.NameDetails;
import com.netconnect.Applications.UserApp.Exception.InvalidDateOfBirthException;
import com.netconnect.Applications.UserApp.Exception.InvalidFullNameException;
import com.netconnect.Applications.UserApp.User;
import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.Applications.UserApp.UserScenes.MenusLogin.ReportBugScene;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.scene.paint.Paint;
import com.netconnect.Applications.UserApp.Helpers.SceneNavigator;
import com.netconnect.Applications.UserApp.UserApplication;

import java.time.LocalDate;

public class UserPrivateDetailsScene implements Sceneable {

    private final SceneNavigator navigator;
    private final UserApplication userApp;
    private final User currentUser;

    public UserPrivateDetailsScene(SceneNavigator navigator, UserApplication userApp, User currentUser) {
        this.navigator = navigator;
        this.userApp = userApp;
        this.currentUser = currentUser;
    }

    public Scene getScene() {
        Text title = new Text("Enter Your Private Details");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setFill(Color.DARKSLATEBLUE);
        title.setTextAlignment(TextAlignment.CENTER);

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");
        fullNameField.setStyle("-fx-background-color: #f4f4f9; -fx-border-color: #8a94b8; -fx-border-radius: 5;");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Date of Birth");
        datePicker.setStyle("-fx-background-color: #f4f4f9; -fx-border-color: #8a94b8; -fx-border-radius: 5;");

        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Man", "Woman", "Non-binary", "Other");
        genderComboBox.setPromptText("Gender");
        genderComboBox.setStyle("-fx-background-color: #f4f4f9; -fx-border-color: #8a94b8; -fx-border-radius: 5;");

        Button saveButton = new Button("Save Info");
        saveButton.setStyle("-fx-background-color: #4a6fa5; -fx-text-fill: white; -fx-font-weight: bold;");
        saveButton.setOnAction(event -> {
            String fullNameInput = fullNameField.getText();
            String gender = genderComboBox.getValue();
            LocalDate dateOfBirthInput = datePicker.getValue();

            if (fullNameInput.isEmpty() || gender == null || dateOfBirthInput == null) {
                SceneNavigator.showAlert("Please fill in all fields.");
                return;
            }

            try {
                NameDetails fullName = new NameDetails(fullNameInput);
                fullName.verifyInfo();

                DateOfBirthDetails dateOfBirth = new DateOfBirthDetails(dateOfBirthInput.toString());

                currentUser.setDetails(fullName, gender, dateOfBirth);

                userApp.updateUserDetails(currentUser.getUsername().getDetails(), fullName.getDetails(), gender, dateOfBirth.getDetails());
                navigator.navigateTo(UserGeneralDetailsScene.class);

            } catch (InvalidFullNameException | InvalidDateOfBirthException e) {
                SceneNavigator.showAlert(e.getMessage());
            }
        });

        Button reportBugButton = new Button("Report Bug");
        reportBugButton.setStyle("-fx-background-color: #c84b31; -fx-text-fill: white; -fx-font-weight: bold;");
        reportBugButton.setOnAction(event -> navigator.navigateTo(ReportBugScene.class));

        HBox buttonLayout = new HBox(10, saveButton, reportBugButton);
        buttonLayout.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, title, fullNameField, genderComboBox, datePicker, buttonLayout);
        layout.setAlignment(Pos.CENTER);

        layout.setBackground(new Background(new BackgroundFill(Paint.valueOf("#fefeff"), CornerRadii.EMPTY, Insets.EMPTY)));
        layout.setStyle("-fx-border-color: #8a94b8; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        return new Scene(layout, 900, 1000);
    }
}
