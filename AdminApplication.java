package com.netconnect.Applications.AdminApp;

import com.netconnect.Applications.AdminApp.AdminUI.AdminApplicationUI;
import com.netconnect.Applications.InterfacesAndParents.Application;
import com.netconnect.Applications.AdminApp.Helpers.AuthService;
import javafx.stage.Stage;
public class AdminApplication implements Application {

    private final Stage adminStage;
    private final AuthService authService = new AuthService();

    public AdminApplication(Stage adminStage) {
        this.adminStage = adminStage;
    }

    public void showMenu() {
        AdminApplicationUI uiAdmin = new AdminApplicationUI();
        uiAdmin.startApplication(this, this.adminStage);
    }

    public boolean login(String username, String password, String employeeCode)  {
        boolean success = authService.adminLogin(username, password, employeeCode);
        if (success) {
            System.out.println("Admin login successful.");
            return true;
        } else {
            System.out.println("Admin login failed.");
            return false;
        }
    }


    public void exit() {
        System.out.println("Exiting the admin application. Goodbye!");
        System.exit(0);
    }
}
