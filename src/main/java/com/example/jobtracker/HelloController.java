package com.example.jobtracker;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController
{
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick()
    {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
    @FXML
    protected void onClickMeButton() {welcomeText.setText("First button action!!");}
}