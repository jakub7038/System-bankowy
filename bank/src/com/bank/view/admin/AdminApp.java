package com.bank.view.admin;

import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;


public class AdminApp {
    private Stage stage;

    public void showMainWindow() {
        stage = new Stage();
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().addAll(
                AccountManagementTab.getTab(),
                CardManagementTab.getTab(),
                ClientManagementTab.getTab(),
                TransactionViewTab.getTab()
        );

        Scene scene = new Scene(tabPane, 800, 600);
        stage.setTitle("Panel Administratora");
        stage.setScene(scene);
        stage.show();
    }
}