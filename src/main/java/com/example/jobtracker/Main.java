package com.example.jobtracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        stage.setTitle("Job Application Tracker");
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}