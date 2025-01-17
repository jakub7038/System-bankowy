package com.bank.view;

import com.bank.model.Receiver;
import com.bank.model.ReceiverService;
import com.bank.model.Transaction;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

import static com.bank.view.MainApp.current_login_account_number;

public class ReceiversView {
    static public Tab receiversTab() {
        VBox receiversTabContent = new VBox(10);
        receiversTabContent.setPadding(new Insets(10));

        // Lista odbiorców
        ListView<String> receiversListView = new ListView<>();
        updateReceiversList(receiversListView);

        // Kontener na przyciski
        HBox buttonContainer = new HBox(10);
        buttonContainer.setVisible(false);

        // Przycisk dodawania nowego odbiorcy
        Button addReceiverButton = new Button("Dodaj nowego odbiorce");
        addReceiverButton.setOnAction(e -> showAddReceiverButtonDialog(receiversListView));

        // Przycisk usuwania odbiorcy
        Button deleteReceiverButton = new Button("Usuń odbiorce");
        deleteReceiverButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");

        buttonContainer.getChildren().add(deleteReceiverButton);

        // Obsługa wyboru odbiorcy z listy
        receiversListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Receiver selectedReceiver = findReceiverByString(newValue);
                System.out.println(selectedReceiver);
                if (selectedReceiver != null) {
                    buttonContainer.setVisible(true);

                    // Akcja dla przycisku usuwania
                    deleteReceiverButton.setOnAction(event -> {
                        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmDialog.setTitle("Potwierdzenie usunięcia");
                        confirmDialog.setHeaderText("Czy na pewno chcesz usunąć tego odbiorce?");
                        confirmDialog.setContentText(selectedReceiver.toString());

                        Optional<ButtonType> result = confirmDialog.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            ReceiverService.deleteReceiver(selectedReceiver.getAccountNumberReceiver(), String.valueOf(current_login_account_number));
                            updateReceiversList(receiversListView);
                            buttonContainer.setVisible(false);
                        }
                    });
                }
            } else {
                buttonContainer.setVisible(false);
            }
        });

        receiversTabContent.getChildren().addAll(receiversListView, buttonContainer, addReceiverButton);

        Tab receiversTab = new Tab("Zapisani odbiorcy", receiversTabContent);
        receiversTab.setClosable(false);
        return receiversTab;
    }


    private static Receiver findReceiverByString(String receiverString) {
        List<Receiver> receivers = ReceiverService.readReceiversByTiedAccount(String.valueOf(current_login_account_number));
        return receivers.stream()
                .filter(receiver -> receiver.display().equals(receiverString))
                .findFirst()
                .orElse(null);
    }

    private static void showAddReceiverButtonDialog(ListView<String> receiversListView) {
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

                    ReceiverService.createReceiver(new Receiver(accountNumber,String.valueOf(current_login_account_number), description, firstName,lastName));
                    updateReceiversList(receiversListView);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private static void updateReceiversList(ListView<String> listView) {
        List<Receiver> receivers = ReceiverService.readReceiversByTiedAccount(String.valueOf(current_login_account_number));
        listView.getItems().clear();
        for (Receiver receiver : receivers) {
            listView.getItems().add(receiver.display());
        }
    }
}
