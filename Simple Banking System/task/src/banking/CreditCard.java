package banking;


import java.sql.*;

public class CreditCard {
    private final String cardNumber;
    private final String pin;

    public CreditCard() {
        this.cardNumber = generateCardNumber();
        this.pin = generatePin();
        printCreated();
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPin() {
        return pin;
    }

    public String generateCardNumber() {
        String bankIdNumber = "400000";
        StringBuilder accountID = new StringBuilder();
        int remainingDigits = 9;

        do {
            while (accountID.length() < remainingDigits) {
                accountID.append((int) (Math.random() * 10));
            }
        } while (!isAccIdUnique(String.valueOf(accountID)));

        String numberMinusLast = bankIdNumber + accountID;
        return bankIdNumber + accountID + getCheckSum(numberMinusLast);
    }

    public boolean isAccIdUnique(String accountID) {
        String searchIdSQL = "SELECT number FROM card " +
                "WHERE number LIKE '% ? %'";
        try (Connection con = DriverManager.getConnection(BankAccount.getDataBase());
             PreparedStatement searchId = con.prepareStatement(searchIdSQL)) {
            searchId.setString(1, accountID);
            ResultSet rs = searchId.executeQuery();
            if (rs.next()) {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }

    // Generate checksum for cardNumber using LUHN ALGORITHM
    public static String getCheckSum(String numberMinusLast) {
        int sum = 0;
        String[] partNumber = numberMinusLast.split("");
        int[] digits = new int[partNumber.length];
        for (int i = 0; i < partNumber.length; i++) {
            digits[i] = Integer.parseInt(partNumber[i]);
            if (i % 2 == 0) {
                // multiply even INDEXED[i] digit by 2. If result is more than 9, subtract 9
                digits[i] = digits[i] * 2 > 9 ? digits[i] * 2 - 9 : digits[i] * 2;
            }
            sum += digits[i];
        }
        //checksum must be 0 not 10 when sum%10 == 0;
        int checkSum = sum % 10 == 0 ? 0 : 10 - sum % 10;
        return String.valueOf(checkSum);
    }

    public String generatePin() {
        StringBuilder pinNumber = new StringBuilder();
        int digits = 4;
        while (pinNumber.length() < digits) {
            pinNumber.append((int) (Math.random() * 10));
        }
        return pinNumber.toString();
    }

    public void printCreated() {
        System.out.println("Your card has been created\n" +
                "Your card number:\n" +
                cardNumber +
                "\nYour card PIN:\n" +
                pin);
    }

}