package com.ratdance;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StudyTimerApp {

    private final Stage stage;
    private double dragOffsetX, dragOffsetY;

    private TimerMode mode = TimerMode.STOPWATCH;
    private boolean isRunning = false;
    private int elapsedSeconds = 0;
    private int remainingSeconds = 0;
    private int targetSeconds = 0;

    private Timeline timeline;
    private ImageView characterView;

    private Label timeLabel;
    private Button startPauseButton;
    private HBox countdownInputBox;
    private TextField hoursField, minutesField, secondsField;

    public StudyTimerApp(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        buildTimeline();

        javafx.scene.layout.Pane root = buildScene();

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        String css = getClass().getResource("/com/ratdance/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                stage.close();
                Platform.exit();
            }
        });

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.setScene(scene);

        stage.show();

        loadGifFile();
    }

    private javafx.scene.layout.Pane buildScene() { return new HBox(); }
    private void buildTimeline() {}
    private void loadGifFile() {}
}
