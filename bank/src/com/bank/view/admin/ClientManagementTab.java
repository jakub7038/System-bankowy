package com.bank.view.admin;

import com.bank.model.Client;
import com.bank.repository.ClientRepository;
import com.bank.view.shared.ErrorAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.sql.Date;
import java.util.Optional;

import static com.bank.view.shared.ErrorAlert.showAlert;

public class ClientManagementTab {
    private static TableView<Client> clientTable;
    private static final ObservableList<Client> clientList = FXCollections.observableArrayList();

    public static Tab getTab() {
        Tab tab = new Tab("Zarządzanie klientami");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Text title = new Text("Panel zarządzania klientami");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        content.getChildren().addAll(title, buttons(), createTableView());
        tab.setContent(content);

        refreshClientData();

        return tab;
    }

    private static TableView<Client> createTableView(){
        clientTable = new TableView<>();

        TableColumn<Client, String> clientPesel = new TableColumn<>("Pesel klienta");
        clientPesel.setCellValueFactory(new PropertyValueFactory<>("pesel"));

        TableColumn<Client, String> clientFirstName = new TableColumn<>("Imię");
        clientFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Client, String> clientLastName = new TableColumn<>("Nazwisko");
        clientLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Client, String> clientMiddleName = new TableColumn<>("2 imię");
        clientMiddleName.setCellValueFactory(new PropertyValueFactory<>("middleName"));

        TableColumn<Client, String> clientPhoneNumber = new TableColumn<>("nr telefonu");
        clientPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        TableColumn<Client, Date> clientBirthDate = new TableColumn<>("Data urodzenia");
        clientBirthDate.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

        clientTable.getColumns().addAll(clientPesel, clientFirstName, clientLastName,
                clientMiddleName, clientPhoneNumber, clientBirthDate);
        clientTable.setPrefHeight(400);
        clientTable.setItems(clientList);
        return clientTable;
    }

    private static HBox buttons(){
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button addClientBtn = new Button("Dodaj klienta");
        Button editClientFirstNameBtn = new Button("Zmień imię");
        Button editClientLastNameBtn = new Button("Zmień nazwisko");
        Button editClientMiddleNameBtn = new Button("Zmień drugie imię");
        Button deleteClientBtn = new Button("Usuń klienta");

        String buttonStyle = "-fx-background-color: #2196f3; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-min-width: 150;";
        addClientBtn.setStyle(buttonStyle);
        editClientFirstNameBtn.setStyle(buttonStyle);
        editClientLastNameBtn.setStyle(buttonStyle);
        editClientMiddleNameBtn.setStyle(buttonStyle);
        deleteClientBtn.setStyle(buttonStyle);

        buttonBox.getChildren().addAll(addClientBtn, editClientFirstNameBtn,editClientLastNameBtn,editClientMiddleNameBtn,
                deleteClientBtn);

        addClientBtn.setOnAction(e -> showAddClientDialog());

        editClientFirstNameBtn.setOnAction(e -> {
            Client selectedClient = clientTable.getSelectionModel().getSelectedItem();
            if (selectedClient != null){
                showChangeClientFirstNameDialog(selectedClient);
            } else showAlert("Błąd","Wybierz klienta");
        });

        editClientMiddleNameBtn.setOnAction(e -> {
            Client selectedClient = clientTable.getSelectionModel().getSelectedItem();
            if(selectedClient != null){
                showChangeClientMiddleNameDialog(selectedClient);
            } else showAlert("Błąd", "Wybierz klienta");
        });

        editClientLastNameBtn.setOnAction( e -> {
            Client selectedClient = clientTable.getSelectionModel().getSelectedItem();
            if(selectedClient != null){
                showChangeClientLastNameDialog(selectedClient);
            } else showAlert("Błąd", "Wybierz klienta");
        });

        deleteClientBtn.setOnAction( e -> {
            Client selectedClient = clientTable.getSelectionModel().getSelectedItem();
            if(selectedClient != null){
                showDeleteClientAlert(selectedClient);
            } else showAlert("Błąd", "Wybierz klienta");
        });

        return buttonBox;
    }

    private static void showDeleteClientAlert(Client selectedClient){
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Potwierdzenie usunięcia");
        confirmDialog.setHeaderText("Czy na pewno chcesz usunąć tego klienta ?");
        confirmDialog.setContentText(selectedClient.toString());
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            String resultMsg = ClientRepository.deleteClient(selectedClient.getPesel());
            if (resultMsg.contains("Error")) ErrorAlert.showAlert("Błąd", resultMsg);
            System.out.println(resultMsg);
            refreshClientData();
        }
    }

    private static void refreshClientData(){
        clientList.clear();
        clientList.addAll(ClientRepository.readAllClients());
    }

    private static void showChangeClientFirstNameDialog(Client selectedClient){
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Zmień imię klienta");
        dialog.setHeaderText(selectedClient.toString());
        ButtonType addButtonType = new ButtonType("Dodaj", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField firstNameField = new TextField();

        grid.add(new Label("Podaj nowe imię"),0,0);
        grid.add(firstNameField,1,0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType){
                if(!firstNameField.getText().isEmpty()){
                    String result = ClientRepository.updateClientFirstName(selectedClient.getPesel(),firstNameField.getText());
                    if(result.contains("Error")) ErrorAlert.showAlert("Błąd", result);
                    System.out.println(result);
                    refreshClientData();
                } else showAlert("Błąd", "Wypełnij wszystkie pola");
            } return null;
        });
        dialog.showAndWait();
    };

    private static void showChangeClientMiddleNameDialog(Client selectedClient) {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Zmień drugie imię klienta");
        dialog.setHeaderText(selectedClient.toString());

        ButtonType addButtonType = new ButtonType("Dodaj", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField middleNameField = new TextField();

        grid.add(new Label("Podaj nowe drugie imię"),0,0);
        grid.add(middleNameField,1,0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType){
                if(!middleNameField.getText().isEmpty()){
                    String result = ClientRepository.updateClientMiddleName(selectedClient.getPesel(),middleNameField.getText());
                    if(result.contains("Error")) ErrorAlert.showAlert("Błąd", result);
                    System.out.println(result);
                    refreshClientData();
                } else showAlert("Błąd", "Wypełnij wszystkie pola");
            } return null;
        });
        dialog.showAndWait();

    }


    private static void showChangeClientLastNameDialog(Client selectedClient) {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Zmień nazwisko klienta");
        dialog.setHeaderText(selectedClient.toString());

        ButtonType addButtonType = new ButtonType("Dodaj", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField lastNameField = new TextField();

        grid.add(new Label("Podaj nowe nazwisko"),0,0);
        grid.add(lastNameField,1,0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType){
                if(!lastNameField.getText().isEmpty()){
                    String result = ClientRepository.updateClientMiddleName(selectedClient.getPesel(),lastNameField.getText());
                    if(result.contains("Error")) ErrorAlert.showAlert("Błąd", result);
                    System.out.println(result);
                    refreshClientData();
                } else showAlert("Błąd", "Wypełnij wszystkie pola");
            } return null;
        });
        dialog.showAndWait();
    }


    private static void showAddClientDialog() {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Dodawanie nowego klienta");
        dialog.setHeaderText("Wprowadź dane nowego klienta");

        ButtonType addButtonType = new ButtonType("Dodaj", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField peselField = new TextField();
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField middleNameField = new TextField();
        TextField phoneNumberField = new TextField();
        DatePicker birthDatePicker = new DatePicker();

        grid.add(new Label("PESEL:"), 0, 0);
        grid.add(peselField, 1, 0);
        grid.add(new Label("Imię:"), 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(new Label("Nazwisko:"), 0, 2);
        grid.add(lastNameField, 1, 2);
        grid.add(new Label("Drugie imię:"), 0, 3);
        grid.add(middleNameField, 1, 3);
        grid.add(new Label("Nr telefonu:"), 0, 4);
        grid.add(phoneNumberField, 1, 4);
        grid.add(new Label("Data urodzenia:"), 0, 5);
        grid.add(birthDatePicker, 1, 5);

        dialog.getDialogPane().setContent(grid);


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                if (peselField.getText().isEmpty() ||
                        firstNameField.getText().isEmpty() ||
                        lastNameField.getText().isEmpty() ||
                        phoneNumberField.getText().isEmpty() ||
                        (birthDatePicker.getValue() == null)) {
                    showAlert("Błąd", "Wypełnij wszystkie wymagane pola!");
                    return null;
                }

                String result = ClientRepository.createClient(
                        peselField.getText(),
                        firstNameField.getText(),
                        lastNameField.getText(),
                        middleNameField.getText(),
                        phoneNumberField.getText(),
                        Date.valueOf(birthDatePicker.getValue().toString())
                );
                if(result.contains("Error")) ErrorAlert.showAlert("Błąd", result);
                refreshClientData();
                System.out.println(result);
            }
            return null;
        });

        dialog.showAndWait();
    }
}