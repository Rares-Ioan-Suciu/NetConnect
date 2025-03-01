package com.netconnect.Applications.UserApp;

import com.netconnect.Applications.InterfacesAndParents.Application;
import com.netconnect.Applications.UserApp.Helpers.*;
import com.netconnect.Applications.UserApp.Details.Email;
import com.netconnect.Applications.UserApp.Details.Password;
import com.netconnect.Applications.UserApp.Details.Username;
import com.netconnect.GoogleLoginHelper;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class UserApplication implements Application {

    Auth authService = new Auth();
    UserProfileService userProfileService = new UserProfileService();
    private final Stage userStage;
    private User currentUser;

    public UserApplication(Stage userStage) {
        this.userStage = userStage;
    }

    public void showMenu() {
        UserApplicationUI UserUI = new UserApplicationUI();
        UserUI.startApplication(this, this.userStage);
    }

    public User login(Username username, String password) {
        this.currentUser = authService.login(username, password);
        return this.currentUser;
    }

    public User signUp(Username username, Email email, Password password) {
        this.currentUser = authService.signUp(username, email, password);
        return this.currentUser;
    }

    public void updateUserDetails(String username, String fullName, String gender, String dateOfBirth) {
        userProfileService.updateUserDetails(username, fullName, gender, dateOfBirth);
    }

    public void updateGeneralDetails(String username, List<String> interests, List<String> abilities, List<String> languages, List<String> expertise, List<String> education) {
        userProfileService.updateGeneralDetails(username, interests, abilities, languages, expertise, education);
    }

    public void updateQuestions(String username, String question) {
        userProfileService.updateQuestions(username, question);
    }

    public void updateProfilePicture(File file, String username) {
        userProfileService.updateProfilePicture(file, username);
    }

    public boolean googleLogin(GoogleLoginHelper.GoogleUser googleUser) {
        Auth auth = new Auth();
        User user = auth.googleLogin(googleUser);

        if (user != null) {
            setCurrentUser(user);
            return true;
        }
        return false;
    }

    private void setCurrentUser(User user) {
        this.currentUser = user;
    }


    public int match(User other,User currentUser)
    {
        return MatchAlgorithm.calculateMatchScore(currentUser, other);
    }

    public void exit() {
        System.out.println("Exiting the application. Goodbye!");
        System.exit(0);
    }


    public User getCurrentUser() {
        return this.currentUser;
    }

    public void googleSignUp(GoogleLoginHelper.GoogleUser googleUser) {
        Auth auth = new Auth();
        User user = auth.googleSignUp(googleUser);

        if (user != null) {
            setCurrentUser(user);
            return;
        }
        if(Auth.getDiagnosis() == 1)
            googleUser.setBanned(1);
    }

}
