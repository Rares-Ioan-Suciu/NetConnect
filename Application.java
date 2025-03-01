package com.netconnect.Applications.InterfacesAndParents;


public interface Application {

    void showMenu();

    default boolean login(String username, String password) {

        return false;
    }

    default boolean signUp(String username, String password) {

        return false;
    }

    default void exit() {
        System.out.println("Exiting the application.");
        System.exit(0);
    }
}
