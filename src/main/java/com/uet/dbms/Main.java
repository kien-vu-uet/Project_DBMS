package com.uet.dbms;

import com.uet.dbms.Entities.Account;
import com.uet.dbms.GUIControllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    private static Account UserAccount;

    @FXML
    private static Stage mainStage;

    @FXML
    private static Image Icon;

    public static Image getIcon() {
        if (Icon == null) {
            Icon = new Image(Objects.requireNonNull(Main.class.getResourceAsStream("icons/icon.png")));
        }
        return Icon;
    }

    public static Account getUserAccount() {
        return UserAccount;
    }

    public static boolean isLoggedIn() {
        return UserAccount != null && !UserAccount.getUsername().isEmpty();
    }

    public static void logOut() {
        UserAccount = null;
    }

    public static void setUserAccount(Account other) {
        UserAccount = other;
    }

    @Override
    public void start(Stage stage) throws RuntimeException {
        mainStage = stage;
        LoginController.loadStage();
    }

    public static void main(String[] args) throws RuntimeException {
        launch();
    }

    public static Stage getMainStage() {
        return mainStage;
    }
}