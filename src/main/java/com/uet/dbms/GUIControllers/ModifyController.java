package com.uet.dbms.GUIControllers;

import com.uet.dbms.Process.SQLiteJDBC;
import com.uet.dbms.Process.Word;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;

public class ModifyController {
    private static Word wordFound;

    public ModifyController(Word word) {
        wordFound = word;
    }

    public TextInputDialog createDialog(int option) { // 0 -> modify || 1 -> add new
        String title;
        if (option == 0) title = "Modification";
        else title = "Addition";
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        File iconFile = new File("src/main/resources/com/uet/dbms/icons/icon.png");
        stage.getIcons().add(new Image(iconFile.toURI().toString()));
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                if (windowEvent.getSource() == stage) {
                    System.out.println("close window");
                    stage.close();
                }
            }
        });


        Font font = new Font(18);

        TextField wordArea = new TextField();
        if (option == 0) wordArea.setText(wordFound.getTarget());
        else if (option == 1) wordArea.setPromptText("Word");
        wordArea.setFont(font);
        wordArea.setPrefWidth(310);
        wordArea.setPrefHeight(40);
        wordArea.setLayoutX(60);
        wordArea.setLayoutY(55);

        TextField pronounceArea = new TextField();
        if (option == 0) pronounceArea.setText(wordFound.getPronounce());
        else if (option == 1) pronounceArea.setPromptText("Pronunciation");
        pronounceArea.setFont(font);
        pronounceArea.setPrefWidth(155);
        pronounceArea.setPrefHeight(40);
        pronounceArea.setLayoutX(385);
        pronounceArea.setLayoutY(55);

        TextArea descriptionArea = new TextArea();
        if (option == 0) descriptionArea.setText(wordFound.getExplain());
        else if (option == 1) descriptionArea.setPromptText("Description");
        descriptionArea.setFont(font);
        descriptionArea.setPrefWidth(480);
        descriptionArea.setPrefHeight(200);
        descriptionArea.setLayoutX(60);
        descriptionArea.setLayoutY(115);

        Button saveButton = new Button("Save");
        saveButton.setFont(font);
        saveButton.setPrefWidth(160);
        saveButton.setPrefHeight(40);
        saveButton.setLayoutX(220);
        saveButton.setLayoutY(335);
        saveButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getSource() == saveButton) {
                    String newTarget = wordArea.getText();
                    String newPronounce = pronounceArea.getText();
                    String newDescription = descriptionArea.getText();
                    if (option == 0) {
                        if (newTarget.equals(wordFound.getTarget())
                                && newDescription.equals(wordFound.getExplain())
                                && newPronounce.equals(wordFound.getPronounce())) {

                        } else {
                            Word modWord = new Word(wordFound.getId(), newTarget, newDescription, newPronounce);
                            SQLiteJDBC.modifyWord(modWord);
                        }
                    } else if (option == 1) {
                        if (newTarget.isEmpty() && newDescription.isEmpty()) {

                        } else {
                            Word newWord = new Word(newTarget, newDescription, newPronounce);
                            SQLiteJDBC.insertWord(newWord);
                        }
                    }
                }
            }
        });

        File bgFile = new File("src/main/resources/com/uet/dbms/icons/ModifyBG.png");
        Image bg = new Image(bgFile.toURI().toString());
        ImageView image = new ImageView(bg);
        image.setFitWidth(600);
        image.setFitHeight(400);
        image.setPreserveRatio(false);
        image.setLayoutX(0);
        image.setLayoutY(0);

        Pane pane = new Pane();
        pane.setPrefWidth(600);
        pane.setPrefHeight(400);
        pane.getChildren().addAll(image, wordArea, pronounceArea, descriptionArea, saveButton);

        DialogPane dialogPane = new DialogPane();
        dialogPane.setContent(pane);
        dialog.setDialogPane(dialogPane);

        return dialog;
    }
}
