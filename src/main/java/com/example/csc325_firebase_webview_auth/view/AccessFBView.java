package com.example.csc325_firebase_webview_auth.view;//package modelview;

import com.example.csc325_firebase_webview_auth.model.Person;
import com.example.csc325_firebase_webview_auth.viewmodel.AccessDataViewModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.converter.NumberStringConverter;

import com.google.cloud.storage.*;
import java.io.File;
import java.nio.file.Files;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AccessFBView {


    @FXML
    private TextField nameField;
    @FXML
    private TextField majorField;
    @FXML
    private TextField ageField;
    @FXML
    private Button writeButton;
    @FXML
    private Button readButton;
    @FXML
    private TextArea outputField;

    @FXML
    private TableView<Person> personTable;
    @FXML
    private TableColumn<Person, String> nameColumn;
    @FXML
    private TableColumn<Person, String> majorColumn;
    @FXML
    private TableColumn<Person, Integer> ageColumn;

    @FXML private ImageView profileImageView;

    private ObservableList<Person> listOfUsers = FXCollections.observableArrayList();
    private Person person;

    public ObservableList<Person> getListOfUsers() {
        return listOfUsers;
    }

    void initialize() {
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        majorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMajor()));
        ageColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAge()).asObject());
        personTable.setItems(listOfUsers);

        AccessDataViewModel accessDataViewModel = new AccessDataViewModel();
        nameField.textProperty().bindBidirectional(accessDataViewModel.userNameProperty());
        majorField.textProperty().bindBidirectional(accessDataViewModel.userMajorProperty());
        ageField.textProperty().bindBidirectional(accessDataViewModel.ageProperty(), new NumberStringConverter());
        writeButton.disableProperty().bind(accessDataViewModel.isWritePossibleProperty().not());
    }

    @FXML
    private void addRecord(ActionEvent event) {
        addData();
    }

    @FXML
    private void readRecord(ActionEvent event) {
        readFirebase();
    }

    @FXML
    private void regRecord(ActionEvent event) {
        registerUser();
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("/files/WebContainer.fxml");
    }

    public void addData() {

        DocumentReference docRef = App.fstore.collection("References").document(UUID.randomUUID().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("Name", nameField.getText());
        data.put("Major", majorField.getText());
        data.put("Age", Integer.parseInt(ageField.getText()));
        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
    }

    public boolean readFirebase() {
        listOfUsers.clear();
        ApiFuture<QuerySnapshot> future = App.fstore.collection("References").get();
        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                String name = document.getString("Name");
                String major = document.getString("Major");
                Long ageLong = document.getLong("Age");
                int age = ageLong != null ? ageLong.intValue() : 0;
                Person person = new Person(document.getId(), name, major, age);
                listOfUsers.add(person);
            }
            return true;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void sendVerificationEmail() {
        try {
            UserRecord user = App.fauth.getUser("name");
            //String url = user.getPassword();

        } catch (Exception e) {
        }
    }

    public boolean registerUser() {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail("user@example.com")
                .setEmailVerified(false)
                .setPassword("secretPassword")
                .setPhoneNumber("+11234567890")
                .setDisplayName("John Doe")
                .setDisabled(false);

        UserRecord userRecord;
        try {
            userRecord = App.fauth.createUser(request);
            System.out.println("Successfully created new user: " + userRecord.getUid());
            return true;

        } catch (FirebaseAuthException ex) {
            ex.printStackTrace();
            return false;
        }

    }

    @FXML
    private void showLogin() throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/files/Login.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void showRegister() throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/files/Register.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("My Application");
        alert.setContentText("This is a Firebase demo app.");
        alert.showAndWait();
    }

    @FXML
    private void handleDelete() {
        Person selected = personTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            App.fstore.collection("References").document(selected.getId()).delete();
            listOfUsers.remove(selected);
        }
    }

    @FXML
    private void handleUpdate() {
        Person selected = personTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("Name", nameField.getText());
            updates.put("Major", majorField.getText());
            updates.put("Age", Integer.parseInt(ageField.getText()));
            App.fstore.collection("References").document(selected.getId()).update(updates);
            readFirebase(); // refresh table
        }
    }
    @FXML
    private void handleUploadPicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(profileImageView.getScene().getWindow());
        if (file != null) {
            uploadToStorage(file);
        }
    }

    private void uploadToStorage(File file) {
        // Replace with actual user ID after login (you can store it in a static variable)
        String userId = "current_user_id"; // TODO: replace with real user ID from login

        try {
            // Your Firebase Storage bucket name (from Firebase Console)
            String bucketName = "csc325yu.appspot.com";
            Storage storage = StorageOptions.getDefaultInstance().getService();
            String blobName = "profile_pictures/" + userId + ".jpg";
            BlobId blobId = BlobId.of(bucketName, blobName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            byte[] content = Files.readAllBytes(file.toPath());
            storage.create(blobInfo, content);
            String fileUrl = "https://storage.googleapis.com/" + bucketName + "/" + blobName;

            // Update Firestore user document (adjust collection/document as needed)
            App.fstore.collection("Users").document(userId).update("profilePicUrl", fileUrl);

            // Display the uploaded image
            Image image = new Image(fileUrl);
            profileImageView.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
