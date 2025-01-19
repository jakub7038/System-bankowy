package com.bank.view.user;

import com.bank.model.Receiver;
import com.bank.repository.ReceiverRepository;
import com.bank.model.Transaction;
import com.bank.view.shared.ErrorAlert;
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

import java.util.Optional;

import static com.bank.view.user.UserApp.current_login_account_number;





public class ReceiverTab {
    private static TableView<Receiver> receiverTable;
    private static final ObservableList<Receiver> receiverList = FXCollections.observableArrayList();

    static public Tab receiversTab() {
        Tab tab = new Tab("Odbiorcy");
        tab.setClosable(false);
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Text title = new Text("Odbiorcy");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        createTableView();
        content.getChildren().addAll(title,buttons(), receiverTable);
        tab.setContent(content);

        refreshReceiverData();
        return tab;
    }

    private static void createTableView(){
        receiverTable = new TableView<>();

        TableColumn<Receiver, String> accountNumberReceiverCol = new TableColumn<>("numer konta odbiorcy");
        accountNumberReceiverCol.setCellValueFactory(new PropertyValueFactory<>("accountNumberReceiver"));

        TableColumn<Receiver, String> accountNumberTiedCol = new TableColumn<>("Numer konta");
        accountNumberTiedCol.setCellValueFactory(new PropertyValueFactory<>("accountNumberTied"));

        TableColumn<Receiver, String> descriptionCol= new TableColumn<>("Opis");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Receiver, String> firstNameCol = new TableColumn<>("Imię");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Receiver, String> lastNameCol = new TableColumn<>("Nazwisko");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));


        receiverTable.getColumns().addAll(accountNumberReceiverCol, accountNumberTiedCol, descriptionCol,
                firstNameCol, lastNameCol);
        receiverTable.setPrefHeight(400);
        receiverTable.setItems(receiverList);
    }

    private static void refreshReceiverData(){
        receiverList.clear();
        receiverList.addAll(ReceiverRepository.readReceiversByTiedAccount(current_login_account_number));
    }


    private static HBox buttons(){
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        String buttonStyle = "-fx-background-color: #2196f3; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-min-width: 150;";

        Button addReceiverBtn = new Button("Dodaj odbiorcę");
        Button deleteReceiverBtn = new Button("Usuń odbiorcę");

        addReceiverBtn.setOnAction(e -> {
            showAddReceiverButtonDialog();
        });

        deleteReceiverBtn.setOnAction( e -> {
            Receiver selectedReceiver = receiverTable.getSelectionModel().getSelectedItem();
            if (selectedReceiver != null){
                deleteReceiverAlert(selectedReceiver);
            } else ErrorAlert.showAlert("Błąd", "Wybierz odbiorcę");
        });

        addReceiverBtn.setStyle(buttonStyle);
        deleteReceiverBtn.setStyle(buttonStyle);
        buttonBox.getChildren().addAll(addReceiverBtn, deleteReceiverBtn);

        return buttonBox;
    }

    private static void  deleteReceiverAlert(Receiver selectedReceiver){
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Potwierdzenie usunięcia");
        confirmDialog.setHeaderText("Czy na pewno chcesz usunąć tego odbiorce?");
        confirmDialog.setContentText(selectedReceiver.displayReceiver());
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ReceiverRepository.deleteReceiver(selectedReceiver.getAccountNumberReceiver(),current_login_account_number);
            refreshReceiverData();
        }
    }

    private static void showAddReceiverButtonDialog() {
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.setTitle("Dodaj nowego odbiorcę");
        dialog.setHeaderText("Wprowadź dane odbiorcy");

        ButtonType confirmButtonType = new ButtonType("Zatwierdź", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField receiverFirstName = new TextField();
        TextField receiverLastName = new TextField();
        TextField receiverDescription = new TextField();
        TextField receiverAccountNumber = new TextField();

        grid.add(new Label("Imię:"), 0, 0);
        grid.add(receiverFirstName, 1, 0);

        grid.add(new Label("Nazwisko:"), 0, 1);
        grid.add(receiverLastName, 1, 1);

        grid.add(new Label("Opis:"), 0, 2);
        grid.add(receiverDescription, 1, 2);

        grid.add(new Label("Numer Konta:"), 0, 3);
        grid.add(receiverAccountNumber, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType){
                try{
                    String firstName = receiverFirstName.getText();
                    String lastName = receiverLastName.getText();
                    String description = receiverDescription.getText();
                    String accountNumber = receiverAccountNumber.getText();

                    ReceiverRepository.createReceiver(new Receiver(accountNumber,String.valueOf(current_login_account_number), description, firstName,lastName));
                    refreshReceiverData();
                } catch (Exception e) {
                    ErrorAlert.showAlert("Błąd" , e.getMessage());
                    throw new RuntimeException(e);
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
}
