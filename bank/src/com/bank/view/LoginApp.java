package com.bank.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.bank.model.AccountService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import com.bank.model.AccountService;
import com.bank.model.DatabaseConnectionTester;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginApp {

    public void showLoginWindow(Stage primaryStage) {
        // Główny kontener
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: #f5f5f5;");
        mainContainer.setAlignment(Pos.CENTER);

        // Panel logowania
        VBox loginPanel = new VBox(12);
        loginPanel.setStyle(
                "-fx-background-color: white;" +
                        "-fx-padding: 20px;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-background-radius: 5px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        loginPanel.setMaxWidth(300);

        // Tytuł
        Text titleText = new Text("Logowanie do systemu");
        titleText.setFont(Font.font("System", FontWeight.BOLD, 18));

        // Pola logowania
        Label loginLabel = new Label("Login:");
        TextField loginField = new TextField();
        loginField.setPromptText("Wprowadź login");
        loginField.setPrefHeight(30);

        Label passwordLabel = new Label("Hasło:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Wprowadź hasło");
        passwordField.setPrefHeight(30);

        // Przycisk logowania
        Button loginButton = new Button("Zaloguj się");
        loginButton.setPrefWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(35);
        loginButton.setStyle(
                "-fx-background-color: #2196f3;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        // Status połączenia z bazą
        HBox dbStatusBox = new HBox(10);
        dbStatusBox.setAlignment(Pos.CENTER);
        Circle statusCircle = new Circle(5);
        Label dbStatusLabel = new Label();
        dbStatusBox.getChildren().addAll(statusCircle, dbStatusLabel);

        // Sprawdzenie połączenia
        if (DatabaseConnectionTester.testConnection()) {
            statusCircle.setFill(Color.GREEN);
            dbStatusLabel.setText("Połączono z bazą danych");
        } else {
            statusCircle.setFill(Color.RED);
            dbStatusLabel.setText("Brak połączenia z bazą danych");
            loginButton.setDisable(true);
        }

        // Obsługa logowania
        loginButton.setOnAction(e -> {
            String login = loginField.getText();
            String password = passwordField.getText();

            if (login.isEmpty() || password.isEmpty()) {
                showAlert("Błąd", "Wypełnij wszystkie pola!");
                return;
            }

            String output = AccountService.login(login, password);
            System.out.println(output);

            if (output.contains("Login successful")) {
                Pattern pattern = Pattern.compile("(\\d+)$");
                Matcher matcher = pattern.matcher(output);
                if (matcher.find()) {
                    String accountNumber = matcher.group(1);
                    MainApp.setCurrent_login_account_number(Integer.parseInt(accountNumber));
                }
                primaryStage.close();
                MainApp mainApp = new MainApp();
                mainApp.showMainWindow();
            } else {
                showAlert("Błąd logowania", "Błędne hasło lub login");
            }
        });

        // Dodanie elementów do panelu logowania
        loginPanel.getChildren().addAll(
                titleText,
                new Separator(),
                loginLabel,
                loginField,
                passwordLabel,
                passwordField,
                loginButton,
                new Separator(),
                dbStatusBox
        );

        mainContainer.getChildren().add(loginPanel);

        Scene scene = new Scene(mainContainer, 400, 450);
        primaryStage.setTitle("Logowanie");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}