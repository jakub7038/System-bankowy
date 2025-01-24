package com.bank.view.user;

import com.bank.model.Receiver;
import com.bank.model.Transaction;
import com.bank.repository.ReceiverRepository;
import com.bank.repository.TransactionRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.Date;
import java.util.List;

import static com.bank.view.shared.ErrorAlert.showAlert;
import static com.bank.view.user.UserApp.current_login_account_number;

public class TransactionTab {
    private static TableView<Transaction> transactionTable;
    private static final ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    public static Tab transactionsTab() {
        Tab tab = new Tab("Transakcje");
        tab.setClosable(false);
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Text title = new Text("Transakcje");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        createTableView();
        content.getChildren().addAll(title,buttons(),transactionTable);
        tab.setContent(content);

        refreshTransactionData();
        return tab;
    }

    private static void refreshTransactionData(){
        transactionList.clear();
        transactionList.addAll(TransactionRepository.readTransactionsByAccount(current_login_account_number));
    }

    private static void createTableView(){
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
    }

    private static HBox buttons(){
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        String buttonStyle = "-fx-background-color: #2196f3; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-min-width: 150;";

        Button addTransactionBtn = new Button("Dodaj transakcje");
        addTransactionBtn.setOnAction(e -> {
            showAddTransactionDialog();
        });
        addTransactionBtn.setStyle(buttonStyle);
        buttonBox.getChildren().addAll(addTransactionBtn);
        return buttonBox;
    }

    private static void showAddTransactionDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Nowa transakcja");
        dialog.setHeaderText("Wprowadź dane transakcji");

        ButtonType confirmButtonType = new ButtonType("Zatwierdź", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        ComboBox<String> contactsComboBox = new ComboBox<>();
        contactsComboBox.setPromptText("Wybierz kontakt");

        List<Receiver> receivers = ReceiverRepository.readReceiversByTiedAccount(current_login_account_number);

        for (Receiver receiver : receivers) {
            contactsComboBox.getItems().add(receiver.displayReceiver());
        }

        TextField recipientAccountField = new TextField();
        TextField amountField = new TextField();
        TextField descriptionField = new TextField();

        contactsComboBox.setOnAction(e -> {
            String selectedContact = contactsComboBox.getValue();
            if (selectedContact != null) {
                String accountNumber = selectedContact.split(":")[1];
                accountNumber = accountNumber.replaceAll("[^0-9]", "");
                recipientAccountField.setText(accountNumber);
            }
        });

        grid.add(new Label("Zapisane kontakty:"), 0, 0);
        grid.add(contactsComboBox, 1, 0);
        grid.add(new Label("Numer konta odbiorcy:"), 0, 1);
        grid.add(recipientAccountField, 1, 1);
        grid.add(new Label("Kwota:"), 0, 2);
        grid.add(amountField, 1, 2);
        grid.add(new Label("Opis:"), 0, 3);
        grid.add(descriptionField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                try {
                    String result = TransactionRepository.createTransaction(
                            current_login_account_number,
                            recipientAccountField.getText(),
                            Double.parseDouble(amountField.getText()),
                            "Przelew",
                            descriptionField.getText()
                    );
                    System.out.println(result);
                    refreshTransactionData();

                } catch (NumberFormatException ex) {
                    showAlert("Błąd", "Nieprawidłowa kwota transakcji");
                } catch (Exception ex) {
                    showAlert("Błąd", "Wystąpił błąd podczas tworzenia transakcji");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
}
