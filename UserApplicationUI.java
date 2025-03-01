package com.netconnect.Applications.UserApp.Helpers;

import com.netconnect.Applications.UserApp.UserApplication;
import com.netconnect.Applications.InterfacesAndParents.BaseApplicationUI;
import javafx.stage.Stage;

public class UserApplicationUI extends BaseApplicationUI<UserApplication, SceneNavigator> {
    @Override
    protected SceneNavigator createNavigator(UserApplication userApp, Stage stage) {
        return new SceneNavigator(stage, userApp);
    }
}
