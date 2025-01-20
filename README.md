# System-bankowy

Poradnik do postawienia bazy danych:

Należy stworzyć użytkowniak bank i przyznać mu hasło i wszystkie uprawnienia:

`CREATE USER bank IDENTIFIED BY bank`;

`ALTER USER bank IDENTIFIED BY oracle;`

`GRANT ALL PRIVILEGES TO bank`;

Należy upewnić się że użytkownik bank ma uprawnienia do DBMS_CRYPTO (jest to wymagane do działania hashowania haseł !)
Uprawnienie należy przyznać przez użytkownika z rolą SYSDBA

`GRANT EXECUTE ON DBMS_CRYPTO TO bank`;

`SET SERVEROUTPUT ON`;

Należy stworzyć tabelki z pliku tabele.sql, następnie należy skompilować wszystkie pakiety (w pakietach znajdują się procedury oraz funkcje wymagane do działania bazy danych)

Ten kod musi zostać wykonany żeby transakcje działały prawidłowo:


```
CREATE SEQUENCE transaction_id_seq
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;
/
```

```
CREATE OR REPLACE TRIGGER trg_transaction_id
BEFORE INSERT ON "TRANSACTION"
FOR EACH ROW
BEGIN
    :NEW.ID := transaction_id_seq.NEXTVAL;
END;
/
```

Należy upewnić się że konfiguracja połączenia w pliku w projekcie bank com.bank.repository.DatabaseConfig.java jest odpowiednia

```
    private static final String URL = "jdbc:oracle:thin:@//localhost:1521/freepdb1";
    private static final String USER = "bank";
    private static final String PASSWORD = "oracle";
```

W razie potrzeby należy zmienić te zmienne tak żeby połączyć się z bazą danych 


Do uruchomienia projektu potrzebne są bilbioteki JavaFX oraz JDBC  

https://gluonhq.com/products/javafx/

https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html

Biblioteki znajdują się również w folderze lib w projekcie aplikacji java



