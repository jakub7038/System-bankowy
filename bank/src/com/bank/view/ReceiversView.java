package com.bank.view;

import com.bank.Receiver;
import com.bank.ReceiverService;
import com.bank.Transaction;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;

import static com.bank.view.MainApp.current_login_account_number;

public class ReceiversView {
    static public Tab receiversTab(){
        List<Receiver> receivers = ReceiverService.readReceiversByTiedAccount(String.valueOf(current_login_account_number));

        VBox receiversTabContent = new VBox(10);
        receiversTabContent.setPadding(new Insets(10));
        ListView<String> receiversListView = new ListView<>();

        updateReceiversList(receiversListView, receivers);


        Button addReceiverButton = new Button("Dodaj nowego odbiorce");
        addReceiverButton.setOnAction(e -> showAddReceiverButtonDialog(receiversListView));

        receiversListView.getItems().addAll(String.valueOf(receivers));

        receiversTabContent.getChildren().addAll(receiversListView, addReceiverButton);

        Tab receiversTab = new Tab("Zapisani odbiorcy", receiversTabContent);
        receiversTab.setClosable(false);
        return receiversTab;
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
        dialog.showAndWait();
    }

    private static void updateReceiversList(ListView<String> listView, List<Receiver> receivers) {
        listView.getItems().clear();
        for (Receiver receiver : receivers) {
            listView.getItems().add(receiver.display());
        }
    }
}
