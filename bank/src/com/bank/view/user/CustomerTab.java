package com.bank.view.user;

import com.bank.model.Client;
import com.bank.repository.ClientRepository;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

import java.util.List;

import static com.bank.view.user.UserApp.current_login_account_number;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.text.SimpleDateFormat;

public class CustomerTab {
    static public Tab customerInfoTab() {
        List<Client> clients = ClientRepository.readClientsByAccount(String.valueOf(current_login_account_number));

        ScrollPane scrollPane = new ScrollPane();
        VBox customerInfoTabContent = new VBox(20);
        customerInfoTabContent.setPadding(new Insets(20));

        Label titleLabel = new Label("Informacje o klientach");
        titleLabel.setFont(new Font(16));
        customerInfoTabContent.getChildren().add(titleLabel);

        for (Client client : clients) {
            customerInfoTabContent.getChildren().add(createClientPanel(client));
        }

        scrollPane.setContent(customerInfoTabContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        Tab customerInfoTab = new Tab("Informacje o klientach", scrollPane);
        return customerInfoTab;
    }

    private static VBox createClientPanel(Client client) {
        VBox clientPanel = new VBox(5);
        clientPanel.setPadding(new Insets(15));
        clientPanel.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-background-radius: 5px;"
        );

        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(10);
        detailsGrid.setVgap(8);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        addField(detailsGrid, 0, "PESEL:", client.getPesel());
        addField(detailsGrid, 1, "Imię:", client.getFirstName());
        addField(detailsGrid, 2, "Nazwisko:", client.getLastName());
        addField(detailsGrid, 3, "Drugie imię:",
                client.getMiddleName() != null ? client.getMiddleName() : "-");
        addField(detailsGrid, 4, "Telefon:", client.getPhoneNumber());
        addField(detailsGrid, 5, "Data urodzenia:",
                client.getDateOfBirth() != null ? dateFormat.format(client.getDateOfBirth()) : "-");

        clientPanel.getChildren().add(detailsGrid);
        return clientPanel;
    }

    private static void addField(GridPane grid, int row, String labelText, String value) {
        Label label = new Label(labelText);
        Label valueLabel = new Label(value);

        label.setStyle("-fx-font-weight: bold;");

        grid.add(label, 0, row);
        grid.add(valueLabel, 1, row);
    }
}