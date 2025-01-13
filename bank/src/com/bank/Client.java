package com.bank;

import java.util.Date;

public class Client {

    private String pesel;
    private String firstName;
    private String lastName;
    private String middleName;
    private String phoneNumber;
    private Date dateOfBirth;

    public Client(String pesel,
                  String firstName,
                  String lastName,
                  String middleName,
                  String phoneNumber,
                  Date dateOfBirth) {
        this.pesel = pesel;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
    }

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        return "com.bank.Client [PESEL=" + pesel +
                ", Name=" + firstName +
                ", Last name=" + lastName +
                ", Middle Name=" + middleName +
                ", Phone=" + phoneNumber +
                ", Date of Birth=" + dateOfBirth + "]";
    }
}
