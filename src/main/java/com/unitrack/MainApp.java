package com.unitrack;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    private final TaskService taskService = new TaskService();
    private final NoteService noteService = new NoteService();
    private final TimetableService timetableService = new TimetableService();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("UniTrack");

        TabPane tabPane = new TabPane();

        tabPane.getTabs().add(new Tab("Dashboard", getDashboard()));
        tabPane.getTabs().add(new Tab("To-Do List", getTodoList()));
        tabPane.getTabs().add(new Tab("Notes", getNotesPane()));
        tabPane.getTabs().add(new Tab("GPA Calculator", getGpaCalc()));
        tabPane.getTabs().add(new Tab("Motivation", getMotivation()));
        tabPane.getTabs().add(new Tab("Timetable", getTimetablePane()));
        tabPane.getTabs().add(new Tab("Settings", getSettings()));

        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox getDashboard() {
    VBox layout = new VBox(10);
    layout.setPadding(new Insets(20));
    layout.setAlignment(Pos.CENTER); // Center everything inside the VBox

    Label welcome = new Label("Welcome to UniTrack");
    Label info = new Label("Organize your academic life the smart way!");

    welcome.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
    info.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");

    layout.getChildren().addAll(welcome, info);
    return layout;
}


    private VBox getTodoList() {
        VBox listBox = new VBox(10);
        listBox.setPadding(new Insets(20));
        Label header = new Label("To-Do List:");

        List<CheckBox> checkBoxes = new ArrayList<>();
        for (Task task : taskService.getTasks()) {
            CheckBox cb = createTaskCheckBox(task);
            checkBoxes.add(cb);
        }

        TextField newTaskField = new TextField();
        newTaskField.setPromptText("Enter new task");

        Button addBtn = new Button("Add Task");
        addBtn.setOnAction(e -> {
            String text = newTaskField.getText().trim();
            if (!text.isEmpty()) {
                Task newTask = new Task(text);
                taskService.addTask(newTask);
                listBox.getChildren().add(createTaskCheckBox(newTask));
                newTaskField.clear();
            }
        });

        listBox.getChildren().addAll(header);
        listBox.getChildren().addAll(checkBoxes);
        listBox.getChildren().addAll(newTaskField, addBtn);

        return listBox;
    }

    private CheckBox createTaskCheckBox(Task task) {
        CheckBox cb = new CheckBox(task.getName());
        cb.setSelected(task.isDone());
        cb.setStyle(cb.isSelected() ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        cb.setOnAction(e -> {
            task.setDone(cb.isSelected());
            cb.setStyle(cb.isSelected() ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
            taskService.updateTask(task);
        });
        return cb;
    }

    private VBox getNotesPane() {
        VBox notesBox = new VBox(10);
        notesBox.setPadding(new Insets(20));
        TextArea notesArea = new TextArea(noteService.loadNote());
        notesArea.setPrefHeight(500);

        Button saveBtn = new Button("Save Notes");
        saveBtn.setOnAction(e -> noteService.saveNote(notesArea.getText()));

        notesBox.getChildren().addAll(notesArea, saveBtn);
        return notesBox;
    }

    private VBox getGpaCalc() {
        VBox gpaBox = new VBox(10);
        gpaBox.setPadding(new Insets(20));

        Label label = new Label("GPA Calculator (out of 10):");
        TextField[] marksFields = new TextField[6];
        for (int i = 0; i < 6; i++) {
            marksFields[i] = new TextField();
            marksFields[i].setPromptText("Subject " + (i + 1) + " marks");
        }

        Button calculate = new Button("Calculate GPA");
        Label result = new Label();

        calculate.setOnAction(e -> {
            try {
                double sum = 0;
                int count = 0;
                for (TextField field : marksFields) {
                    String text = field.getText().trim();
                    if (!text.isEmpty()) {
                        sum += Double.parseDouble(text);
                        count++;
                    }
                }
                if (count > 0) {
                    double avg = sum / count;
                    result.setText("Your GPA is: " + String.format("%.2f", avg));
                } else {
                    result.setText("Please enter at least one mark.");
                }
            } catch (Exception ex) {
                result.setText("Invalid input! Enter numbers only.");
            }
        });

        gpaBox.getChildren().add(label);
        gpaBox.getChildren().addAll(marksFields);
        gpaBox.getChildren().addAll(calculate, result);
        return gpaBox;
    }

    private VBox getMotivation() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        Label quote = new Label(noteService.getMotivationQuote());
        box.getChildren().add(quote);
        return box;
    }

    private VBox getTimetablePane() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        Label title = new Label("Timetable:");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333;");
        title.setPadding(new Insets(0, 0, 10, 0));

        TextField subjectField = new TextField();
        subjectField.setPromptText("Subject Name");
        TextField timeField = new TextField();
        timeField.setPromptText("Time (e.g., Mon 9:00-10:00)");
        subjectField.setPrefWidth(200);
        timeField.setPrefWidth(200);

        Button add = new Button("Add Entry");
        VBox entriesBox = new VBox(5);
        timetableService.getTimetable().forEach(entry -> entriesBox.getChildren().add(new Label(entry)));

        add.setOnAction(e -> {
            String subject = subjectField.getText();
            String time = timeField.getText();
            if (!subject.isEmpty() && !time.isEmpty()) {
                String entry = subject + " - " + time;
                timetableService.addEntry(entry);
                entriesBox.getChildren().add(new Label(entry));
                subjectField.clear();
                timeField.clear();
            }
        });

        box.getChildren().addAll(title, entriesBox, subjectField, timeField, add);
        return box;
    }

    private VBox getSettings() {
        VBox settingsBox = new VBox(10);
        settingsBox.setPadding(new Insets(20));
        Label label = new Label("Settings");
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        CheckBox darkMode = new CheckBox("Enable Dark Mode");
        darkMode.setOnAction(e -> {
            if (darkMode.isSelected()) {
                settingsBox.getScene().getRoot().setStyle("-fx-background-color: #333;");
                label.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
            } else {
                settingsBox.getScene().getRoot().setStyle("-fx-background-color: white;");
                label.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 16px;");
            }
        });

        Label comingSoon = new Label("More features coming soon!");
        comingSoon.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
        comingSoon.setPadding(new Insets(10, 0, 0, 0));

        settingsBox.getChildren().addAll(label, darkMode, comingSoon);
        return settingsBox;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

