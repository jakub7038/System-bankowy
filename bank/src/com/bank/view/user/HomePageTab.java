package com.bank.view.user;

import com.bank.model.Account;
import com.bank.repository.AccountRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import static com.bank.view.user.UserApp.current_login_account_number;

public class HomePageTab {
    static public Tab homePage() {
        VBox mainTabContent = new VBox(20);
        mainTabContent.setStyle("-fx-background-color: #f5f5f5;");
        mainTabContent.setPadding(new Insets(30));
        mainTabContent.setAlignment(Pos.TOP_CENTER);

        Account account = AccountRepository.readAccountByNumber(String.valueOf(current_login_account_number));

        VBox infoPanel = createInfoPanel(account);

        mainTabContent.getChildren().add(infoPanel);

        Tab mainTab = new Tab("Okno główne", mainTabContent);
        mainTab.setClosable(false);

        return mainTab;
    }

    private static VBox createInfoPanel(Account account) {
        VBox infoPanel = new VBox(15);
        infoPanel.setStyle("-fx-background-color: white; " +
                "-fx-padding: 20; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        infoPanel.setMaxWidth(500);

        Label titleLabel = new Label("Witaj w naszym banku !");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Separator separator = new Separator();
        separator.setStyle("-fx-max-width: 460;");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20, 0, 0, 0));

        String labelStyle = "-fx-font-size: 14px; -fx-text-fill: #666666;";
        String valueStyle = "-fx-font-size: 16px; -fx-font-weight: bold;";

        addInfoRow(gridPane, "Numer konta:", account.getAccountNumber(), 0, labelStyle, valueStyle);
        addInfoRow(gridPane, "Dostępne środki:", String.format("$%.2f", account.getBalance()), 1, labelStyle, valueStyle);
        addInfoRow(gridPane, "Typ konta:", account.getTypeOfAccount(), 2, labelStyle, valueStyle);

        infoPanel.getChildren().addAll(titleLabel, separator, gridPane);

        return infoPanel;
    }

    private static void addInfoRow(GridPane grid, String label, String value, int row,
                                   String labelStyle, String valueStyle) {
        Label labelNode = new Label(label);
        labelNode.setStyle(labelStyle);

        Label valueNode = new Label(value);
        valueNode.setStyle(valueStyle);

        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }
}