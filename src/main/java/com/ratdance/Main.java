package com.ratdance;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        new StudyTimerApp(primaryStage).show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
