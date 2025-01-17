module com.bank {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.sql;
    requires com.oracle.database.jdbc;
    exports com.bank;
    exports com.bank.model;
}