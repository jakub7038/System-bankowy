package com.bank.view;

import com.bank.view.admin.AdminApp;
import com.bank.view.user.UserApp;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.bank.repository.AccountRepository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import com.bank.repository.DatabaseConnectionTester;

import static com.bank.view.shared.ErrorAlert.showAlert;

public class LoginApp {

    public void showLoginWindow(Stage primaryStage) {
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

        Text titleText = new Text("Logowanie do systemu");
        titleText.setFont(Font.font("System", FontWeight.BOLD, 18));

        Label loginLabel = new Label("Login:");
        TextField loginField = new TextField();
        loginField.setPromptText("Wprowadź login");
        loginField.setPrefHeight(30);

        Label passwordLabel = new Label("Hasło:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Wprowadź hasło");
        passwordField.setPrefHeight(30);

        Button loginButton = new Button("Zaloguj się");
        loginButton.setPrefWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(35);
        loginButton.setStyle(
                "-fx-background-color: #2196f3;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        HBox dbStatusBox = new HBox(10);
        dbStatusBox.setAlignment(Pos.CENTER);
        Circle statusCircle = new Circle(5);
        Label dbStatusLabel = new Label();
        dbStatusBox.getChildren().addAll(statusCircle, dbStatusLabel);

        if (DatabaseConnectionTester.testConnection()) {
            statusCircle.setFill(Color.GREEN);
            dbStatusLabel.setText("Połączono z bazą danych");
        } else {
            statusCircle.setFill(Color.RED);
            dbStatusLabel.setText("Brak połączenia z bazą danych");
            loginButton.setDisable(true);
        }

        loginButton.setOnAction(e -> {
            String login = loginField.getText();
            String password = passwordField.getText();

            if (login.isEmpty() || password.isEmpty()) {
                showAlert("Błąd", "Wypełnij wszystkie pola!");
                return;
            }

            if (login.equals("admin") && password.equals("admin")) {
                primaryStage.close();
                AdminApp adminApp = new AdminApp();
                adminApp.showMainWindow();
                return;
            }


            String output = AccountRepository.login(login, password);
            System.out.println(output);
            if (output.contains("Login successful")) {
                Pattern pattern = Pattern.compile("(\\d+)$");
                Matcher matcher = pattern.matcher(output);
                if (matcher.find()) {
                    String accountNumber = matcher.group(1);
                    UserApp.setCurrent_login_account_number(accountNumber);
                }
                primaryStage.close();
                UserApp userApp = new UserApp();
                userApp.showMainWindow();
            } else {
                showAlert("Błąd logowania", "Błędne hasło lub login");
            }
        });

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

}