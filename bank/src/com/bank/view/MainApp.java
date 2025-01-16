package com.bank.view;

import com.bank.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MainApp {

    static int current_login_account_number;

    public static int getCurrent_login_account_number() {
        return current_login_account_number;
    }

    public static void setCurrent_login_account_number(int current_login_account_number) {
        MainApp.current_login_account_number = current_login_account_number;
    }

    public void showMainWindow() {
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(HomePageView.homePage(),
                CardsView.cardsTab(),
                TransactionsView.transactionsTab(),
                CustomersView.customerInfoTab(),
                ReceiversView.receiversTab());

        Scene scene = new Scene(tabPane, 1600 , 800);
        Stage mainStage = new Stage();
        mainStage.setTitle("Aplikacja bankowa");

        mainStage.setScene(scene);
        mainStage.show();
    }

}