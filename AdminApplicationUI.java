package com.netconnect.Applications.AdminApp.AdminUI;

import com.netconnect.Applications.AdminApp.AdminApplication;
import com.netconnect.Applications.AdminApp.Helpers.AdminSceneNavigator;
import com.netconnect.Applications.InterfacesAndParents.BaseApplicationUI;
import javafx.stage.Stage;

public class AdminApplicationUI extends BaseApplicationUI<AdminApplication, AdminSceneNavigator> {

    @Override
    protected AdminSceneNavigator createNavigator(AdminApplication adminApp, Stage stage) {
        return new AdminSceneNavigator(stage, adminApp);
    }
}
