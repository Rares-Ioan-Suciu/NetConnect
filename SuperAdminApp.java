package com.netconnect.Applications.SuperAdminApp;

import com.netconnect.Applications.SuperAdminApp.Helpers.SuperAuthService;
import com.netconnect.Applications.InterfacesAndParents.Application;
import com.netconnect.Applications.SuperAdminApp.Scenes.SuperAdminUI;
import javafx.stage.Stage;

public class SuperAdminApp implements Application {
    private final Stage superAdminStage;
    private final SuperAuthService authService = new SuperAuthService();

    public SuperAdminApp(Stage superAdminStage) {
        this.superAdminStage = superAdminStage;
    }

    public void showMenu() {
        SuperAdminUI superAdminUI = new SuperAdminUI();
        superAdminUI.startApplication(this, this.superAdminStage);
    }


    public boolean login(String username, String password, String masterKey){
        boolean success = authService.superAdminLogin(username, password, masterKey);
        if (success) {
            System.out.println("Admin login successful.");
            return true;
        } else {
            System.out.println("Admin login failed.");
            return false;
        }
    }


    public void exit() {
        System.out.println("Exiting the super admin application. Goodbye!");
        System.exit(0);
    }
}
