package com.bank.view.user;

import com.bank.model.Card;
import com.bank.repository.CardRepository;
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

import java.util.Objects;

import static com.bank.view.user.UserApp.current_login_account_number;

public class CardTab {
    private static TableView<Card> cardTable;
    private static final ObservableList<Card> cardtList = FXCollections.observableArrayList();

    public static Tab cardsTab() {
        Tab tab = new Tab("Karty");
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Text title = new Text("Panel zarządzania kartami");

        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        createTableView();
        content.getChildren().addAll(title, buttons(), cardTable);
        tab.setContent(content);

        refreshCardData();
        return tab;
    }

    private static HBox buttons(){
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        String buttonStyle = "-fx-background-color: #2196f3; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-min-width: 150;";

        Button toggleActiveButton = new Button("Aktywuj/Dezaktywuj");
        Button changeLimitsButton = new Button("Zmień limity");
        Button changePinButton = new Button("Zmień PIN");

        toggleActiveButton.setStyle(buttonStyle);
        changeLimitsButton.setStyle(buttonStyle);
        changePinButton.setStyle(buttonStyle);

        toggleActiveButton.setOnAction( e -> {
            Card selectedCard = cardTable.getSelectionModel().getSelectedItem();
            if(selectedCard != null)  CardRepository.updateCardStatus(selectedCard.getCardNumber(), !selectedCard.isActive());
            else ErrorAlert.showAlert("Błąd","Wybierz kartę");
            refreshCardData();
        });
        changeLimitsButton.setOnAction( e -> {
            Card selectedCard = cardTable.getSelectionModel().getSelectedItem();
            if (selectedCard != null) showChangeLimitsDialog(selectedCard);
            else ErrorAlert.showAlert("Błąd","Wybierz kartę");
        });

        changePinButton.setOnAction( e -> {
            Card selectedCard = cardTable.getSelectionModel().getSelectedItem();
            if (selectedCard != null) showChangePinDialog(selectedCard);
            else ErrorAlert.showAlert("Błąd","Wybierz kartę");
        });

        buttonBox.getChildren().addAll(toggleActiveButton, changeLimitsButton, changePinButton);
        return buttonBox;
    }


    private static void createTableView(){
        cardTable = new TableView<>();

        TableColumn<Card, String> cardNumberCol = new TableColumn<>("Numer karty");
        cardNumberCol.setCellValueFactory(new PropertyValueFactory<>("cardNumber"));

        TableColumn<Card, String> expirationDateCol = new TableColumn<>("Data wygaśnięcia");
        expirationDateCol.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));

        TableColumn<Card, String> dailyLimitCol = new TableColumn<>("dzienny limit");
        dailyLimitCol.setCellValueFactory(new PropertyValueFactory<>("dailyLimit"));

        TableColumn<Card, String> singlePaymentLimitCol = new TableColumn<>("limit pojedynczej płatności");
        singlePaymentLimitCol.setCellValueFactory(new PropertyValueFactory<>("singlePaymentLimit"));

        TableColumn<Card, String> ccvCol = new TableColumn<>("ccv");
        ccvCol.setCellValueFactory(new PropertyValueFactory<>("cvv"));

        TableColumn<Card, Boolean> isActiveCol = new TableColumn<>("czy aktywna");
        isActiveCol.setCellValueFactory(new PropertyValueFactory<>("active"));
        isActiveCol.setCellFactory(column -> new TableCell<Card, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Tak" : "Nie");
                }
            }
        });

        TableColumn<Card, String> accountNumberCol = new TableColumn<>("Przypisany numer konta");
        accountNumberCol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));

        TableColumn<Card, String> pinCol = new TableColumn<>("pin");
        pinCol.setCellValueFactory(new PropertyValueFactory<>("pin"));


        cardTable.getColumns().addAll(cardNumberCol, expirationDateCol, dailyLimitCol,
                singlePaymentLimitCol, ccvCol, isActiveCol, accountNumberCol, pinCol);
        cardTable.setPrefHeight(400);
        cardTable.setItems(cardtList);
    }

    private static void refreshCardData(){
        cardtList.clear();
        cardtList.addAll(CardRepository.readCardsByAccountNumber(current_login_account_number));
    }

    private static void showChangePinDialog(Card selectedCard) {
        Dialog<Card> dialog = new Dialog<>();
        dialog.setTitle("Zmiana pinu");
        dialog.setHeaderText("Zmień PIN dla karty" + selectedCard.toString());

        ButtonType confirmButtonType = new ButtonType("Zatwierdź", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField newPin = new TextField();
        newPin.setPromptText("Podaj nowy PIN");
        TextField newPinCheck = new TextField();
        newPinCheck.setPromptText("Potwierdź nowy PIN");

        grid.add(new Label("Podaj nowy PIN:"), 0, 0);
        grid.add(newPin, 1, 0);
        grid.add(new Label("Podaj nowy PIN jeszcze raz"), 0, 1);
        grid.add(newPinCheck, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType){
                try{
                    String pin1 = newPin.getText();
                    String pin2 = newPinCheck.getText();

                    if(Objects.equals(pin1, pin2)){
                        CardRepository.updateCardPin(selectedCard.getCardNumber() , pin1);
                        refreshCardData();
                    } else {
                        showAlert("Błąd", "Podane PINY nie są takie same");
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Błąd", "Wprowadź poprawne wartości liczbowe.");
                    return null;
                } catch (Exception ex) {
                    showAlert("Błąd", "Wystąpił błąd podczas aktualizacji");
                    return null;
                }

            }

            return null;
        });
        dialog.showAndWait();
    }

    private static void showChangeLimitsDialog(Card selectedCard) {
        Dialog<Card> dialog = new Dialog<>();
        dialog.setTitle("Zmiana limitów");
        dialog.setHeaderText("Zmień limity dla karty: " + selectedCard.toString());

        ButtonType confirmButtonType = new ButtonType("Zatwierdź", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField dailyLimitField = new TextField();
        dailyLimitField.setPromptText("Wprowadź nowy limit dzienny");
        dailyLimitField.setText(String.valueOf(selectedCard.getDailyLimit()));

        TextField singleTransactionLimitField = new TextField();
        singleTransactionLimitField.setPromptText("Wprowadź nowy limit pojedynczej płatności");
        singleTransactionLimitField.setText(String.valueOf(selectedCard.getSinglePaymentLimit()));

        grid.add(new Label("Nowy limit dzienny:"), 0, 0);
        grid.add(dailyLimitField, 1, 0);
        grid.add(new Label("Nowy limit pojedynczej płatności:"), 0, 1);
        grid.add(singleTransactionLimitField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                try {
                    double dailyLimit = Double.parseDouble(dailyLimitField.getText());
                    double singleTransactionLimit = Double.parseDouble(singleTransactionLimitField.getText());

                    if (dailyLimit <= 0 || singleTransactionLimit <= 0) {
                        showAlert("Błąd", "Limity muszą być większe od zera.");
                        return null;
                    }
                    if (singleTransactionLimit > dailyLimit) {
                        showAlert("Błąd", "Limit pojedynczej płatności nie może przekraczać limitu dziennego.");
                        return null;
                    }
                    CardRepository.updateCardLimits(selectedCard.getCardNumber(), dailyLimit, singleTransactionLimit);
                    refreshCardData();
                } catch (NumberFormatException ex) {
                    showAlert("Błąd", "Wprowadź poprawne wartości liczbowe.");
                    return null;
                } catch (Exception ex) {
                    showAlert("Błąd", "Wystąpił błąd podczas aktualizacji limitów");
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
