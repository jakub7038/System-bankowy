package com.bank.view.admin;

import com.bank.model.Client;
import com.bank.repository.AccountRepository;
import com.bank.repository.ClientAccountRepository;
import com.bank.repository.ClientRepository;
import com.bank.view.shared.ErrorAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import com.bank.model.Account;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.bank.view.shared.ErrorAlert.showAlert;

public class AccountManagementTab {
    private static TableView<Account> accountTable;
    private static final ObservableList<Account> accountList = FXCollections.observableArrayList();

    public static Tab getTab() {
        Tab tab = new Tab("Zarządzanie kontami");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Text title = new Text("Panel zarządzania kontami");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        content.getChildren().addAll(title, buttons(), createTableView());
        tab.setContent(content);

        refreshAccountData();
        return tab;
    }

    private static HBox buttons(){
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button createAccountBtn = new Button("Utwórz konto");
        Button deleteAccountBtn = new Button("Usuń konto");
        Button changePasswordAccountBtn = new Button("Zmień hasło");
        Button changeLoginAccountBtn = new Button("Zmień login");
        Button displayAssignedClientsBtn = new Button("Wyświetl przypisanych kientów");
        Button assignClientToAccountBtn = new Button("Przypisz klienta do konta");
        Button deleteClientFromAccountBtn = new Button("Usuń klienta z konta");
        Button refreshDataBtn = new Button("Odśwież");

        String buttonStyle = "-fx-background-color: #2196f3; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-min-width: 150;";
        createAccountBtn.setStyle(buttonStyle);
        deleteAccountBtn.setStyle(buttonStyle);
        changePasswordAccountBtn.setStyle(buttonStyle);
        changeLoginAccountBtn.setStyle(buttonStyle);
        displayAssignedClientsBtn.setStyle(buttonStyle);
        assignClientToAccountBtn.setStyle(buttonStyle);
        deleteClientFromAccountBtn.setStyle(buttonStyle);
        refreshDataBtn.setStyle(buttonStyle);
        buttonBox.getChildren().addAll(createAccountBtn, deleteAccountBtn,
                changePasswordAccountBtn,
                changeLoginAccountBtn,
                displayAssignedClientsBtn,
                assignClientToAccountBtn,
                deleteClientFromAccountBtn,
                refreshDataBtn);

        createAccountBtn.setOnAction(e -> showCreateAccountDialog());

        deleteAccountBtn.setOnAction(e -> {
            Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();
            if (selectedAccount != null) {
                deleteAccount(selectedAccount);
            } else {
                showAlert("Błąd", "Wybierz konto do usunięcia");
            }
        });
        changePasswordAccountBtn.setOnAction(e ->{
            Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();
            if (selectedAccount != null){
                changePassword(selectedAccount);
            } else showAlert("Błąd", "Wybierz konto");
        });
        changeLoginAccountBtn.setOnAction(e -> {
            Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();
            if (selectedAccount != null){
                changeLogin(selectedAccount);
            } else showAlert("Błąd", "Wybierz konto");
        });
        displayAssignedClientsBtn.setOnAction(e -> {
            Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();
            if(selectedAccount != null){
                displayAssignedClients(selectedAccount);
            } else showAlert("Błąd", "Wybierz konto");
        });
        assignClientToAccountBtn.setOnAction(e -> {
            Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();
            if(selectedAccount != null){
                assignClientToAccount(selectedAccount);
            } else showAlert("Błąd", "Wybierz konto");
        });
        deleteClientFromAccountBtn.setOnAction( e -> {
            Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();
            if(selectedAccount != null){
                deleteClientFromAccount(selectedAccount);
            } else showAlert("Błąd", "Wybierz konto");
        });
        refreshDataBtn.setOnAction( e -> refreshAccountData());

        return buttonBox;
    }

    private static void deleteClientFromAccount(Account selectedAccount) {
        Dialog<Account> dialog = new Dialog<>();
        dialog.setTitle("Usuwanie klienta z konta nr: "+ selectedAccount.getAccountNumber());
        dialog.setHeaderText("Wprowadź pesel klient którego chcesz usunąć");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField pesel = new TextField();

        grid.add(new Label("Pesel:"), 0, 0);
        grid.add(pesel, 1, 0);

        dialog.getDialogPane().setContent(grid);

        ButtonType createButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        dialog.setResultConverter( buttonType -> {
            if (buttonType == createButtonType) {
                String result = ClientAccountRepository.deleteClientFromAccount(pesel.getText(), selectedAccount.getAccountNumber());
                if(result.contains("Error")) ErrorAlert.showAlert("Błąd", result);
            }
            return null;
        });
        dialog.showAndWait();
    }

    private static void assignClientToAccount(Account selectedAccount) {
        Dialog<Account> dialog = new Dialog<>();
        dialog.setTitle("Przypisanie klienta do konta nr: "+ selectedAccount.getAccountNumber());
        dialog.setHeaderText("Wprowadź pesel klient którego chcesz przypisać");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField pesel = new TextField();

        grid.add(new Label("Pesel:"), 0, 0);
        grid.add(pesel, 1, 0);

        dialog.getDialogPane().setContent(grid);

        ButtonType createButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        dialog.setResultConverter( buttonType -> {
            if (buttonType == createButtonType) {
                String result = ClientAccountRepository.addClientToAccount(pesel.getText(),selectedAccount.getAccountNumber());
                if(result.contains("Error")) ErrorAlert.showAlert("Błąd", result);
            }
            return null;
        });
        dialog.showAndWait();
    }

    private static TableView<Account> createTableView(){
        accountTable = new TableView<>();

        ContextMenu contextMenu = new ContextMenu();
        MenuItem copyCellMenuItem = new MenuItem("Kopiuj komórkę");

        copyCellMenuItem.setOnAction(e -> copyCellToClipboard());

        contextMenu.getItems().addAll(copyCellMenuItem);
        accountTable.setContextMenu(contextMenu);

        accountTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.C && event.isControlDown()) {
                if (event.isShiftDown()) {
                } else {
                    copyCellToClipboard(); // Ctrl + C kopiuje komórkę
                }
            }
        });

        accountTable.getSelectionModel().setCellSelectionEnabled(true);

        TableColumn<Account, String> accountNumberCol = new TableColumn<>("Numer konta");
        accountNumberCol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));

        TableColumn<Account, String> accountTypeCol = new TableColumn<>("typ konta");
        accountTypeCol.setCellValueFactory(new PropertyValueFactory<>("typeOfAccount"));

        TableColumn<Account, Double> accountBalanceCol = new TableColumn<>("Balans");
        accountBalanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));

        TableColumn<Account, String> accountDateCol = new TableColumn<>("Data utworzenia");
        accountDateCol.setCellValueFactory(new PropertyValueFactory<>("dateOfCreation"));

        TableColumn<Account, String> accountStatusCol = new TableColumn<>("Status");
        accountStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Account, String> accountloginsCol = new TableColumn<>("Login");
        accountloginsCol.setCellValueFactory(new PropertyValueFactory<>("login"));

        accountBalanceCol.setCellFactory(column -> new TableCell<Account, Double>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f zł", value));
                }
            }
        });

        accountTable.getColumns().addAll(accountNumberCol, accountTypeCol, accountBalanceCol,
                accountDateCol, accountStatusCol, accountloginsCol);
        accountTable.setPrefHeight(400);
        accountTable.setItems(accountList);
        return accountTable;
    }

    private static void copyCellToClipboard() {
        TablePosition<?, ?> pos = accountTable.getSelectionModel().getSelectedCells().stream()
                .findFirst()
                .orElse(null);

        if (pos != null) {
            Account account = accountTable.getItems().get(pos.getRow());
            String cellContent = getCellContent(account, pos.getTableColumn().getText());

            final ClipboardContent content = new ClipboardContent();
            content.putString(cellContent);
            Clipboard.getSystemClipboard().setContent(content);
        }
    }

    private static String getCellContent(Account account, String columnName) {
        switch (columnName) {
            case "Numer konta":
                return account.getAccountNumber();
            case "typ konta":
                return account.getTypeOfAccount();
            case "Balans":
                return String.format("%.2f zł", account.getBalance());
            case "Data utworzenia":
                return String.valueOf(account.getDateOfCreation());
            case "Status":
                return account.getStatus();
            case "Login":
                return account.getLogin();
            default:
                return "";
        }
    }

    private static void refreshAccountData() {
        accountList.clear();
        accountList.addAll(AccountRepository.readAllAccounts());
    }

    private static void showCreateAccountDialog() {
        Dialog<Account> dialog = new Dialog<>();
        dialog.setTitle("Tworzenie nowego konta");
        dialog.setHeaderText("Wprowadź dane nowego konta");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField accountType = new TextField();
        TextField accountInitialBalanceField = new TextField();
        TextField accountStatus = new TextField();
        TextField accountLogin = new TextField();
        TextField accountPassword = new TextField();
        TextField accountPesel = new TextField();

        grid.add(new Label("Typ konta:"), 0, 0);
        grid.add(accountType, 1, 0);
        grid.add(new Label("Saldo początkowe:"), 0, 1);
        grid.add(accountInitialBalanceField, 1, 1);
        grid.add(new Label("Status"),0,2);
        grid.add(accountStatus,1,2);
        grid.add(new Label("login"),0,3);
        grid.add(accountLogin,1,3);
        grid.add(new Label("Hasło"),0,4);
        grid.add(accountPassword,1,4);
        grid.add(new Label("Pesel klienta (klient musi istnieć w bazie)"),0,5);
        grid.add(accountPesel,1,5);

        dialog.getDialogPane().setContent(grid);

        ButtonType createButtonType = new ButtonType("Utwórz", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    String type = accountType.getText();
                    double balance = Double.parseDouble(accountInitialBalanceField.getText());
                    String status = accountStatus.getText();
                    String login = accountLogin.getText();
                    String password = accountPassword.getText();
                    String pesel = accountPesel.getText();

                    String result = AccountRepository.createAccount(type,balance,status,login,password,pesel);
                    if (result.contains("Error")) ErrorAlert.showAlert("Błąd", result);
                    refreshAccountData();
                    System.out.println(result);

                } catch (NumberFormatException e) {
                    showAlert("Błąd", "Nieprawidłowa wartość salda");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private static void displayAssignedClients(Account selectedAccount) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Przypisani klienci");
        dialog.setHeaderText("Lista klientów przypisanych do konta: " + selectedAccount.getAccountNumber());

        TextArea clientsTextArea = new TextArea();
        clientsTextArea.setEditable(false);
        clientsTextArea.setPrefRowCount(10);
        clientsTextArea.setPrefColumnCount(40);
        clientsTextArea.setWrapText(true);

        List<Client> clients = ClientRepository.readClientsByAccount(selectedAccount.getAccountNumber());

        StringBuilder clientsText = new StringBuilder();
        for (Client client : clients) {
            clientsText.append(String.format("Pesel: %s, Imię: %s, Nazwisko: %s, drugie imię: %s, numer telefonu: %s, data urodzenia: %s \n",
                    client.getPesel(),
                    client.getFirstName(),
                    client.getLastName(),
                    client.getMiddleName(),
                    client.getPhoneNumber(),
                    client.getDateOfBirth()
            ));
        }
        clientsTextArea.setText(clientsText.toString());

        Button copyButton = new Button("Kopiuj do schowka");
        copyButton.setOnAction(e -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(clientsTextArea.getText());
            clipboard.setContent(content);
        });

        VBox content = new VBox(10);
        content.getChildren().addAll(clientsTextArea, copyButton);
        content.setPadding(new Insets(10));

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setContent(content);

        dialog.showAndWait();
    }

    private static void changePassword(Account selectedAccount) {
        Dialog<Account> dialog = new Dialog<>();
        dialog.setTitle("Zmienianie hasła");
        dialog.setHeaderText("Wprowadź nowe hasło");
        dialog.setContentText("Numer konta: " + selectedAccount.getAccountNumber());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField accountPassword = new TextField();
        TextField accountPasswordRepeat = new TextField();

        grid.add(new Label("nowe hasło:"), 0, 0);
        grid.add( accountPassword, 1, 0);

        grid.add(new Label("potwierdź nowe hasło:"), 0, 1);
        grid.add(accountPasswordRepeat, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType createButtonType = new ButtonType("Utwórz", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                if((!accountPassword.getText().isEmpty() && accountPassword.getText().equals(accountPasswordRepeat.getText()))){
                    String result = AccountRepository.adminUpdatePassword(selectedAccount.getAccountNumber(),accountPassword.getText());
                    if (result.contains("Error")) ErrorAlert.showAlert("Błąd", result);
                    System.out.println(result);
                    refreshAccountData();
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    private static void changeLogin(Account selectedAccount){
        Dialog<Account> dialog = new Dialog<>();
        dialog.setTitle("Zmienianie loginu");
        dialog.setHeaderText("Wprowadź nowy login");
        dialog.setContentText("Numer konta: " + selectedAccount.getAccountNumber());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField accountLogin = new TextField();

        grid.add(new Label("nowy login:"), 0, 0);
        grid.add( accountLogin, 1, 0);

        dialog.getDialogPane().setContent(grid);

        ButtonType createButtonType = new ButtonType("Utwórz", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                if((!accountLogin.getText().isEmpty())){
                    String result = AccountRepository.adminUpdateLogin(selectedAccount.getAccountNumber(),accountLogin.getText());
                    if (result.contains("Error")) ErrorAlert.showAlert("Błąd", result);
                    System.out.println(result);
                    refreshAccountData();
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    private static void deleteAccount(Account account) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potwierdzenie usunięcia");
        alert.setHeaderText("Czy na pewno chcesz usunąć konto?");
        alert.setContentText("Numer konta: " + account.getAccountNumber());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
               String result = AccountRepository.deleteAccount(account.getAccountNumber());
               if (result.contains("Error")) ErrorAlert.showAlert("Błąd", result);
            }
        });
    }

}