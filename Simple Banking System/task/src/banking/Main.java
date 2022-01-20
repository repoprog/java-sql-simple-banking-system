package banking;

import java.sql.*;

import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    static boolean quit = false;

    public static void main(String[] args) {
        String fileName =   args[0].equals("-fileName") ? args[1] : null;
        createNewTable(fileName);
        BankAccount.setDataBase(fileName);

        while (!quit) {
            showMenu();
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    createAccount();
                    break;
                case "2":
                    login();
                    break;
                case "0":
                    System.out.println("Bye!");
                    quit = true;
                default:
                    System.out.println("Wrong choice.");
            }
        }
    }

    public static void createNewTable(String fileName) {
        String url = "jdbc:sqlite:" + fileName;
        String sql = "CREATE TABLE IF NOT EXISTS card (" +
                "id INTEGER PRIMARY KEY," +
                "number TEXT," +
                "pin TEXT," +
                "balance INTEGER DEFAULT 0 );";
        try (Connection con = DriverManager.getConnection(url);
             Statement statement = con.createStatement()) {

            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void showMenu() {
        System.out.println("1. Create an account\n" +
                "2. Log into account\n" +
                "0. Exit");
    }

    public static void createAccount() {
        BankAccount account = new BankAccount();
    }

    public static void login() {
        String enteredCard;
        System.out.println("Enter your card number: ");
        enteredCard = scanner.nextLine();
        String enteredPin;
        System.out.println("Enter your PIN: ");
        enteredPin = scanner.nextLine();
        BankAccount.isAuthorised(enteredCard, enteredPin);

    }
}