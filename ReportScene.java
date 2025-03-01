package com.netconnect.Applications.UserApp.UserScenes.MenusLogin;

import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import com.netconnect.Applications.UserApp.Helpers.SceneNavigator;
public abstract class ReportScene implements Sceneable {

    protected final SceneNavigator navigator;
    protected final VBox layout;

    public ReportScene(SceneNavigator navigator) {
        this.navigator = navigator;
        this.layout = new VBox(10);
    }

    public abstract Scene getScene();

    protected abstract void submitReport();

    protected Scene initializeScene() {
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> navigator.goBack());

        layout.getChildren().add(backButton);
        return new Scene(layout, 900, 1000);
    }

    protected boolean validateInput(TextArea... fields) {
        for (TextArea field : fields) {
            if (field.getText().trim().isEmpty() && field.getText().length() <=1000) {
                showError("All fields are required.");
                return false;
            }
        }
        return true;
    }

    protected void showError(String message) {
        Label errorLabel = new Label(message);
        layout.getChildren().add(errorLabel);
    }
}
