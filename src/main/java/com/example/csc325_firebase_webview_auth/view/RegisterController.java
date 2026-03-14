package com.example.csc325_firebase_webview_auth.view;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

public class RegisterController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleRegister() {
        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(emailField.getText())
                    .setPassword(passwordField.getText())
                    .setDisplayName(nameField.getText());
            UserRecord userRecord = App.fauth.createUser(request);
            System.out.println("Created user: " + userRecord.getUid());

            Map<String, Object> userData = new HashMap<>();
            userData.put("name", nameField.getText());
            userData.put("email", emailField.getText());
            App.fstore.collection("Users").document(userRecord.getUid()).set(userData);

            ((Stage) nameField.getScene().getWindow()).close();
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
        }
    }
}
