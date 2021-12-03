package com.uet.dbms.GUIControllers;

import com.uet.dbms.Entities.Topic;
import com.uet.dbms.Main;
import com.uet.dbms.Process.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static javafx.scene.web.HTMLEditorSkin.Command.FONT_FAMILY;

public class HomeController {
    ////////////////////////// for back-end variables ///////////////
    private boolean[] shownInScene = { true, false, false };  // show: dictionary / text translation / user's favourite word
    private static List<Word> relatedWordList;
    private static Word wordFound;
    private static List<Topic> favouriteTopicList;
    private static boolean accountIsShowing = false;
    /////////////////////////////////////////////////////////////////

    ////////////////////////// for fxml variables ///////////////////
    @FXML private static final Image starIcon = new Image(Objects.requireNonNull(Main.class.getResourceAsStream("icons/starIcon.png")));
    @FXML private static final Image starMarkIcon = new Image(Objects.requireNonNull(Main.class.getResourceAsStream("icons/starMarkIcon.png")));

    @FXML private Pane rootPane;
    @FXML private Label usernameLabel;
    @FXML private Pane queryWordPane;
    @FXML private Pane favouriteWordPane;
    @FXML private Pane translateTextPane;

    @FXML private TextField searchArea;
    @FXML private TextArea wordArea;
    @FXML private TextArea pronunciationArea;
    @FXML private TextArea descriptionArea;
    @FXML private Pane queryOptionPane;
    @FXML private MenuButton favouriteMenu;
    @FXML private ListView<Text> relatedWordListView; // height on range(0, 31x14)
    @FXML private MenuItem newTopicOption;
    @FXML private ImageView userAvatar;

    @FXML private TextArea inputTextArea;
    @FXML private TextArea translatedTextAea;

    @FXML private ListView<Text> topicListView;
    @FXML private ListView<Text> wordsListView;
    /////////////////////////////////////////////////////////////////

    ////////////////////////// for UI processing methods ////////////
    //==================== menu bar set up ========================//
    @FXML
    private void menuBarOnMouseMoving() {
        if (rootPane.getLayoutY() == -150) {
            rootPane.setLayoutY(0);
        } else if (rootPane.getLayoutY() == 0) {
            rootPane.setLayoutY(-150);
        }
        if (!accountIsShowing) {
            setUsername();
            setUserAvatar();
            accountIsShowing = true;
        }
    }

    @FXML
    private void logOutButtonOnClicked() {
        Main.logOut();
        accountIsShowing = false;
        LoginController.loadStage();
    }

    @FXML
    private void changeAvatarButtonOnClicked() throws IOException {
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Change Avatar");
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            File iconFile = new File("src/main/resources/com/uet/dbms/icons/icon.png");
            stage.getIcons().add(new Image(iconFile.toURI().toString()));
            dialog.setHeaderText("Copy path to your image!");
            dialog.setContentText("Path:");
            dialog.showAndWait();
            String path = dialog.getEditor().getText();
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("File not exist!");
                return;
            }
            Image avatar = new Image(file.toURI().toString());
            userAvatar.setImage(avatar);
            // make a copy of avatar to project's resource
            String pathToImageFolder = "src/main/resources/com/uet/dbms/icons";
            String avatarFileName = Main.getUserAccount().getUsername() + "Avatar.png";
            String pathToAvatar = pathToImageFolder + "/" + avatarFileName;
            CopyImage.copy(path, pathToAvatar);
            Main.getUserAccount().setAvatarPath(pathToAvatar);
            SQLiteJDBC.changeAvatarPath(Main.getUserAccount().getId(), pathToAvatar);
        } catch (FileNotFoundException fe) {
            System.err.println(fe.getClass().getName() + ": " + fe.getMessage());
        }
    }

    @FXML
    private void dictionaryButtonOnClicked() {
        shownInScene[0] = true;
        shownInScene[1] = false;
        shownInScene[2] = false;
        wordFound = null;
        searchArea.setText("");
        wordArea.setText("");
        descriptionArea.setText("");
        pronunciationArea.setText("");
        queryOptionPane.setVisible(false);
        setMainScene();
    }

    @FXML
    private void textButtonOnClicked() {
        shownInScene[0] = false;
        shownInScene[1] = true;
        shownInScene[2] = false;
        inputTextArea.setText("");
        translatedTextAea.setText("");
        setMainScene();
    }

    @FXML
    private void favouriteButtonOnClicked() {
        shownInScene[0] = false;
        shownInScene[1] = false;
        shownInScene[2] = true;
        setMainScene();
        if (Main.getUserAccount().getId() != 0) createTopicGUI();
    }

    private void setMainScene() {
        // in case nothing is shown: set default scene is 'queryWordPane'
        if (!shownInScene[0] && !shownInScene[1] && !shownInScene[2]) {
            shownInScene[0] = true;
        }
        queryWordPane.setVisible(shownInScene[0]);
        translateTextPane.setVisible(shownInScene[1]);
        favouriteWordPane.setVisible(shownInScene[2]);
    }
    //=============================================================//
    //===================== query word set up =====================//
    @FXML
    private void searchAreaOnTyping() {
        String pattern = searchArea.getText();
        if (pattern.equals("")) {
            relatedWordListView.setVisible(false);
            return;
        }
        relatedWordListView.setVisible(true);
        if (relatedWordList != null) {
            relatedWordList.clear();
            relatedWordListView.getItems().clear();
        }
        relatedWordList = SQLiteJDBC.patternSearch(pattern);
        if (relatedWordList.isEmpty()) {
            relatedWordListView.setVisible(false);
            return;
        }
        relatedWordListView.setVisible(true);
        wordFound = relatedWordList.get(0); // set default value by the first in list
        relatedWordListView.setVisible(true);
        if (relatedWordList.size() >= 14) relatedWordListView.setPrefHeight(430);
        else relatedWordListView.setPrefHeight(relatedWordList.size() * 31);
        Font font = new Font(18);
        for (Word word : relatedWordList) {
            Text wordToText = new Text(word.getTarget());
            wordToText.setFont(font);
            relatedWordListView.getItems().add(wordToText);
        }
        relatedWordListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    private void favouriteMenuOnClicked() {
        if (Main.getUserAccount().getId() == 0) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Notification!");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            File iconFile = new File("src/main/resources/com/uet/dbms/icons/icon.png");
            stage.getIcons().add(new Image(iconFile.toURI().toString()));
            alert.setHeaderText("Please login to use this utilities!");
            alert.setContentText("Continue with 'Guest'?");
            alert.showAndWait();
        }
    }

    @FXML
    private void relatedWordListOnClicked() {
        if (relatedWordList.isEmpty()) return;
        Text selectedItem = relatedWordListView.getSelectionModel().getSelectedItem();
        int pos = relatedWordListView.getItems().indexOf(selectedItem);
        wordFound = relatedWordList.get(pos);
        searchArea.setText(wordFound.getTarget());
        wordArea.setText(wordFound.getTarget());
        descriptionArea.setText(wordFound.getExplain());
        pronunciationArea.setText(wordFound.getPronounce());
        queryOptionPane.setVisible(true);
        setFavouriteShowings();
    }

    @FXML
    private void searchButtonOnClicked() {
        String target = searchArea.getText();
        Word temp = SQLiteJDBC.targetSearch(target);
        relatedWordListView.setVisible(false);
        if (temp == null) {
            queryOptionPane.setVisible(false);
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Cannot find '" + searchArea.getText() + "' !");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            File iconFile = new File("src/main/resources/com/uet/dbms/icons/icon.png");
            stage.getIcons().add(new Image(iconFile.toURI().toString()));
            alert.setHeaderText("We can't find word like '" + searchArea.getText() + "'!");
            alert.setContentText("Do you want to add this word?");
            Optional<ButtonType> action = alert.showAndWait();
            if (action.isPresent() && action.get() == ButtonType.OK) {
                ModifyController mc = new ModifyController(wordFound);
                TextInputDialog dialog = mc.createDialog(1);
                dialog.showAndWait();
            }
        } else {
            wordFound = temp;
            wordArea.setText(wordFound.getTarget());
            descriptionArea.setText(wordFound.getExplain());
            pronunciationArea.setText(wordFound.getPronounce());
            queryOptionPane.setVisible(true);
            setFavouriteShowings();
        }
    }

    @FXML
    private void pronounceButtonOnClicked() {
        TextToSpeech.speak(wordFound.getTarget());
    }

    @FXML
    private void deleteWordButtonOnClicked() {
        if (!queryWordPane.isVisible()) return;
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete " + wordFound.getTarget());
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        File iconFile = new File("src/main/resources/com/uet/dbms/icons/icon.png");
        stage.getIcons().add(new Image(iconFile.toURI().toString()));
        alert.setHeaderText("You won't find this word in the future!");
        alert.setContentText("Continue?");
        Optional<ButtonType> action = alert.showAndWait();
        if (action.isPresent() && action.get() == ButtonType.OK) {
            SQLiteJDBC.deleteWord(wordFound.getId());
            // refresh window after deleting
            wordFound = null;
            wordArea.setText("");
            descriptionArea.setText("");
            pronunciationArea.setText("");
            queryOptionPane.setVisible(false);
        }
    }

    @FXML
    private void modifyWordButtonOnClicked() {
        if (!queryWordPane.isVisible()) return;
        // call to modify stage
        ModifyController mc = new ModifyController(wordFound);
        TextInputDialog dialog = mc.createDialog(0);
        dialog.showAndWait();
    }

    @FXML
    private void newWordButtonOnClicked() {
        // call to word-addition stage
        ModifyController mc = new ModifyController(wordFound);
        TextInputDialog dialog = mc.createDialog(1);
        dialog.showAndWait();
    }

    @FXML
    private void createNewTopicItem() {
        if (Main.getUserAccount().getId() == 0) {
            favouriteMenuOnClicked();
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Favourite Topic!");
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        File iconFile = new File("src/main/resources/com/uet/dbms/icons/icon.png");
        stage.getIcons().add(new Image(iconFile.toURI().toString()));
        dialog.setHeaderText("Create a new topic and add '" + wordFound + "'");
        dialog.setContentText("Topic name:");
        dialog.showAndWait();
        String topicName = dialog.getEditor().getText();
        if (topicName.isEmpty()) topicName = "Unnamed";
        // add a item to UI
        SQLiteJDBC.insertWordToFavourite(Main.getUserAccount().getId(), topicName, wordFound.getId());
        Topic newTopic = new Topic();
        newTopic.setContent(topicName);
        newTopic.getWordList().add(wordFound);
        if (!favouriteTopicList.contains(newTopic)) favouriteTopicList.add(newTopic);
        setFavouriteShowings();
    }

    private void setFavouriteShowings() {
        favouriteMenu.getItems().clear();
        favouriteMenu.getItems().add(newTopicOption);
        for (Topic topic : favouriteTopicList) {
//            System.out.println(topic);
            MenuItem topicItem = new MenuItem();
            topicItem.setText(topic.getContent());
            ImageView topicIcon = new ImageView();
            topicIcon.setFitHeight(25);
            topicIcon.setFitWidth(25);
            if (!topic.contains(wordFound)) topicIcon.setImage(starIcon);
            else topicIcon.setImage(starMarkIcon);
//            System.out.println(topic.contains(wordFound));
            topicItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (event.getSource() == topicItem) {
                        if (!topic.contains(wordFound)) {
                            SQLiteJDBC.insertWordToFavourite(Main.getUserAccount().getId(), topic.getContent(), wordFound.getId());
                            topic.getWordList().add(wordFound);
                            topicIcon.setImage(starMarkIcon);
                        } else {
                            SQLiteJDBC.deleteWordOnFavourite(Main.getUserAccount().getId(), topic.getContent(), wordFound.getId());
                            topic.getWordList().remove(wordFound);
                            topicIcon.setImage(starIcon);
                        }
                    }
                }
            });
            topicItem.setGraphic(topicIcon);
            favouriteMenu.getItems().add(topicItem);
        }
    }
    //=============================================================//

    //===================== text translation set up ===============//
    @FXML
    private void engToVieOnClicked() throws IOException {
        String text = inputTextArea.getText();
        if (text.isEmpty()) return;
        String result = GG_API_Translator.translate("en", "vi", text);
        translatedTextAea.setText(result);
    }

    @FXML
    private void vieToEngOnClicked() throws IOException {
        String text = inputTextArea.getText();
        if (text.isEmpty()) return;
        String result = GG_API_Translator.translate("vi", "en", text);
        translatedTextAea.setText(result);
    }
    //=============================================================//

    //===================== user's favourite word set up ==========//
    private void createTopicGUI() {
        topicListView.getItems().clear();
        wordsListView.getItems().clear();
        for (int i = 0; i < favouriteTopicList.size(); i++) {
            Topic topic = favouriteTopicList.get(i);
            Text renameOption = new Text("Rename");
            renameOption.setFont(Font.font(String.valueOf(FONT_FAMILY), FontPosture.ITALIC, 12));
            renameOption.setUnderline(true);
            renameOption.setVisible(false);
            int finalI = i;
            renameOption.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Rename");
                    Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
                    File iconFile = new File("src/main/resources/com/uet/dbms/icons/icon.png");
                    stage.getIcons().add(new Image(iconFile.toURI().toString()));
                    dialog.setHeaderText("Enter new name: ");
                    dialog.setContentText("Name: ");
                    dialog.showAndWait();
                    String name = dialog.getEditor().getText();
                    if (name.isEmpty()) name = "Unnamed";
                    SQLiteJDBC.changeTopicName(topic.getContent(), name);
                    topic.setContent(name);
                    topicListView.getItems().get(finalI * 3).setText(name);
                }
            });
            Text deleteOption = new Text("Delete\n");
            deleteOption.setFont(Font.font(String.valueOf(FONT_FAMILY), FontPosture.ITALIC, 12));
            deleteOption.setUnderline(true);
            deleteOption.setVisible(false);
            deleteOption.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete");
                    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                    File iconFile = new File("src/main/resources/com/uet/dbms/icons/icon.png");
                    stage.getIcons().add(new Image(iconFile.toURI().toString()));
                    alert.setHeaderText("You won't see this topic in the future!");
                    alert.setContentText("Continue?");
                    Optional<ButtonType> action = alert.showAndWait();
                    if (action.isPresent() && action.get() == ButtonType.OK) {
                        SQLiteJDBC.deleteTopic(topic.getContent(), Main.getUserAccount().getId());
                        favouriteTopicList.remove(finalI);
                        topicListView.getItems().remove(finalI * 3);
                        topicListView.getItems().remove(finalI * 3);
                        topicListView.getItems().remove(finalI * 3);
                    }
                }
            });
            Text text = new Text(topic.getContent());
            text.setFont(new Font(24));
            text.setWrappingWidth(150);
            text.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getSource() == text) {
                        createWordsGUI(topic);
                        for (int j = 0; j < topicListView.getItems().size(); j++) {
                            if (j % 3 == 0) continue;
                            topicListView.getItems().get(j).setVisible(false);
                        }
                        renameOption.setVisible(true);
                        deleteOption.setVisible(true);
                    }
                }
            });
            text.setFill(Color.CHOCOLATE);
            topicListView.getItems().add(text);
            topicListView.getItems().add(renameOption);
            topicListView.getItems().add(deleteOption);
        }
        if (wordsListView.getItems().isEmpty() && !favouriteTopicList.isEmpty())
            createWordsGUI(favouriteTopicList.get(0));
    }

    private void createWordsGUI(Topic topic) {
        wordsListView.getItems().clear();
        for (int i = 0; i < topic.getWordList().size(); i++) {
            Word word = topic.getWordList().get(i);
            Text text = new Text(word.getTarget() + ": " + word.getExplain());
            text.setWrappingWidth(550);
            text.setFont(new Font(24));
            text.setFill(Color.ROYALBLUE);
            text.setOnMouseClicked(new EventHandler<>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getSource() == text) {
                        dictionaryButtonOnClicked();
                        wordFound = word;
                        searchArea.setText(wordFound.getTarget());
                        wordArea.setText(wordFound.getTarget());
                        descriptionArea.setText(wordFound.getExplain());
                        pronunciationArea.setText(wordFound.getPronounce());
                        queryOptionPane.setVisible(true);
                        setFavouriteShowings();
                    }
                }
            });
            wordsListView.getItems().add(text);
            Text deleteOption = new Text("Delete");
            deleteOption.setFont(Font.font(String.valueOf(FONT_FAMILY), FontPosture.ITALIC, 12));
            deleteOption.setUnderline(true);
            int finalI = i;
            deleteOption.setOnMouseClicked(new EventHandler<>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getSource() == deleteOption) {
                        topic.getWordList().remove(word);
                        SQLiteJDBC.deleteWordOnFavourite(Main.getUserAccount().getId(), topic.getContent(), word.getId());
                        wordsListView.getItems().remove(finalI * 2);
                        wordsListView.getItems().remove(finalI * 2);
                    }
                }
            });
            wordsListView.getItems().add(deleteOption);
        }
    }
    //=============================================================//

    /////////////////////////////////////////////////////////////////

    ///////////////////////// for other processing methods //////////
    private void setUserAvatar() {
        String avtPath = Main.getUserAccount().getAvatarPath();
        if (avtPath.equals("")) {
            File file = new File("src/main/resources/com/uet/dbms/icons/avatarIcon.png");
            if (!file.exists()) System.err.println("Not found default avatar!");
            Image defaultAvatar = new Image(file.toURI().toString());
            userAvatar.setImage(defaultAvatar);
        } else {
            File file = new File(avtPath);
            if (!file.exists()) System.err.println("Cannot find image path for user avatar!");
            Image avatar = new Image(file.toURI().toString());
            userAvatar.setImage(avatar);
        }
    }

    private void setUsername() {
        usernameLabel.setText(Main.getUserAccount().getUsername());
    }
    /////////////////////////////////////////////////////////////////

    ///////////////////////// for main method ///////////////////////
    public static void loadStage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(Main.class.getResource("fxml/Home.fxml")));
            Scene scene = new Scene(fxmlLoader.load(), 900, 600);
            Stage stage = Main.getMainStage();
            stage.setTitle("My Dictionary");
            stage.getIcons().add(Main.getIcon());
            stage.setScene(scene);
            stage.show();
            // load all user favourite word into 'favouriteTopicList'
            if (Main.getUserAccount().getId() != 0) {
                favouriteTopicList = new ArrayList<>();
                List<String> topicList = SQLiteJDBC.queryFavouriteTopic(Main.getUserAccount().getId());
                for (String topicContent : topicList) {
                    favouriteTopicList.add(new Topic(topicContent));
                    System.out.println(topicContent);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + e.getMessage());
        }
    }
    /////////////////////////////////////////////////////////////////
}

