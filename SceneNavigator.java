package com.netconnect.Applications.UserApp.Helpers;

import com.netconnect.Applications.UserApp.User;
import com.netconnect.Applications.UserApp.UserApplication;
import com.netconnect.Applications.InterfacesAndParents.SceneNavigatorBase;
import com.netconnect.Applications.UserApp.UserScenes.MenusLogin.*;
import com.netconnect.Applications.UserApp.UserScenes.SignUpUpdateProcess.*;
import com.netconnect.ChatRoom.ChatClient;
import com.netconnect.ChatRoom.ChatRoomScene;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneNavigator extends SceneNavigatorBase {

    private final UserApplication userApp;
    public User currentUser;
    private ChatClient chatClient;

    public SceneNavigator(Stage stage, UserApplication userApp) {
        super(stage);
        this.userApp = userApp;
    }

    @Override
    public Class<?> getInitialSceneClass() {
        return MenuScene.class;
    }

    @Override
    protected Scene createScene(Class<?> sceneClass) {
        if (sceneClass == MenuScene.class) {
            return new MenuScene(this).getScene();
        } else if (sceneClass == LoginScene.class) {
            return new LoginScene(this, userApp).getScene();
        } else if (sceneClass == ExitScene.class) {
            return new ExitScene(this).getScene();
        } else if (sceneClass == SignUpScene.class) {
            return new SignUpScene(this, userApp).getScene();
        } else if (sceneClass == UserPrivateDetailsScene.class) {
            return new UserPrivateDetailsScene(this, userApp, currentUser).getScene();
        } else if (sceneClass == UserGeneralDetailsScene.class) {
            return new UserGeneralDetailsScene(this, userApp, currentUser).getScene();
        } else if (sceneClass == QuestionsScene.class) {
            return new QuestionsScene(this, userApp, currentUser).getScene();
        } else if (sceneClass == ProfilePictureScene.class) {
            return new ProfilePictureScene(this, userApp, currentUser).getScene();
        } else if (sceneClass == MainScreenScene.class) {
            return new MainScreenScene(this).getScene();
        } else if (sceneClass == SeeProfileScene.class) {
            return new SeeProfileScene(this, currentUser, false, 0).getScene();
        } else if (sceneClass == MatchScene.class) {
            return new MatchScene(this, userApp, currentUser).getScene();
        } else if (sceneClass == ReportBugScene.class) {
            return new ReportBugScene(this).getScene();
        } else if (sceneClass == ReportProfileScene.class) {
            return new ReportProfileScene(this).getScene();
        } else if (sceneClass == ChatRoomScene.class) {
            return new ChatRoomScene(this, chatClient).getScene();
        }
           else if (sceneClass == UpdateProfileScene.class)
        {
            return new UpdateProfileScene(this, currentUser).getScene();
        }
           else if(sceneClass == ChangePasswordScene.class)
        {
            return new ChangePasswordScene(this, currentUser).getScene();
        }
           else if(sceneClass == LoginWithGoogleScene.class) {
            return new LoginWithGoogleScene(this, userApp).getScene();
        } else if (sceneClass == SignUpWithGoogleScene.class) {
            return new SignUpWithGoogleScene(this, userApp).getScene();
        } else if (sceneClass == ChangeDOBScene.class) {
            return new ChangeDOBScene(this, currentUser).getScene();
        }
           else if(sceneClass == ChangeGenderScene.class)
        {
            return new ChangeGenderScene(this, currentUser).getScene();
        }
        return null;
    }

    public void setUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void setChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public Stage getStage() {
        return this.stage;
    }

    public User getUser() {
        return this.currentUser;
    }

}
