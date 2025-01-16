package com.bank;

public class Receiver {

    private String accountNumberReceiver;
    private String accountNumberTied;
    private String description;
    private String firstName;
    private String lastName;

    public Receiver(String accountNumberReceiver,
                    String accountNumberTied,
                    String description,
                    String firstName,
                    String lastName) {
        this.accountNumberReceiver = accountNumberReceiver;
        this.accountNumberTied = accountNumberTied;
        this.description = description;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getAccountNumberReceiver() {
        return accountNumberReceiver;
    }

    public void setAccountNumberReceiver(String accountNumberReceiver) {
        this.accountNumberReceiver = accountNumberReceiver;
    }

    public String getAccountNumberTied() {
        return accountNumberTied;
    }

    public void setAccountNumberTied(String accountNumberTied) {
        this.accountNumberTied = accountNumberTied;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "com.bank.Receiver [com.bank.Receiver com.bank.Account=" + accountNumberReceiver +
                ", Tied com.bank.Account=" + accountNumberTied +
                ", Description=" + description +
                ", Name=" + firstName +
                ", lastname= " + lastName + "]";
    }

    public String display() {
        return "Imię: " + firstName +
                ", Nazwisko: " +  lastName +
                "Opis: " + description +
                "Numer konta: " + accountNumberReceiver;
    }
}
