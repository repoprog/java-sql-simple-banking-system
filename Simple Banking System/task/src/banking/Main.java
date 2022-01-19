package banking;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static BankAccount account;

    public static void main(String[] args) {

        boolean quit = false;
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

    public static void showMenu() {
        System.out.println("1. Create an account\n" +
                "2. Log into account\n" +
                "0. Exit");
    }

    public static void createAccount() {
        account = new BankAccount();
    }

    public static void login() {
        String enteredCard;
        System.out.println("Enter your card number: ");
        enteredCard = scanner.nextLine();
        String enteredPin;
        System.out.println("Enter your PIN: ");
        enteredPin = scanner.nextLine();
        account.isAuthorised(enteredCard, enteredPin);

    }
}
