package com.uet.dbms.GUIControllers;

import com.uet.dbms.Main;
import com.uet.dbms.Process.SQLiteJDBC;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;

public class RegisterController {
    private static boolean[] ableToRegister = { false, false, false };
    private static List<String> usernameList;

    ///////////////////////////////// for fxml variables ////////////////////////////////
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmField;

    @FXML
    private ImageView checkUsernameImage;

    @FXML
    private ImageView checkPasswordImage;

    @FXML
    private ImageView checkConfirmImage;

    @FXML
    private static Stage stage;

    @FXML
    private static final Image VMarkIcon = new Image(Objects.requireNonNull(Main.class.getResourceAsStream("icons/VMarkIcon.png")));;

    @FXML
    private static final Image XMarkIcon = new Image(Objects.requireNonNull(Main.class.getResourceAsStream("icons/XMarkIcon.png")));;
    /////////////////////////////////////////////////////////////////////////////////////

    @FXML
    private void usernameFieldOnTyping() throws RuntimeException {
        System.out.println(1);
        String username = usernameField.getText();
        System.out.println(username);
        if (username.equals("") || checkExistedUsername(username)) {
            System.out.println("Existed Username!");
            ableToRegister[0] = false;
            checkUsernameImage.setImage(XMarkIcon);
        } else {
            ableToRegister[0] = true;
            checkUsernameImage.setImage(VMarkIcon);
        }
    }

    @FXML
    private void confirmFieldOnTyping() throws RuntimeException {
        System.out.println(3);
        String confirm = confirmField.getText();
        String password = passwordField.getText();
        System.out.println(password + "|" + confirm);
        if (password.equals("")) {
            ableToRegister[1] = false;
            checkPasswordImage.setImage(XMarkIcon);
        } else {
            ableToRegister[1] = true;
            checkPasswordImage.setImage(VMarkIcon);
        }
        if (confirm.equals("") || !confirm.equals(password)) {
            ableToRegister[2] = false;
            checkConfirmImage.setImage(XMarkIcon);
        } else {
            ableToRegister[2] = true;
            checkConfirmImage.setImage(VMarkIcon);
        }
    }

    @FXML
    private void registerButtonOnClick() {
        if (ableToRegister[0] && ableToRegister[1] && ableToRegister[2]) {
            String username = usernameField.getText();
            String password = passwordField.getText();
            SQLiteJDBC.insertAccount(username, password);
            stage.close();
        }
    }

    public static void loadStage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(Main.class.getResource("fxml/Register.fxml")));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            stage = new Stage();
            stage.setTitle("Register");
            stage.getIcons().add(Main.getIcon());
            stage.setScene(scene);
            stage.show();
            usernameList = SQLiteJDBC.getAllUsername();
        } catch (Exception e) {
            System.err.println("Error in loading register window!");
        }
    }

    private boolean checkExistedUsername(String username) {
        return usernameList.contains(username);
    }
}
