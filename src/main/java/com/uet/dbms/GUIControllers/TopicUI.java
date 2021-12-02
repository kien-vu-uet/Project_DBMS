package com.uet.dbms.GUIControllers;

import com.uet.dbms.Entities.Topic;
import com.uet.dbms.Main;
import com.uet.dbms.Process.SQLiteJDBC;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TopicUI {
    private static List<Topic> topicList;
    private static List<Pane> topicPaneList;
    private static List<List<Pane>> wordPaneList;
    private static Pane topicBox;
    private static Pane wordBox;


    public TopicUI(List<Topic> list) {
        topicList = list;
        topicPaneList = new ArrayList<>();
        wordPaneList = new ArrayList<>();
        FXMLLoader root1 = new FXMLLoader(Objects.requireNonNull(Main.class.getResource("fxml/TopicBox.fxml")));
        FXMLLoader root2 = new FXMLLoader(Objects.requireNonNull(Main.class.getResource("fxml/WordBox.fxml")));
        try {
            topicBox = root1.load();
            wordBox = root2.load();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void createTopicUI() {
        List<String> color = new ArrayList<>(Arrays.asList("#f08080", "#90ee90",
                "#8fbc8f", "#00ced1", "#f08080", "#228b22",
                "#ffa07a", "#32cd32", "#3cb371", "#ff4500", "#ffff00"));
        for (int i = 0; i < topicList.size(); i++) {
            Topic topic = topicList.get(i);
            Pane topicPane = getCopyFrom(topicBox);
            topicPane.setStyle("-fx-background-color: " + color.get(new Random().nextInt(color.size()) % color.size()));

            Label topicLabel = (Label) topicPane.getChildren().get(0);
            topicLabel.setText(topic.getContent());
            ((Button) topicPane.getChildren().get(1)).setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
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
                    topicLabel.setText(name);
                    topic.setContent(name);
                }
            });

            int finalI = i;
            ((Button) topicPane.getChildren().get(2)).setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete");
                    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                    File iconFile = new File("src/main/resources/com/uet/dbms/icons/icon.png");
                    stage.getIcons().add(new Image(iconFile.toURI().toString()));
                    alert.setHeaderText("You won't see this topic in the future!");
                    alert.setContentText("Continue?");
                    Optional<ButtonType> action = alert.showAndWait();
                    if(!action.isPresent()) return;
                    else {
                        if (action.get() == ButtonType.CANCEL) return;
                        if (action.get() == ButtonType.OK) {
                            SQLiteJDBC.deleteTopic(topic.getContent(), Main.getUserAccount().getId());
                            topicList.remove(finalI);
                            topicPaneList.remove(finalI);
                            wordPaneList.remove(finalI);
                        }
                    }
                }
            });


        }
    }

    private Pane getCopyFrom(Pane pane) {
        Pane res = new Pane();
        res.setPrefSize(pane.getWidth(), pane.getHeight());
        for (Node node : pane.getChildren()) {
            if (node instanceof Label) {
                Label label = new Label();
                label.setFont(new Font(18));
                label.setLayoutX(node.getLayoutX());
                label.setLayoutY(node.getLayoutY());
                res.getChildren().add(label);
            } else if (node instanceof Button) {
                Button button = new Button();
                button.setText(((Button) node).getText());
                button.setLayoutX(node.getLayoutX());
                button.setLayoutY(node.getLayoutY());
                res.getChildren().add(button);
            }
        }
        return res;
    }
}
