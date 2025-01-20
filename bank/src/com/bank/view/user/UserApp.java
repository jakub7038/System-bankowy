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

        tabPane.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-tab-min-height: 20px;" +
                        "-fx-tab-max-height: 20px;" +
                        "-fx-border-color: #d4d4d4;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-focus-color: transparent;"
        );

        for (Tab tab : tabPane.getTabs()) {
            tab.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #e0e0e0, #ffffff);" +
                            "-fx-text-base-color: #333333;" +
                            "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 10;"
            );

            tab.setOnSelectionChanged(event -> {
                if (tab.isSelected()) {
                    tab.setStyle(
                            "-fx-background-color: linear-gradient(to bottom, #6a95ff, #003cbf);" +
                                    "-fx-text-base-color: white;" +
                                    "-fx-font-size: 14px;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-padding: 10;"
                    );
                } else {
                    tab.setStyle(
                            "-fx-background-color: linear-gradient(to bottom, #e0e0e0, #ffffff);" +
                                    "-fx-text-base-color: #333333;" +
                                    "-fx-font-size: 14px;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-padding: 10;"
                    );
                }
            });
        }


        Scene scene = new Scene(tabPane, 1500 , 800);
        Stage mainStage = new Stage();
        mainStage.setTitle("Aplikacja bankowa");

        mainStage.setScene(scene);
        mainStage.show();
    }

}