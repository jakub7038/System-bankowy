package com.bank.model;

import java.sql.Timestamp;

public class Transaction {

    private int id;
    private String accountNumber;
    private String accountNumberReceiver;
    private Timestamp dateOfTransaction;
    private double amount;
    private String typeOfTransaction;
    private String description;

    public Transaction(int id,
                       String accountNumber,
                       String accountNumberReceiver,
                       Timestamp dateOfTransaction,
                       double amount,
                       String typeOfTransaction,
                       String description) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountNumberReceiver = accountNumberReceiver;
        this.dateOfTransaction = dateOfTransaction;
        this.amount = amount;
        this.typeOfTransaction = typeOfTransaction;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountNumberReceiver() {
        return accountNumberReceiver;
    }

    public void setAccountNumberReceiver(String accountNumberReceiver) {
        this.accountNumberReceiver = accountNumberReceiver;
    }

    public Timestamp getDateOfTransaction() {
        return dateOfTransaction;
    }

    public void setDateOfTransaction(Timestamp dateOfTransaction) {
        this.dateOfTransaction = dateOfTransaction;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTypeOfTransaction() {
        return typeOfTransaction;
    }

    public void setTypeOfTransaction(String typeOfTransaction) {
        this.typeOfTransaction = typeOfTransaction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ID Transakcji=" +String.valueOf(id).trim()  +
                ", Numer konta=" + String.valueOf(accountNumber).trim() +
                ", Numer odbiorcy=" +  String.valueOf( accountNumberReceiver).trim() +
                ", Data=" +  String.valueOf(dateOfTransaction).trim() +
                ", Kwota transakcji=" +  String.valueOf(amount).trim() +
                ", Typ transakcji=" +  String.valueOf(typeOfTransaction).trim() +
                ", Opis transakcji=" +  String.valueOf(description).trim();
    }
    public String display(){
        return "Numer konta odbiorcy: " + String.valueOf( accountNumberReceiver).trim() +
                ", Data: " + String.valueOf(dateOfTransaction).trim() +
                ", Kwota: " + String.valueOf(amount).trim() +
                ", Opis: "+ String.valueOf(description).trim();
    }
}
