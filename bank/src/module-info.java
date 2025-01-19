module com.bank {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.sql;
    requires com.oracle.database.jdbc;
    requires java.desktop;
    exports com.bank;
    exports com.bank.model;
    exports com.bank.repository;
}