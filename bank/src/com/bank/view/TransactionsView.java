package com.bank.view;

import com.bank.model.Transaction;
import com.bank.model.TransactionService;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

import static com.bank.view.MainApp.current_login_account_number;

public class TransactionsView {
    public static Tab transactionsTab() {
        List<Transaction> transactions = TransactionService.readTransactionsByAccount(String.valueOf(current_login_account_number));
        VBox transactionsTabContent = new VBox(10);
        transactionsTabContent.setPadding(new Insets(10));

        ListView<String> transactionsListView = new ListView<>();
        updateTransactionsList(transactionsListView, transactions);


        Button addTransactionButton = new Button("Dodaj nową transakcję");
        addTransactionButton.setOnAction(e -> showAddTransactionDialog(transactionsListView));

        transactionsTabContent.getChildren().addAll(transactionsListView, addTransactionButton);

        Tab transactionsTab = new Tab("Transakcje", transactionsTabContent);
        transactionsTab.setClosable(false);
        return transactionsTab;
    }

    private static void updateTransactionsList(ListView<String> listView, List<Transaction> transactions) {
        listView.getItems().clear();
        for (Transaction transaction : transactions) {
            listView.getItems().add(transaction.display());
        }
    }

    private static void showAddTransactionDialog(ListView<String> transactionsListView) {
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.setTitle("Nowa transakcja");
        dialog.setHeaderText("Wprowadź dane transakcji");

        ButtonType confirmButtonType = new ButtonType("Zatwierdź", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField recipientAccountField = new TextField();
        TextField amountField = new TextField();
        TextField descriptionField = new TextField();

        grid.add(new Label("Numer konta odbiorcy:"), 0, 0);
        grid.add(recipientAccountField, 1, 0);
        grid.add(new Label("Kwota:"), 0, 2);
        grid.add(amountField, 1, 2);
        grid.add(new Label("Opis:"), 0, 3);
        grid.add(descriptionField, 1, 3);

        dialog.getDialogPane().setContent(grid);


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                try {
                    Random random = new Random();

                    int randomInt = random.nextInt();
                    System.out.println("Random integer: " + randomInt);

                    int randomIntInRange = random.nextInt(10000);

                    double amount = Double.parseDouble(amountField.getText());

                    String result = TransactionService.createTransaction(
                            randomIntInRange,
                            String.valueOf(current_login_account_number),
                            recipientAccountField.getText(),
                            Timestamp.valueOf("2025-01-05 12:00:00"),
                            amount,
                            "test",
                            descriptionField.getText()
                    );
                    System.out.println(result);

                    List<Transaction> updatedTransactions = TransactionService.readTransactionsByAccount(
                            String.valueOf(current_login_account_number)
                    );
                    updateTransactionsList(transactionsListView, updatedTransactions);

                } catch (NumberFormatException ex) {
                    showAlert("Błąd", "Nieprawidłowa kwota transakcji");
                    return null;
                } catch (Exception ex) {
                    showAlert("Błąd", "Wystąpił błąd podczas tworzenia transakcji");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
