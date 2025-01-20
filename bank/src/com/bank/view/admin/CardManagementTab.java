package com.bank.view.admin;

import com.bank.model.Account;
import com.bank.model.Card;
import com.bank.repository.CardRepository;
import com.bank.model.Client;
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

public class CardManagementTab {

    private static TableView<Card> cardTable;
    private static final ObservableList<Card> cardList = FXCollections.observableArrayList();

    public static Tab getTab() {
        Tab tab = new Tab("Zarządzanie kartami");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Text title = new Text("Panel zarządzania kartami");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        content.getChildren().addAll(title, buttons(), createTableView());
        tab.setContent(content);

        refreshCardData();
        return tab;
    }

    private static void copyCellToClipboard(){
        TablePosition<?, ?> pos = cardTable.getSelectionModel().getSelectedCells().stream()
                .findFirst()
                .orElse(null);

        if (pos != null) {
            Card card = cardTable.getItems().get(pos.getRow());
            String cellContent = getCellContent(card, pos.getTableColumn().getText());

            final ClipboardContent content = new ClipboardContent();
            content.putString(cellContent);
            Clipboard.getSystemClipboard().setContent(content);
        }
    }

    private static String getCellContent(Card card, String columnName) {
        switch (columnName) {
            case "Numer karty":
                return card.getCardNumber();
            case "Data wygaśnięcia":
                return  String.valueOf(card.getExpirationDate());
            case "Dzienny limit":
                return  String.valueOf(card.getDailyLimit());
            case "Limit pojedynczej płatności":
                return String.valueOf(card.getSinglePaymentLimit());
            case "ccv":
                return card.getCvv();
            case "Czy aktywna":
                return card.isActive() ? "Tak" : "Nie";
            default:
                return "";
        }
    }

    private static TableView<Card> createTableView(){
        cardTable = new TableView<>();

        ContextMenu contextMenu = new ContextMenu();
        MenuItem copyCellMenuItem = new MenuItem("Kopiuj komórkę");

        copyCellMenuItem.setOnAction(e -> copyCellToClipboard());

        cardTable.setContextMenu(contextMenu);
        contextMenu.getItems().addAll(copyCellMenuItem);

        cardTable.getSelectionModel().setCellSelectionEnabled(true);

        TableColumn<Card, String> cardNumberCol = new TableColumn<>("Numer karty");
        cardNumberCol.setCellValueFactory(new PropertyValueFactory<>("cardNumber"));

        TableColumn<Card, String> expirationDateCol = new TableColumn<>("Data wygaśnięcia");
        expirationDateCol.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));

        TableColumn<Card, String> dailyLimitCol = new TableColumn<>("Dzienny limit");
        dailyLimitCol.setCellValueFactory(new PropertyValueFactory<>("dailyLimit"));

        TableColumn<Card, String> singlePaymentLimitCol = new TableColumn<>("Limit pojedynczej płatności");
        singlePaymentLimitCol.setCellValueFactory(new PropertyValueFactory<>("singlePaymentLimit"));

        TableColumn<Card, String> ccvCol = new TableColumn<>("ccv");
        ccvCol.setCellValueFactory(new PropertyValueFactory<>("cvv"));

        TableColumn<Card, Boolean> isActiveCol = new TableColumn<>("Czy aktywna");
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
        cardTable.setItems(cardList);
        return cardTable;
    }

    private static HBox buttons(){
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button issueCardBtn = new Button("Utwórz kartę");
        Button blockCardBtn = new Button("Zablokuj kartę");
        Button unblockCardBtn = new Button("Odblokuj kartę");
        Button deleteCardBtn = new Button("Usuń kartę");
        Button refreshDataBtn = new Button("Odśwież");

        String buttonStyle = "-fx-background-color: #2196f3; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-min-width: 150;";
        issueCardBtn.setStyle(buttonStyle);
        blockCardBtn.setStyle(buttonStyle);
        unblockCardBtn.setStyle(buttonStyle);
        deleteCardBtn.setStyle(buttonStyle);
        refreshDataBtn.setStyle(buttonStyle);
        buttonBox.getChildren().addAll(issueCardBtn, blockCardBtn,
                unblockCardBtn,deleteCardBtn,refreshDataBtn);

        blockCardBtn.setOnAction(e -> {
            Card selectedCard = cardTable.getSelectionModel().getSelectedItem();
            if(selectedCard != null){
                String result = CardRepository.updateCardStatus(selectedCard.getCardNumber(),false);
                if (result.contains("Error")) ErrorAlert.showAlert("Błąd", result);
                refreshCardData();
            } else{
                ErrorAlert.showAlert("Błąd","Wybierz karte do zablokowania");
            }
        });

        unblockCardBtn.setOnAction(e -> {
            Card selectedCard = cardTable.getSelectionModel().getSelectedItem();
            if(selectedCard != null){
                String result = CardRepository.updateCardStatus(selectedCard.getCardNumber(),true);
                if (result.contains("Error")) ErrorAlert.showAlert("Błąd", result);
                refreshCardData();
            } else{
                ErrorAlert.showAlert("Błąd","Wybierz karte do odblokowania");
            }
        });

        issueCardBtn.setOnAction(e -> showAddCardDialog());

        deleteCardBtn.setOnAction( e -> {
            Card selectedCard = cardTable.getSelectionModel().getSelectedItem();
            if(selectedCard != null){
                String result = CardRepository.deleteCard(selectedCard.getCardNumber());
                refreshCardData();
                if (result.contains("Error")) ErrorAlert.showAlert("Błąd", result);
            } else ErrorAlert.showAlert("Błąd" , "Wybierz karte");
        });
        refreshDataBtn.setOnAction( e -> refreshCardData());
        return buttonBox;
    }

    private static void refreshCardData(){
        cardList.clear();
        cardList.addAll(CardRepository.readAllCards());
    }
    private static void showAddCardDialog(){
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Dodawanie nowej karty");
        dialog.setHeaderText("Wprowadź dane nowej karty");

        ButtonType addButtonType = new ButtonType("Dodaj", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField dailyLimitField = new TextField();
        TextField singlePaymentLimitField = new TextField();
        TextField ccvField = new TextField();
        CheckBox isActiveCheckBox = new CheckBox();
        TextField accountNumberField = new TextField();
        TextField pin = new TextField();

        grid.add(new Label("Dzienny limit:"), 0, 0);
        grid.add(dailyLimitField, 1, 0);
        grid.add(new Label("Limit pojedyńczej płatności:"), 0, 1);
        grid.add(singlePaymentLimitField, 1, 1);
        grid.add(new Label("ccv:"), 0, 2);
        grid.add(ccvField, 1, 2);
        grid.add(new Label("czy aktywna:"), 0, 3);
        grid.add(isActiveCheckBox, 1, 3);
        grid.add(new Label("Przypisany numer konta:"), 0, 4);
        grid.add(accountNumberField, 1, 4);
        grid.add(new Label("pin:"), 0, 5);
        grid.add(pin, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType){
                if(dailyLimitField.getText().isEmpty()
                        || singlePaymentLimitField.getText().isEmpty()
                        || ccvField.getText().isEmpty()
                        || accountNumberField.getText().isEmpty()
                        || pin.getText().isEmpty()
                ){
                    ErrorAlert.showAlert("Błąd", "Wypełnij wszystkie wymagane pola!");
                    return null;
                }
                String result = CardRepository.createCard(
                        Double.parseDouble(dailyLimitField.getText()),
                        Double.parseDouble(singlePaymentLimitField.getText()) ,
                        ccvField.getText(),
                        isActiveCheckBox.isSelected(),
                        accountNumberField.getText(),
                        pin.getText()
                );
                if(result.contains("Error")){
                    ErrorAlert.showAlert("Błąd",result);
                }
                refreshCardData();
                System.out.println(result);
            }
            return null;
        });
        dialog.showAndWait();
    }

}