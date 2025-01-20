package com.bank.view.admin;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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

        Scene scene = new Scene(tabPane, 1500, 600);
        stage.setTitle("Panel Administratora");
        stage.setScene(scene);
        stage.show();
    }
}