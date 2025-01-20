package com.bank.view.admin;

import com.bank.model.Client;
import com.bank.model.Transaction;
import com.bank.repository.TransactionRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.Date;

class TransactionViewTab {
    private static TableView<Transaction> transactionTable;
    private static final ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    static Tab getTab(){
        Tab tab = new Tab("Wyświetlanie wszystkich transakcji w banku");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Text title = new Text("Panel do wyświetlania transakcji");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button refreshDataBtn = new Button("Odśwież");
        String buttonStyle = "-fx-background-color: #2196f3; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-min-width: 150;";
        refreshDataBtn.setStyle(buttonStyle);
        refreshDataBtn.setOnAction(e -> refreshTransactionData());
        buttonBox.getChildren().add(refreshDataBtn);

        content.getChildren().addAll(title,buttonBox,createTableView());
        tab.setContent(content);

        refreshTransactionData();
        return tab;

    }

    private static void copyCellToClipboard(){
        TablePosition<?, ?> pos = transactionTable.getSelectionModel().getSelectedCells().stream()
                .findFirst()
                .orElse(null);

        if (pos != null) {
            Transaction transaction = transactionTable.getItems().get(pos.getRow());
            String cellContent = getCellContent(transaction, pos.getTableColumn().getText());

            final ClipboardContent content = new ClipboardContent();
            content.putString(cellContent);
            Clipboard.getSystemClipboard().setContent(content);
        }
    }

    private static String getCellContent(Transaction transaction, String columnName){
        switch (columnName) {
            case "Id":
                return String.valueOf(transaction.getId());
            case "Numer konta":
                return  transaction.getAccountNumber();
            case "Numer konta odbiorcy":
                return  transaction.getAccountNumberReceiver();
            case "Data":
                return String.valueOf(transaction.getDateOfTransaction());
            case "Kwota":
                return String.valueOf(transaction.getAmount());
            case "Typ":
                return transaction.getTypeOfTransaction();
            case "Opis":
                return transaction.getDescription();
            default:
                return "";
        }
    }

    private static TableView<Transaction> createTableView(){
        transactionTable = new TableView<>();

        ContextMenu contextMenu = new ContextMenu();
        MenuItem copyCellMenuItem = new MenuItem("Kopiuj komórkę");

        copyCellMenuItem.setOnAction(e -> copyCellToClipboard());

        transactionTable.setContextMenu(contextMenu);
        contextMenu.getItems().addAll(copyCellMenuItem);

        transactionTable.getSelectionModel().setCellSelectionEnabled(true);


        TableColumn<Transaction, String> IdCol = new TableColumn<>("Id");
        IdCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Transaction, String> AccountNumberCol = new TableColumn<>("Numer konta");
        AccountNumberCol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));

        TableColumn<Transaction, String> AccountNumberReceiverCol= new TableColumn<>("Numer konta odbiorcy");
        AccountNumberReceiverCol.setCellValueFactory(new PropertyValueFactory<>("accountNumberReceiver"));

        TableColumn<Transaction, String> dateOfTransactionCol = new TableColumn<>("Data");
        dateOfTransactionCol.setCellValueFactory(new PropertyValueFactory<>("dateOfTransaction"));

        TableColumn<Transaction, String> amountCol = new TableColumn<>("Kwota");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Transaction, Date> typeOfTransactionCol = new TableColumn<>("Typ");
        typeOfTransactionCol.setCellValueFactory(new PropertyValueFactory<>("typeOfTransaction"));

        TableColumn<Transaction, Date> descriptionCol = new TableColumn<>("Opis");
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
