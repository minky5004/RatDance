package com.ratdance;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.util.prefs.Preferences;

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

    private javafx.scene.layout.Pane buildScene() {
        // Character display area (GIF fills this ImageView)
        characterView = new ImageView();
        characterView.setPreserveRatio(true);
        characterView.setFitWidth(320);
        characterView.setFitHeight(350);
        characterView.setId("character-view");
        setupWindowDrag(characterView);
        setupCharacterContextMenu(characterView);

        // Title bar (for dragging)
        HBox titleBar = new HBox();
        titleBar.setId("title-bar");
        titleBar.setPrefHeight(15);
        setupWindowDrag(titleBar);

        // Mode toggle
        ToggleGroup modeGroup = new ToggleGroup();
        ToggleButton stopwatchToggle = new ToggleButton("타이머");
        ToggleButton countdownToggle = new ToggleButton("카운트다운");
        stopwatchToggle.setToggleGroup(modeGroup);
        countdownToggle.setToggleGroup(modeGroup);
        stopwatchToggle.setSelected(true);
        stopwatchToggle.setOnAction(e -> switchMode(TimerMode.STOPWATCH));
        countdownToggle.setOnAction(e -> switchMode(TimerMode.COUNTDOWN));

        HBox modeToggle = new HBox(8, stopwatchToggle, countdownToggle);
        modeToggle.setAlignment(Pos.CENTER);

        // Time display
        timeLabel = new Label("00:00");
        timeLabel.setId("time-display");
        setupWindowDrag(timeLabel);

        // Countdown H:M:S input
        hoursField = createTimeField("0");
        minutesField = createTimeField("0");
        secondsField = createTimeField("0");

        Label colon1 = new Label(":");
        colon1.setId("colon-label");
        Label colon2 = new Label(":");
        colon2.setId("colon-label");

        countdownInputBox = new HBox(6, hoursField, colon1, minutesField, colon2, secondsField);
        countdownInputBox.setAlignment(Pos.CENTER);
        countdownInputBox.setVisible(false);
        countdownInputBox.setManaged(false);

        // Control buttons with icons
        startPauseButton = new Button("▶");
        startPauseButton.setId("play-button");
        startPauseButton.getStyleClass().add("control-button");
        startPauseButton.setOnAction(e -> {
            if (isRunning) pauseTimer();
            else startTimer();
        });

        Button resetButton = new Button("⏹");
        resetButton.setId("stop-button");
        resetButton.getStyleClass().add("control-button");
        resetButton.setOnAction(e -> resetTimer());

        HBox controls = new HBox(12, startPauseButton, resetButton);
        controls.setAlignment(Pos.CENTER);

        // Control panel (transparent, sits to the right of the character)
        VBox controlPanel = new VBox(4, titleBar, modeToggle, timeLabel, countdownInputBox, controls);
        controlPanel.setId("control-panel");
        controlPanel.setAlignment(Pos.TOP_CENTER);
        controlPanel.setPadding(new Insets(6, 8, 8, 8));
        controlPanel.setPrefWidth(150);
        controlPanel.setStyle("-fx-background-color: transparent;");

        // Spacer to push panel down
        VBox spacer = new VBox();
        spacer.setStyle("-fx-background-color: transparent;");

        // Wrapper to position panel at bottom
        VBox panelWrapper = new VBox(spacer, controlPanel);
        VBox.setVgrow(spacer, Priority.ALWAYS);
        panelWrapper.setStyle("-fx-background-color: transparent;");

        // Root: GIF on left, panel on right
        HBox root = new HBox(0, characterView, panelWrapper);
        root.setAlignment(Pos.TOP_LEFT);
        root.setStyle("-fx-background-color: transparent;");

        return root;
    }

    private TextField createTimeField(String defaultVal) {
        TextField field = new TextField(defaultVal);
        field.getStyleClass().add("time-field");
        field.setPrefWidth(50);
        field.setAlignment(Pos.CENTER);
        field.setTextFormatter(new TextFormatter<>(c ->
            c.getControlNewText().matches("\\d{0,2}") ? c : null));
        return field;
    }

    private void setupCharacterContextMenu(ImageView view) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem changeGif = new MenuItem("🎨 GIF 변경");
        changeGif.setOnAction(e -> {
            File chosen = showFileChooserDialog();
            if (chosen != null) initCharacter(chosen);
        });
        contextMenu.getItems().add(changeGif);
        view.setOnContextMenuRequested(e ->
            contextMenu.show(view, e.getScreenX(), e.getScreenY()));
    }

    private void setupWindowDrag(javafx.scene.Node node) {
        node.setOnMousePressed(e -> {
            dragOffsetX = e.getScreenX() - stage.getX();
            dragOffsetY = e.getScreenY() - stage.getY();
        });
        node.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - dragOffsetX);
            stage.setY(e.getScreenY() - dragOffsetY);
        });
    }


    private void switchMode(TimerMode newMode) {
        resetTimer();
        mode = newMode;
        boolean isCountdown = (newMode == TimerMode.COUNTDOWN);
        countdownInputBox.setVisible(isCountdown);
        countdownInputBox.setManaged(isCountdown);
        timeLabel.setText(formatTime(0));
    }

    private void buildTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (mode == TimerMode.STOPWATCH) {
                elapsedSeconds++;
                timeLabel.setText(formatTime(elapsedSeconds));
            } else {
                remainingSeconds--;
                timeLabel.setText(formatTime(Math.max(0, remainingSeconds)));
                if (remainingSeconds <= 0) {
                    timeline.stop();
                    isRunning = false;
                    startPauseButton.setText("▶");
                    startPauseButton.setStyle("-fx-text-fill: #4CAF50;");
                    onCountdownComplete();
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void startTimer() {
        if (mode == TimerMode.COUNTDOWN && !isRunning) {
            targetSeconds = parseCountdownInput();
            if (targetSeconds <= 0) return;
            remainingSeconds = targetSeconds;
            timeLabel.setText(formatTime(remainingSeconds));
        }
        timeline.play();
        isRunning = true;
        startPauseButton.setText("⏸");
        startPauseButton.setStyle("-fx-text-fill: white;");
    }

    private void pauseTimer() {
        timeline.pause();
        isRunning = false;
        startPauseButton.setText("▶");
        startPauseButton.setStyle("-fx-text-fill: #4CAF50;");
    }

    private void resetTimer() {
        timeline.stop();
        isRunning = false;
        elapsedSeconds = 0;
        remainingSeconds = targetSeconds;
        timeLabel.setText(formatTime(mode == TimerMode.STOPWATCH ? 0 : targetSeconds));
        startPauseButton.setText("▶");
        startPauseButton.setStyle("-fx-text-fill: #4CAF50;");
    }

    private int parseCountdownInput() {
        try {
            int h = Integer.parseInt(hoursField.getText().isEmpty() ? "0" : hoursField.getText());
            int m = Integer.parseInt(minutesField.getText().isEmpty() ? "0" : minutesField.getText());
            int s = Integer.parseInt(secondsField.getText().isEmpty() ? "0" : secondsField.getText());
            return h * 3600 + m * 60 + s;
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private String formatTime(int totalSeconds) {
        int h = totalSeconds / 3600;
        int m = (totalSeconds % 3600) / 60;
        int s = totalSeconds % 60;
        if (h > 0) return String.format("%d:%02d:%02d", h, m, s);
        else       return String.format("%02d:%02d", m, s);
    }

    private void loadGifFile() {
        // JAR 위치 기준 및 현재 작업 디렉토리 기준 두 경로 모두 탐색
        File gif = resolveDefaultGif();
        if (gif != null) {
            initCharacter(gif);
            return;
        }

        String savedPath = Preferences.userNodeForPackage(StudyTimerApp.class)
                                      .get("lastGifPath", null);
        if (savedPath != null) {
            File savedFile = new File(savedPath);
            if (savedFile.exists()) {
                initCharacter(savedFile);
                return;
            }
        }
    }

    private File resolveDefaultGif() {
        // 1순위: 현재 작업 디렉토리 기준
        File cwd = new File("characters/mouse_dance.gif");
        if (cwd.exists()) return cwd;

        // 2순위: JAR/클래스 파일 위치 기준
        try {
            File jarDir = new File(StudyTimerApp.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).getParentFile();
            File jarRelative = new File(jarDir, "characters/mouse_dance.gif");
            if (jarRelative.exists()) return jarRelative;
        } catch (Exception ignored) {}

        return null;
    }

    private void initCharacter(File gifFile) {
        try {
            Image image = new Image(gifFile.toURI().toString());
            characterView.setImage(image);
            Preferences.userNodeForPackage(StudyTimerApp.class)
                       .put("lastGifPath", gifFile.getAbsolutePath());
        } catch (Exception ex) {
            System.err.println("GIF load failed: " + ex.getMessage());
        }
    }

    private File showFileChooserDialog() {
        FileChooser fc = new FileChooser();
        fc.setTitle("GIF 캐릭터 선택");
        fc.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("GIF 파일", "*.gif")
        );
        return fc.showOpenDialog(stage);
    }

    private void onCountdownComplete() {
        String originalStyle = timeLabel.getStyle();
        timeLabel.setStyle("-fx-text-fill: #FF4444;");
        PauseTransition flash = new PauseTransition(Duration.seconds(2));
        flash.setOnFinished(e -> timeLabel.setStyle(originalStyle));
        flash.play();
        java.awt.Toolkit.getDefaultToolkit().beep();
    }
}
