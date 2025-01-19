package com.bank.view.admin;

import com.bank.model.Transaction;
import com.bank.repository.TransactionRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.Date;

class TransactionViewTab {
    private static TableView<Transaction> transactionTable;
    private static final ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    static Tab getTab(){
        Tab tab = new Tab("Zarządzanie transakcjami");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Text title = new Text("Panel do wyświetlania transakcji");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        content.getChildren().addAll(title,createTableView());
        tab.setContent(content);

        refreshTransactionData();
        return tab;

    }
    private static TableView<Transaction> createTableView(){
        transactionTable = new TableView<>();

        TableColumn<Transaction, String> IdCol = new TableColumn<>("Id");
        IdCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Transaction, String> AccountNumberCol = new TableColumn<>("Numer konta");
        AccountNumberCol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));

        TableColumn<Transaction, String> AccountNumberReceiverCol= new TableColumn<>("Numer konta odbiorcy");
        AccountNumberReceiverCol.setCellValueFactory(new PropertyValueFactory<>("accountNumberReceiver"));

        TableColumn<Transaction, String> dateOfTransactionCol = new TableColumn<>("data");
        dateOfTransactionCol.setCellValueFactory(new PropertyValueFactory<>("dateOfTransaction"));

        TableColumn<Transaction, String> amountCol = new TableColumn<>("kwota");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Transaction, Date> typeOfTransactionCol = new TableColumn<>("typ");
        typeOfTransactionCol.setCellValueFactory(new PropertyValueFactory<>("typeOfTransaction"));

        TableColumn<Transaction, Date> descriptionCol = new TableColumn<>("opis");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        transactionTable.getColumns().addAll(IdCol, AccountNumberCol, AccountNumberReceiverCol,
                dateOfTransactionCol, amountCol, typeOfTransactionCol, descriptionCol);
        transactionTable.setPrefHeight(400);
        transactionTable.setItems(transactionList);
        return transactionTable;
    }
    private static void refreshTransactionData(){
        transactionList.clear();
        transactionList.addAll(TransactionRepository.readAllTransactions());
    }
}
