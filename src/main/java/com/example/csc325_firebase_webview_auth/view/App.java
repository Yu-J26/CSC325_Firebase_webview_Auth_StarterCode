package com.example.csc325_firebase_webview_auth.view;

import com.example.csc325_firebase_webview_auth.model.FirestoreContext;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import java.io.IOException;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {

    public static Firestore fstore;
    public static FirebaseAuth fauth;
    public static Scene scene;
    private final FirestoreContext contxtFirebase = new FirestoreContext();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage splashStage = new Stage();
        splashStage.initStyle(StageStyle.UNDECORATED);

        VBox splashRoot = new VBox(20);
        splashRoot.setAlignment(Pos.CENTER);
        splashRoot.setStyle("-fx-background-color: white; -fx-padding: 30;");

        Label titleLabel = new Label("Loading Application...");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(200);

        splashRoot.getChildren().addAll(titleLabel, progressBar);
        Scene splashScene = new Scene(splashRoot);
        splashStage.setScene(splashScene);
        splashStage.show();

        Task<Void> initTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                fstore = contxtFirebase.firebase();
                fauth = FirebaseAuth.getInstance();
                return null;
            }
        };
        initTask.setOnSucceeded(e -> {
            splashStage.close();
            try {
                scene = new Scene(loadFXML("/files/AccessFBView.fxml"));
                // Load CSS (style.css will be added in Commit 3)
                scene.getStylesheets().add(getClass().getResource("/files/style.css").toExternalForm());
                primaryStage.setScene(scene);
                primaryStage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        new Thread(initTask).start();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
