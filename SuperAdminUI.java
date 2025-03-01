package com.netconnect.Applications.SuperAdminApp.Scenes;

import com.netconnect.Applications.InterfacesAndParents.BaseApplicationUI;
import com.netconnect.Applications.SuperAdminApp.SuperAdminApp;
import com.netconnect.Applications.SuperAdminApp.Helpers.SuperAdminNavigator;
import javafx.stage.Stage;

public class SuperAdminUI extends BaseApplicationUI<SuperAdminApp, SuperAdminNavigator> {

    @Override
    protected SuperAdminNavigator createNavigator(SuperAdminApp superAdminApp, Stage stage) {
        return new SuperAdminNavigator(stage, superAdminApp);
    }
}
