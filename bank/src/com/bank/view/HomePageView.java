package com.bank.view;

import com.bank.Account;
import com.bank.AccountService;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

import static com.bank.view.MainApp.current_login_account_number;

public class HomePageView {
    static public Tab homePage(){
        VBox mainTabContent = new VBox(10);
        Account account = AccountService.readAccountByNumber(String.valueOf(current_login_account_number));

        Label accountNumberLabel = new Label("Numer konta: " + account.getAccountNumber());
        Label balanceLabel = new Label("Dostępne środki: $" + account.getBalance());
        Label accountTypeLabel = new Label("Typ konta: " + account.getTypeOfAccount());
        mainTabContent.getChildren().addAll(accountNumberLabel, balanceLabel, accountTypeLabel);
        Tab mainTab = new Tab("Main", mainTabContent);
        mainTab.setClosable(false);
        return mainTab;
    }
}
