package com.netconnect.Applications.InterfacesAndParents;

import javafx.stage.Stage;

public abstract class BaseApplicationUI<T extends Application, N extends SceneNavigatorBase> {

    protected N navigator;

    public void startApplication(T appInstance, Stage stage) {
        navigator = createNavigator(appInstance, stage);
        navigator.start();
    }

    protected abstract N createNavigator(T appInstance, Stage stage);
}
