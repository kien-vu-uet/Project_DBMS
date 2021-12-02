package com.uet.dbms.GUIControllers;

import com.uet.dbms.Entities.Account;
import com.uet.dbms.Main;
import com.uet.dbms.Process.SQLiteJDBC;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Objects;

public class LoginController {

    ///////////////////////////////// for fxml variables ////////////////////////////////
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;
    /////////////////////////////////////////////////////////////////////////////////////

    @FXML
    private void loginAsGuestButtonOnClick() throws RuntimeException {
        Main.setUserAccount(new Account(0, "Guest", "", ""));
        System.out.println("Login as Guest!");
        goToHome();
    }

    @FXML
    private void registerButtonOnClick() throws RuntimeException {
        RegisterController.loadStage();
        refreshTextField();
    }

    @FXML
    private void loginButtonOnClick() throws RuntimeException {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();
//            System.out.println(username + "\n" + password);
            int id = SQLiteJDBC.checkLoginAccount(username, password);
            if (id == -1) {
                System.out.println("Invalid account!");
                messageLabel.setText("Invalid username or password");
            } else {
                System.out.println("Login successfully!");
                Main.setUserAccount(SQLiteJDBC.getAccountByID(id));
                goToHome();
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private void refreshTextField() {
        usernameField.setText("");
        passwordField.setText("");
    }

    public static void loadStage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(Main.class.getResource("fxml/Login.fxml")));
            Scene scene = new Scene(fxmlLoader.load(), 900, 600);
            Stage stage = Main.getMainStage();
            stage.setTitle("Login");
            stage.getIcons().add(Main.getIcon());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void goToHome() {
        if (Main.isLoggedIn()) {
            HomeController.loadStage();
        }
        System.out.println("launch home scene!");
    }
}