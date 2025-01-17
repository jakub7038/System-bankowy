package com.bank.view;

import com.bank.model.Card;
import com.bank.model.CardService;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;

import static com.bank.view.MainApp.current_login_account_number;

public class CardsView {
    public static Tab cardsTab() {
        VBox cardsTabContent = new VBox(10);
        cardsTabContent.setPadding(new Insets(10));

        // Lista kart
        ListView<String> cardsListView = new ListView<>();
        updateCardsList(cardsListView);

        // Kontener na przyciski dla wybranej karty
        HBox buttonContainer = new HBox(10);
        buttonContainer.setVisible(false);

        Button toggleActiveButton = new Button();
        Button changeLimitsButton = new Button("Zmień limity");
        Button changePinButton = new Button("Zmień PIN");

        buttonContainer.getChildren().addAll(toggleActiveButton, changeLimitsButton, changePinButton);

        // Obsługa wyboru karty z listy
        cardsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Card selectedCard = findCardByString(newValue);
                if (selectedCard != null) {
                    buttonContainer.setVisible(true);
                    toggleActiveButton.setText(selectedCard.isActive() ? "Dezaktywuj" : "Aktywuj");

                    // Akcja dla przycisku aktywacji/dezaktywacji
                    toggleActiveButton.setOnAction(event -> {
                        System.out.println(selectedCard.isActive());
                        CardService.updateCardStatus(selectedCard.getCardNumber(), !selectedCard.isActive());
                        selectedCard.setActive(!selectedCard.isActive());
                        updateCardsList(cardsListView);
                    });

                    // Akcja dla przycisku zmiany limitów
                    changeLimitsButton.setOnAction(event -> {
                        showChangeLimitsWindow(selectedCard, cardsListView);
                    });

                    changePinButton.setOnAction(event -> {
                        showChangePinWindow(selectedCard, cardsListView);
                    });

                }
            } else {
                buttonContainer.setVisible(false);
            }
        });

        cardsTabContent.getChildren().addAll(cardsListView, buttonContainer);

        Tab cardsTab = new Tab("Karty", cardsTabContent);
        cardsTab.setClosable(false);
        return cardsTab;
    }


    private static void updateCardsList(ListView<String> listView) {
        List<Card> cards = CardService.readCardsByAccountNumber(String.valueOf(current_login_account_number));
        listView.getItems().clear();
        for (Card card : cards) {
            listView.getItems().add(card.display());
        }
    }

    private static Card findCardByString(String cardString) {
        List<Card> cards = CardService.readCardsByAccountNumber(String.valueOf(current_login_account_number));
        return cards.stream()
                .filter(card -> card.display().equals(cardString))
                .findFirst()
                .orElse(null);
    }
    private static void showChangePinWindow(Card card, ListView<String> cardsListView) {
        Dialog<Card> dialog = new Dialog<>();
        dialog.setTitle("Zmiana pinu");
        dialog.setHeaderText("Zmień PIN dla karty" + card.toString());

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
                        CardService.updateCardPin(card.getCardNumber() , pin1);
                        card.setPin(pin1);
                        updateCardsList(cardsListView);
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

    private static void showChangeLimitsWindow(Card card, ListView<String> cardsListView) {
        Dialog<Card> dialog = new Dialog<>();
        dialog.setTitle("Zmiana limitów");
        dialog.setHeaderText("Zmień limity dla karty: " + card.toString());

        ButtonType confirmButtonType = new ButtonType("Zatwierdź", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField dailyLimitField = new TextField();
        dailyLimitField.setPromptText("Wprowadź nowy limit dzienny");
        dailyLimitField.setText(String.valueOf(card.getDailyLimit()));

        TextField singleTransactionLimitField = new TextField();
        singleTransactionLimitField.setPromptText("Wprowadź nowy limit pojedynczej płatności");
        singleTransactionLimitField.setText(String.valueOf(card.getSinglePaymentLimit()));

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

                    card.setDailyLimit(dailyLimit);
                    card.setSinglePaymentLimit(singleTransactionLimit);
                    CardService.updateCardLimits(card.getCardNumber(), dailyLimit, singleTransactionLimit);

                    updateCardsList(cardsListView);

                    return card;
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
