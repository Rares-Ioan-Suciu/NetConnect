package com.netconnect.Applications.InterfacesAndParents;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.Stack;

public abstract class SceneNavigatorBase {

    protected final Stage stage;
    protected final Stack<Scene> historyStack = new Stack<>();

    public SceneNavigatorBase(Stage stage) {
        this.stage = stage;
        stage.setTitle("NetConnect");
    }

    public void start() {
        navigateTo(getInitialSceneClass());
    }


    protected abstract Class<?> getInitialSceneClass();


    public void navigateTo(Class<?> sceneClass) {
        if (stage.getScene() != null) {
            historyStack.push(stage.getScene());
        }
        stage.setScene(createScene(sceneClass));
        stage.show();
    }


    protected abstract Scene createScene(Class<?> sceneClass);


    public void goBack() {
        if (!historyStack.isEmpty()) {
            stage.setScene(historyStack.pop());
        }
    }

    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void close() {
        stage.close();
        System.exit(0);
    }
}
