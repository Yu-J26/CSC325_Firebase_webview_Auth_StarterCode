package com.example.csc325_firebase_webview_auth.view;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        System.out.println("Login attempted: " + emailField.getText());
        ((Stage) emailField.getScene().getWindow()).close();
    }
}
