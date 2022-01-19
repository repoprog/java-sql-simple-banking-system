package banking;

import java.util.ArrayList;
import java.util.Scanner;

public class BankAccount {
    Scanner scanner = new Scanner(System.in);
    private long balance;
    private ArrayList<CreditCard> cardsList = new ArrayList<>();
    private CreditCard card;

    public BankAccount() {
        this.balance = 0;
        card = new CreditCard();
        this.cardsList.add(card);
    }

    public long getBalance() {
        return this.balance;
    }

    public boolean isAuthorised(String enteredCard, String enteredPin) {
        for (CreditCard creditCard : this.cardsList) {
            if (creditCard.getCardNumber().equals(enteredCard)
                    && creditCard.getPin().equals(enteredPin)) {
                System.out.println("You have successfully logged in!");
                loggedMenu();
                return true;
            }
        }
        System.out.println("Wrong card number or PIN!");
        return false;
    }

    private void loggedMenu() {
        boolean quit = false;
        while (!quit) {
            System.out.println("1. Balance\n" +
                    "2. Log out\n" +
                    "0. Exit");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    System.out.println("Balance: " + getBalance());
                    break;
                case "2":
                    System.out.println("You have successfully logged out!");
                    quit = true;
                    break;
                case "0":
                    quit = true;
                    break;
                default:
                    System.out.println("Wrong choice.");

            }
        }
    }
}