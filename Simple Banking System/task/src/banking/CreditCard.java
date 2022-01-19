package banking;


public class CreditCard {
    private String cardNumber;
    private String pin;
    private BankAccount account;

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
        int count = 0;
        while (count < remainingDigits) {
            accountID.append((int) (Math.random() * 10));
            count++;
        }
        String numberMinusLast = bankIdNumber + accountID;
        return bankIdNumber + accountID + getCheckSum(numberMinusLast);
    }

    private String getCheckSum(String numberMinusLast) { // LUHN ALGORITHM
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
        int count = 0;
        while (count < digits) {
            pinNumber.append((int) (Math.random() * 10));
            count++;
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