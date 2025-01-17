package com.bank.view;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

public class CustomersView {
    static public Tab customerInfoTab(){
//     List<Client> clients = ClientService.readClientByPesel()
        VBox customerInfoTabContent = new VBox(10);
        customerInfoTabContent.getChildren().add(
                new Label("Informacje o kliencie")

        );
        Tab customerInfoTab = new Tab("Customer Info", customerInfoTabContent);
        customerInfoTab.setClosable(false);
        return customerInfoTab;
    }
}
