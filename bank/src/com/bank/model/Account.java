package com.bank.model;

import java.sql.Date;

public class Account {
    private String accountNumber;
    private String typeOfAccount;
    private double balance;
    private Date dateOfCreation;
    private String status;
    private String login;

    public Account(String accountNumber, String typeOfAccount, double balance, Date dateOfCreation, String status, String login) {
        this.accountNumber = accountNumber;
        this.typeOfAccount = typeOfAccount;
        this.balance = balance;
        this.dateOfCreation = dateOfCreation;
        this.status = status;
        this.login = login;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getTypeOfAccount() {
        return typeOfAccount;
    }

    public void setTypeOfAccount(String typeOfAccount) {
        this.typeOfAccount = typeOfAccount;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "Numer konta: " + accountNumber + "," +
                " Typ konta: " + typeOfAccount +
                ", Balans: " + balance +
                ", Data utworzenia: " + dateOfCreation +
                ", Status: " + status +
                ", Login: " + login;
    }
}
