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

public class LoginApp {

    public void showLoginWindow(Stage primaryStage) {
        Text loginText = new Text("Login:");
        Text passwordText = new Text("Hasło:");

        TextField loginField = new TextField();
        PasswordField passwordField = new PasswordField();

        Button loginButton = new Button("Zaloguj się");

        Text errorText = new Text();

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        grid.add(loginText, 0, 0);
        grid.add(loginField, 1, 0);
        grid.add(passwordText, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(loginButton, 1, 2);
        grid.add(errorText, 1, 3);

        loginButton.setOnAction(e -> {
            String login = loginField.getText();
            String password = passwordField.getText();


            System.out.println(AccountService.login(login,password));
            String output = AccountService.login(login, password);

            if(output.contains("Login successful")){

                Pattern pattern = Pattern.compile("(\\d+)$");
                Matcher matcher = pattern.matcher(output);
                if (matcher.find()) {
                    String accountNumber = matcher.group(1);
                    MainApp.setCurrent_login_account_number(Integer.parseInt(accountNumber));
                }
                primaryStage.close();
                MainApp mainApp = new MainApp();
                mainApp.showMainWindow();
            } else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd logowania");
                alert.setHeaderText("Wystąpił błąd");
                alert.setContentText("Błędne hasło lub login");
                alert.showAndWait();
            }

        });

        Scene scene = new Scene(grid, 300, 200);
        primaryStage.setTitle("Okno logowania");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}