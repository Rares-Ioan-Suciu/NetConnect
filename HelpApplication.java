package com.netconnect.Applications.HelpApp;

import com.netconnect.Applications.InterfacesAndParents.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HelpApplication implements Application {

    private final Stage stage;

    public HelpApplication(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void showMenu() {

        Label titleLabel = new Label("Welcome to NetConnect!");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        titleLabel.setTextFill(Color.DARKBLUE);

        Text introText = new Text("NetConnect is your go-to platform for connecting with experts, recruiters, and enthusiasts in the CS field. Below is a quick guide to help you navigate and use the application.");
        introText.setFont(Font.font("Arial", 14));
        introText.setWrappingWidth(500);


        VBox sections = new VBox(20);
        sections.setPadding(new Insets(20));

        sections.getChildren().addAll(
                createSection("Opening the App",
                        "1. Use argument \"user\" to connect as a user, where you can make an account or log in.\n" +
                                "2. Use argument \"admin\" to log in as an admin and react to bug and user reports.\n" +
                                "3. Use argument \"super_admin\" to connect as a super admin and manage admins, users, and handle reports.\n" +
                                "4. Use argument \"server\" to start the server for chatting between users."),
                createSection("Getting Started",
                        "1. Sign up or log in using the main menu.\n" +
                                "2. Complete your profile for better matches.\n" +
                                "3. Use the 'Match' button to find the most suitable connection."),
                createSection("Chatting",
                        "Once you choose your perfect match, click the 'Chat with User' button and start a conversation."),
                createSection("Features",
                        "• Connect with people offering help or seeking support.\n" +
                                "• Report issues directly to the admin.\n" +
                                "• Customize your profile with interests and abilities for accurate matching."),
                createSection("Tips",
                        "• Use specific interests for better matches.\n" +
                                "• Come back regularly to see new features.")
        );


        ScrollPane scrollPane = new ScrollPane(sections);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f5f5f5;");


        Button exitButton = new Button("Exit");
        exitButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        exitButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-padding: 10;");
        exitButton.setOnMouseEntered(e -> exitButton.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white; -fx-padding: 10;"));
        exitButton.setOnMouseExited(e -> exitButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-padding: 10;"));
        exitButton.setOnAction(e -> stage.close());


        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #e8f4f8;");
        mainLayout.getChildren().addAll(titleLabel, introText, scrollPane, exitButton);

        Scene helpScene = new Scene(mainLayout, 800, 1200);
        stage.setScene(helpScene);
        stage.show();
    }

    private VBox createSection(String title, String content) {
        Label sectionTitle = new Label(title);
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        sectionTitle.setTextFill(Color.DARKBLUE);

        Text sectionContent = new Text(content);
        sectionContent.setFont(Font.font("Arial", 14));
        sectionContent.setWrappingWidth(500);

        VBox section = new VBox(10, sectionTitle, sectionContent);
        section.setPadding(new Insets(10));
        section.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
        return section;
    }

    public Stage getStage() {
        return this.stage;
    }
}
