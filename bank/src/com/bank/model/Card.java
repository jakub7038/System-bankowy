package com.bank.model;

import java.sql.Date;

public class Card {

    private String cardNumber;
    private Date expirationDate;
    private double dailyLimit;
    private double singlePaymentLimit;
    private String cvv;
    private boolean isActive;
    private String accountNumber;
    private String pin;

    public Card(String cardNumber,
                Date expirationDate,
                double dailyLimit,
                double singlePaymentLimit,
                String cvv,
                boolean isActive,
                String accountNumber,
                String pin) {
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.dailyLimit = dailyLimit;
        this.singlePaymentLimit = singlePaymentLimit;
        this.cvv = cvv;
        this.isActive = isActive;
        this.accountNumber = accountNumber;
        this.pin = pin;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public double getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(double dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public double getSinglePaymentLimit() {
        return singlePaymentLimit;
    }

    public void setSinglePaymentLimit(double singlePaymentLimit) {
        this.singlePaymentLimit = singlePaymentLimit;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    @Override
    public String toString() {
        return "Numer karty" +  String.valueOf(cardNumber).trim() +
                ", Data wygaśnięcia=" +  String.valueOf(expirationDate).trim() +
                ", Dzienny limit=" +  String.valueOf(dailyLimit).trim() +
                ", Limit pojedyńczej płatności=" +  String.valueOf(singlePaymentLimit).trim() +
                ", CVV=" +  String.valueOf(cvv).trim() +
                ", PIN= " + String.valueOf(pin).trim() +
                ", Czy aktywna=" + (isActive ? "Tak" : "Nie") +
                ", Numer konta przypisanego do karty=" +  String.valueOf(accountNumber).trim();
    }
    public String display(){
        return "Numer karty: " + String.valueOf(cardNumber).trim() +
                ", Data wygaśnięcia: " + String.valueOf(expirationDate).trim() +
                ", Dzienny limit=" +  String.valueOf(dailyLimit).trim() +
                ", Limit pojedyńczej płatności=" +  String.valueOf(singlePaymentLimit).trim() +
                ", CVV=" +  String.valueOf(cvv).trim() +
                ", " + (isActive ? "Aktywna" : "Nie aktywna");
    }
}
//String.valueOf(isActive).trim()