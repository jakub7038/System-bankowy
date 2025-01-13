package com.bank.view;

import com.bank.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class MainApp {

    static private int current_login_account_number;

    public static int getCurrent_login_account_number() {
        return current_login_account_number;
    }

    public static void setCurrent_login_account_number(int current_login_account_number) {
        MainApp.current_login_account_number = current_login_account_number;
    }

    public void showMainWindow() {

        VBox mainTabContent = new VBox(10);
        Account account = AccountService.readAccountByNumber(String.valueOf(current_login_account_number));

        Label accountNumberLabel = new Label("Account Number: " + account.getAccountNumber());
        Label balanceLabel = new Label("Balance: $" + account.getBalance());
        Label accountTypeLabel = new Label("Account Type: " + account.getTypeOfAccount());

        mainTabContent.getChildren().addAll(accountNumberLabel, balanceLabel, accountTypeLabel);
        // Ustawienie układu

        Tab mainTab = new Tab("Main", mainTabContent);

        List<Card> cards = CardService.readCardsByAccountNumber(String.valueOf(current_login_account_number));

        // Tworzenie zakładki "Karty"
        VBox cardsTabContent = new VBox(10);
        ListView<String> cardsListView = new ListView<>();
        cardsListView.getItems().addAll(String.valueOf(cards)); // Dodanie kart do ListView
        cardsTabContent.getChildren().add(cardsListView);
        Tab cardsTab = new Tab("Cards", cardsTabContent);


        List<Transaction> transactions = TransactionService.readTransactionsByAccount(String.valueOf(current_login_account_number));

        // Tworzenie zakładki "Transakcje"
        VBox transactionsTabContent = new VBox(10);
        ListView<String> transactionsListView = new ListView<>();
        transactionsListView.getItems().addAll(String.valueOf(transactions)); // Dodanie transakcji do ListView
        transactionsTabContent.getChildren().add(transactionsListView);
        Tab transactionsTab = new Tab("Transactions", transactionsTabContent);



        // Tworzenie zakładki "Informacje o koncie"
        VBox accountInfoTabContent = new VBox(10);
        accountInfoTabContent.getChildren().add(
                new Label("Account info window content here.")
        );
        Tab accountInfoTab = new Tab("Account Info", accountInfoTabContent);


//        Client client = ClientService.
        // Tworzenie zakładki "Informacje o kliencie"
        VBox customerInfoTabContent = new VBox(10);
        customerInfoTabContent.getChildren().add(
                new Label("Informacje o kliencie")

        );
        Tab customerInfoTab = new Tab("Customer Info", customerInfoTabContent);

        // Tworzenie TabPane i dodanie zakładek
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(mainTab, cardsTab, transactionsTab, accountInfoTab, customerInfoTab);

        Scene scene = new Scene(tabPane);
        Stage mainStage = new Stage();
        mainStage.setTitle("Główne Okno Aplikacji");
        mainStage.setScene(scene);
        mainStage.show();
    }

}