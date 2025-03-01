package com.netconnect.Applications.AdminApp.Helpers;

import com.netconnect.Applications.AdminApp.AdminApplication;
import com.netconnect.Applications.AdminApp.AdminUI.*;
import com.netconnect.Applications.InterfacesAndParents.SceneNavigatorBase;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AdminSceneNavigator extends SceneNavigatorBase {

    private final AdminApplication adminApp;

    public AdminSceneNavigator(Stage stage, AdminApplication adminApp) {
        super(stage);
        this.adminApp = adminApp;
    }

    @Override
    protected Class<?> getInitialSceneClass() {
        return AdminMenuScene.class;
    }

    @Override
    protected Scene createScene(Class<?> sceneClass) {
        if (sceneClass == AdminMenuScene.class) {
            return new AdminMenuScene(this, adminApp).getScene();
        } else if (sceneClass == AdminLoginScene.class) {
            return new AdminLoginScene(this, adminApp).getScene();
        } else if (sceneClass == ReportsBugsScene.class) {
            return new ReportsBugsScene(this).getScene();
        } else if (sceneClass == ReportsProfilesScene.class) {
            return new ReportsProfilesScene(this).getScene();
        } else if (sceneClass == ExitSceneAdmin.class) {
            return new ExitSceneAdmin(this).getScene();
        } else if (sceneClass == ReportsIntermediaryScene.class) {
            return new ReportsIntermediaryScene(this).getScene();
        }
        return null;
    }
}
