package com.bank.view.user;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class UserApp {

    static String current_login_account_number;

    public static String getCurrent_login_account_number() {
        return current_login_account_number;
    }

    public static void setCurrent_login_account_number(String current_login_account_number) {
        UserApp.current_login_account_number = current_login_account_number;
    }

    public void showMainWindow() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(HomePageTab.homePage(),
                CardTab.cardsTab(),
                TransactionTab.transactionsTab(),
                CustomerTab.customerInfoTab(),
                ReceiverTab.receiversTab());

        Scene scene = new Scene(tabPane, 1600 , 800);
        Stage mainStage = new Stage();
        mainStage.setTitle("Aplikacja bankowa");

        mainStage.setScene(scene);
        mainStage.show();
    }

}