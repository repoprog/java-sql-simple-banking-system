package banking;


import java.sql.*;
import java.util.Scanner;

public class BankAccount {
    static Scanner scanner = new Scanner(System.in);
    private int balance;
    private CreditCard card;
    private static String dataBase;

    public BankAccount() {
        this.balance = 0;
        card = new CreditCard();
        addAccountToDatabase();
    }

    public static void setDataBase(String dataBase) {
        BankAccount.dataBase = dataBase;
    }

    public static String getDataBase() {
        return dataBase;
    }

    private static int getBalance(String cardNumber) {
        int balance = -1;
        String url = "jdbc:sqlite:" + dataBase;
        String selectBalance = "SELECT id, balance FROM card WHERE number = ?";

        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement pstmt = con.prepareStatement(selectBalance)) {
            pstmt.setString(1, cardNumber);
            ResultSet rs = pstmt.executeQuery();//zawiera tylko kolumny id, balance!!
            while (rs.next()) {

                balance = rs.getInt("balance");
                System.out.println("Balance: " + balance);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return balance;
    }

    private static void addMoney(String cardNumber) {
        System.out.println("Enter income: ");
        int deposit = scanner.nextInt();
        scanner.nextLine();
        String insert = "UPDATE card SET balance = (balance + ?) WHERE number = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dataBase);
             PreparedStatement preparedStatement = conn.prepareStatement(insert)) {

            preparedStatement.setInt(1, deposit);// index of ? VALUES
            preparedStatement.setString(2, cardNumber);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Income was added!\n");

    }

    private static void transferMoney(String cardNumber) {

        System.out.println("Enter card number: ");
        String transferNumber = scanner.nextLine();
        boolean isSameAccount = cardNumber.equals(transferNumber);

        if (isSameAccount) {
            System.out.println("You can't transfer money to the same account!");
            return;
        } else if (!isNumberValid(transferNumber)) {
            System.out.println("Probably you made a mistake in the card number" +
                    ". Please try again!");
            return;
        }

        if (isInDataBase(transferNumber)) {
            System.out.println("Enter how much money you want to transfer:");
            int amount = scanner.nextInt();
            scanner.nextLine();
            if (amount <= getBalance(cardNumber)) {
                makeTransaction(cardNumber, transferNumber, amount);
            } else {
                System.out.println("Not Not enough money!");
            }
        } else {
            System.out.println("Such a card does not exist.");

        }
    }


    private static void makeTransaction(String cardNumber, String transferNumber, int amount) {
        String insertWithdrawSQL = "UPDATE card SET balance = (balance - ?) WHERE number = ?";
        String insertDepositSQL = "UPDATE card SET balance = (balance + ?) WHERE number = ?";

        try (Connection con = DriverManager.getConnection("jdbc:sqlite:" + dataBase)) {

            // Disable auto-commit mode
            con.setAutoCommit(false);

            try (PreparedStatement insertWithdraw = con.prepareStatement(insertWithdrawSQL);
                 PreparedStatement insertDeposit = con.prepareStatement(insertDepositSQL)) {

                // Insert a withdraw
                insertWithdraw.setInt(1, amount);// index of ? VALUES
                insertWithdraw.setString(2, cardNumber);
                insertWithdraw.executeUpdate();

                // Insert a deposit
                insertDeposit.setInt(1, amount);// index of ? VALUES
                insertDeposit.setString(2, transferNumber);
                insertDeposit.executeUpdate();

                con.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Success!\n");
    }


    public static boolean isNumberValid(String transferNumber) {
        String givenCheckSum;
        if (transferNumber.length() == 16) {
            givenCheckSum = String.valueOf(transferNumber.charAt(15));
            // starts at 0 end at char 15 (exclusive), takes numbers 1-15
            transferNumber = transferNumber.substring(0, transferNumber.length() - 1);
        } else {
            return false;
        }
        return givenCheckSum.equals(CreditCard.getCheckSum(transferNumber));
    }

    public static boolean isInDataBase(String transferNumber) {
        String searchNumberSQL = "SELECT number FROM card WHERE number = ?";

        try (Connection con = DriverManager.getConnection("jdbc:sqlite:" + dataBase);
             PreparedStatement searchNumber = con.prepareStatement(searchNumberSQL)) {
            searchNumber.setString(1, transferNumber);
            ResultSet rs = searchNumber.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    private static void closeAccount(String cardNumber) {
        String url = "jdbc:sqlite:" + dataBase;
        //Not all parameters have to be present, id is not present and autoincrement with new row added
        String insert = "DELETE FROM card WHERE number = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = conn.prepareStatement(insert)) {

            preparedStatement.setString(1, cardNumber);// index of ? VALUES
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("The account has been closed!\n");
    }

    private void addAccountToDatabase() {
        String url = "jdbc:sqlite:" + dataBase;
        //Not all parameters have to be present, id is not present and autoincrement with new row added
        String insert = "INSERT INTO card(number, pin, balance) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = conn.prepareStatement(insert)) {

            preparedStatement.setString(1, card.getCardNumber());// index of ? VALUES
            preparedStatement.setString(2, card.getPin());
            preparedStatement.setInt(3, balance);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void isAuthorised(String enteredCard, String enteredPin) {
        String number = null, pin = null;

        String authoriseSQL = "SELECT number, pin FROM card WHERE number = ?";

        try (Connection con = DriverManager.getConnection("jdbc:sqlite:" + dataBase);
             PreparedStatement authorise = con.prepareStatement(authoriseSQL)) {
            authorise.setString(1, enteredCard);
            ResultSet rs = authorise.executeQuery();//zawiera tylko kolumny id, balance!!
            while (rs.next()) {
                number = rs.getString(1);
                System.out.println(number);
                pin = rs.getString(2);
                System.out.println(pin);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (number != null) {
            assert pin != null;
            if (pin.equals(enteredPin)) {

                System.out.println("You have successfully logged in!");
                loggedMenu(enteredCard);

                return;
            }
        }

        System.out.println("Wrong card number or PIN!");
    }

    private static void loggedMenu(String cardNumber) {
        boolean quit = false;
        while (!quit) {
            System.out.println("1. Balance\n" +
                    "2. Add income\n" +
                    "3. Do transfer\n" +
                    "4. Close account\n" +
                    "5. Log out\n" +
                    "0. Exit");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    getBalance(cardNumber);
                    break;
                case "2":
                    addMoney(cardNumber);
                    break;
                case "3":
                    transferMoney(cardNumber);
                    break;
                case "4":
                    closeAccount(cardNumber);
                    quit = true;
                    break;
                case "5":
                    System.out.println("You have successfully logged out!");
                    quit = true;
                    break;
                case "0":
                    System.out.println("Bye!");
                    Main.quit = true;
                    quit = true;
                    break;
                default:
                    System.out.println("Wrong choice.");

            }
        }
    }
}
