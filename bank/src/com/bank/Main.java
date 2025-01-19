package com.bank;

import com.bank.view.LoginApp;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{
    @Override
    public void start(Stage primaryStage) {
        LoginApp loginApp = new LoginApp();
        loginApp.showLoginWindow(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);  //LAUNCH GUI !!!
    }
}
