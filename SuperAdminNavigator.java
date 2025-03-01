package com.netconnect.Applications.SuperAdminApp.Helpers;

import com.netconnect.Applications.AdminApp.AdminUI.ReportsBugsScene;
import com.netconnect.Applications.SuperAdminApp.Scenes.*;
import com.netconnect.Applications.SuperAdminApp.SuperAdminApp;
import com.netconnect.Applications.InterfacesAndParents.SceneNavigatorBase;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SuperAdminNavigator extends SceneNavigatorBase {
    private final SuperAdminApp superAdminApp;

    public SuperAdminNavigator(Stage stage, SuperAdminApp superAdminApp) {
        super(stage);
        this.superAdminApp = superAdminApp;
    }

    @Override
    protected Class<?> getInitialSceneClass() {
        return SuperAdminMenuScene.class;
    }

    @Override
    protected Scene createScene(Class<?> sceneClass) {
        if (sceneClass == SuperAdminMenuScene.class) {
            return new SuperAdminMenuScene(this).getScene();
        } else if (sceneClass == SuperAdminLoginScene.class) {
            return new SuperAdminLoginScene(this, superAdminApp).getScene();
        } else if(sceneClass == SuperAdminOptionsScene.class) {
            return new SuperAdminOptionsScene(this).getScene();
        } else if (sceneClass == ReportSuperProfilesScene.class) {
            return new ReportSuperProfilesScene(this).getScene();
        } else if (sceneClass == ManageUsersScene.class) {
            return new ManageUsersScene(this).getScene();
        } else if (sceneClass == ReportsIntermediarySuperScene.class) {
            return new ReportsIntermediarySuperScene(this).getScene();
        } else if (sceneClass == ReportsSuperBugsScene.class) {
            return new ReportsSuperBugsScene(this).getScene();
        } else if (sceneClass == ManageAdminsScene.class) {
            return new ManageAdminsScene(this).getScene();
        }
        return null;
    }
}
