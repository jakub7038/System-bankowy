import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class Main {
    public static void account() {
        AccountService service = new AccountService();

        System.out.println("Reading all accounts:");
        List<Account> accounts = service.readAllAccounts();
        for (Account account : accounts) {
            System.out.println(account);
        }

        System.out.println("\nCreating a new account:");
        String creationResult = service.createAccount(
                "12345", "Savings", 1000.0, Date.valueOf("2025-01-05"), "Active", "jdoe", "password123", "12345678901"
        );
        System.out.println("Account creation result: " + creationResult);

        System.out.println("\nUpdating password:");
        String updatePasswordResult = service.updatePassword("12345", "password123", "newpassword123");
        System.out.println("Password update result: " + updatePasswordResult);

        System.out.println("\nPerforming login:");
        String loginResult = service.login("jdoe", "newpassword123");
        System.out.println("Login result: " + loginResult);

        System.out.println("\nReading all accounts after updates:");
        accounts = service.readAllAccounts();
        for (Account account : accounts) {
            System.out.println(account);
        }
    }

    public static void client(){
        ClientService clientService = new ClientService();

        String createResult = clientService.createClient(
                "12345678901",
                "John",
                "Doe",
                "Michael",
                "555-1234",
                Date.valueOf("1990-01-01")
        );
        System.out.println("Create Result: " + createResult);

        System.out.println("Reading all clients:");
        List<Client> clients = clientService.readAllClients();
        for (Client client : clients) {
            System.out.println(client);
        }

        System.out.println("Reading client with PESEL 12345678901:");
        Client client = clientService.readClientByPesel("12345678901");
        if (client != null) {
            System.out.println(client);
        } else {
            System.out.println("Client not found.");
        }

        String updateFirstNameResult = clientService.updateClientFirstName("12345678901", "Jonathan");
        System.out.println("Update First Name Result: " + updateFirstNameResult);

        String updateLastNameResult = clientService.updateClientLastName("12345678901", "Smith");
        System.out.println("Update Last Name Result: " + updateLastNameResult);

        String updateMiddleNameResult = clientService.updateClientMiddleName("12345678901", "Edward");
        System.out.println("Update Middle Name Result: " + updateMiddleNameResult);

        String updatePhoneNumberResult = clientService.updateClientPhoneNumber("12345678901", "555-6789");
        System.out.println("Update Phone Number Result: " + updatePhoneNumberResult);

        System.out.println("Reading updated client with PESEL 12345678901:");
        Client updatedClient = clientService.readClientByPesel("12345678901");
        if (updatedClient != null) {
            System.out.println(updatedClient);
        } else {
            System.out.println("Client not found.");
        }
    }

    public static void card() {
        CardService cardService = new CardService();

        String createResult = cardService.createCard(
                "1234567812345678",
                Date.valueOf("2025-12-31"),
                1000,
                500,
                "123",
                true,
                "12345",
                "1234"
        );
        System.out.println("Create Result: " + createResult);

        System.out.println("Reading all cards:");
        List<Card> cards = cardService.readAllCards();
        for (Card card : cards) {
            System.out.println(card);
        }

        System.out.println("Reading card with number 1234567812345678:");
        Card cardByNumber = cardService.readCardByNumber("1234567812345678");
        if (cardByNumber != null) {
            System.out.println(cardByNumber);
        } else {
            System.out.println("Card not found.");
        }

        System.out.println("Reading cards linked to account number 9876543210:");
        List<Card> cardsByAccount = cardService.readCardsByAccountNumber("9876543210");
        for (Card cardByAccount : cardsByAccount) {
            System.out.println(cardByAccount);
        }

        String updateLimitsResult = cardService.updateCardLimits("1234567812345678", 2000, 1000);
        System.out.println("Update Limits Result: " + updateLimitsResult);

        String updateStatusResult = cardService.updateCardStatus("1234567812345678", false);
        System.out.println("Update Status Result: " + updateStatusResult);

        String updatePinResult = cardService.updateCardPin("1234567812345678", "4321");
        System.out.println("Update PIN Result: " + updatePinResult);

        System.out.println("Reading updated card with number 1234567812345678:");
        Card updatedCard = cardService.readCardByNumber("1234567812345678");
        if (updatedCard != null) {
            System.out.println(updatedCard);
        } else {
            System.out.println("Card not found.");
        }
    }

    public static void transaction() {
        TransactionService transactionService = new TransactionService();
        System.out.println("Creating a new transaction:");
        String createResult = transactionService.createTransaction(
                5,
                "12345",
                "54321",
                Timestamp.valueOf("2025-01-05 12:00:00"),
                500.0,
                "Transfer",
                "Payment for services"
        );
        System.out.println("Create Result: " + createResult);

        System.out.println("\nReading all transactions:");
        List<Transaction> transactions = transactionService.readAllTransactions();
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }

        System.out.println("\nReading transactions for account number 12345:");
        List<Transaction> transactionsByAccount = transactionService.readTransactionsByAccount("12345");
        for (Transaction transactionByAccount : transactionsByAccount) {
            System.out.println(transactionByAccount);
        }
    }

    public static void receiver() {
        ReceiverService receiverService = new ReceiverService();

        System.out.println("Creating a new receiver:");
        String createResult = receiverService.createReceiver(
                new Receiver(
                        "56789",
                        "12345",
                        "Utility Bill",
                        "John",
                        "Doe"
                )
        );
        System.out.println("Create Result: " + createResult);

        System.out.println("\nReading all receivers:");
        List<Receiver> receivers = receiverService.readAllReceivers();
        for (Receiver receiver : receivers) {
            System.out.println(receiver);
        }

        System.out.println("\nReading receivers tied to account number '12345':");
        List<Receiver> tiedReceivers = receiverService.readReceiversByTiedAccount("12345");
        for (Receiver receiver : tiedReceivers) {
            System.out.println(receiver);
        }

        System.out.println("\nDeleting the receiver with account number '56789' tied to '12345':");
        String deleteResult = receiverService.deleteReceiver("56789", "12345");
        System.out.println("Delete Result: " + deleteResult);

        System.out.println("\nReading all receivers after deletion:");
        receivers = receiverService.readAllReceivers();
        for (Receiver receiver : receivers) {
            System.out.println(receiver);
        }
    }

    public static void main(String[] args) {
        //client();
        //account();
        //card();
        //transaction();
        //receiver();

//        TransactionService transactionService = new TransactionService();
//
//        List<Transaction> allTransactions = transactionService.readAllTransactions();
//        for (Transaction transaction : allTransactions) {
//            System.out.println(transaction);
//        }
//        System.out.println("bruh");
//        List<Transaction> accountTransactions = transactionService.readTransactionsByAccount("12345");
//        for (Transaction transaction : accountTransactions) {
//            System.out.println(transaction);
//        }
//
//        ReceiverService receiverService = new ReceiverService();
//
//        List<Receiver> allReceivers = receiverService.readAllReceivers();
//        for (Receiver receiver : allReceivers) {
//            System.out.println(receiver);
//        }
//        System.out.println("bruh");
//        List<Receiver> receiversByAccount = receiverService.readReceiversByTiedAccount("1234567890123461");
//        for (Receiver receiver : receiversByAccount) {
//            System.out.println(receiver);
//        }
//
//
//        ClientService clientService = new ClientService();
//
//        List<Client> allClients = clientService.readAllClients();
//        for (Client client : allClients) {
//            System.out.println(client);
//        }
//        System.out.println("bruh");
//        System.out.println(clientService.readClientByPesel("12345678921"));
//
//        CardService cardService = new CardService();
//
//        List<Card> allCards = cardService.readAllCards();
//        for (Card card : allCards) {
//            System.out.println(card);
//        }
//
//        System.out.println("bruh");
//        Card cardByNumber = cardService.readCardByNumber("1234567812345678");
//            System.out.println(cardByNumber);
//
//        System.out.println("bruh");
//        List<Card> cardByAccount = cardService.readCardsByAccountNumber("1234567890123461");
//        for (Card card : cardByAccount) {
//            System.out.println(card);
//        }

        AccountService accountService = new AccountService();

        List<Account> allAccounts = accountService.readAllAccounts();
        for (Account account : allAccounts) {
            System.out.println(account);
        }

        System.out.println("bruh");

        System.out.println(accountService.readAccountByNumber("1234567890123461"));

    }
}
