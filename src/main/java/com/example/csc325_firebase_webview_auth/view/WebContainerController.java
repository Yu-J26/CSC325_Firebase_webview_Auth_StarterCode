/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.example.csc325_firebase_webview_auth.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class
 *
 * @author MoaathAlrajab
 */
public class WebContainerController implements Initializable {
    Document doc;
    private DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @FXML
    Label label;

    @FXML
    WebView webView;
    private WebEngine webEngine;

    @FXML
    private void goAction(ActionEvent evt) {
        webEngine.load("http://google.com");
    }

    @FXML
    private void setLabel(ActionEvent e){
        System.out.println("H1");
        doc.getElementById("ueberschr").setAttribute("value", "Red");
    }

    @FXML
    private void swithcBackStage(ActionEvent e){
        try {
            App.setRoot("/files/AccessFBView.fxml");
        } catch (IOException ex) {
            Logger.getLogger(WebContainerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            webEngine = webView.getEngine();
            // Load the HTML file (ensure it's in src/main/resources/files/)
            webEngine.load(getClass().getResource("/files/newhtml.html").toExternalForm());

            webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
                @Override
                public void changed(ObservableValue<? extends State> ov, State t, State newState) {
                    if (newState == State.SUCCEEDED) {
                        doc = webEngine.getDocument();
                        JSObject jsobj = (JSObject) webEngine.executeScript("window");
                        jsobj.setMember("app12", new Bridge());
                        jsobj.setMember("enigma", new EnigmaBridge()); // ADD THIS LINE
                    }
                }
            });
            webView.setContextMenuEnabled(false);
            webEngine.setJavaScriptEnabled(true);
        } catch (Exception ex) {
            Logger.getLogger(WebContainerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public class Bridge {
        public void showTime() {
            System.out.println("Show Time");
            label.setText("Now is: " + df.format(new Date()));
        }
    }

    // Add this inner class
    public class EnigmaBridge {
        public void checkSolution(String input) {
            System.out.println("Checking solution: " + input);
            if ("example".equals(input)) {
                webEngine.executeScript("document.getElementById('out').className = 'enabled';");
            } else {
                webEngine.executeScript("document.getElementById('out').className = '';");
            }
        }
    }
}
