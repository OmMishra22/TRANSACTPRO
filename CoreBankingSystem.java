import java.sql.*;
import java.util.Scanner;

public class CoreBankingSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/banking_db";
    private static final String USER = "root";
    private static final String PASSWORD = "password";
    private static Connection conn;

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to Database Successfully!");
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\n1. Add Customer\n2. Create Account\n3. Deposit\n4. Withdraw\n5. View Balance\n6. Fund Transfer\n7. Exit");
                System.out.print("Enter choice: ");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1: addCustomer(); break;
                    case 2: createAccount(); break;
                    case 3: deposit(); break;
                    case 4: withdraw(); break;
                    case 5: viewBalance(); break;
                    case 6: fundTransfer(); break;
                    case 7: System.out.println("Exiting..."); return;
                    default: System.out.println("Invalid choice. Try again!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addCustomer() throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Customer Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Mobile Number: ");
        String mobile = sc.nextLine();
        PreparedStatement pst = conn.prepareStatement("INSERT INTO Customers (name, mobile) VALUES (?, ?)");
        pst.setString(1, name);
        pst.setString(2, mobile);
        pst.executeUpdate();
        System.out.println("Customer Added Successfully!");
    }

    private static void createAccount() throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Customer ID: ");
        int custId = sc.nextInt();
        System.out.print("Enter Initial Deposit: ");
        double balance = sc.nextDouble();
        PreparedStatement pst = conn.prepareStatement("INSERT INTO Accounts (customer_id, balance) VALUES (?, ?)");
        pst.setInt(1, custId);
        pst.setDouble(2, balance);
        pst.executeUpdate();
        System.out.println("Account Created Successfully!");
    }

    private static void deposit() throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Account ID: ");
        int accId = sc.nextInt();
        System.out.print("Enter Deposit Amount: ");
        double amount = sc.nextDouble();
        PreparedStatement pst = conn.prepareStatement("UPDATE Accounts SET balance = balance + ? WHERE account_id = ?");
        pst.setDouble(1, amount);
        pst.setInt(2, accId);
        pst.executeUpdate();
        System.out.println("Amount Deposited Successfully!");
    }

    private static void withdraw() throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Account ID: ");
        int accId = sc.nextInt();
        System.out.print("Enter Withdrawal Amount: ");
        double amount = sc.nextDouble();
        PreparedStatement pst = conn.prepareStatement("UPDATE Accounts SET balance = balance - ? WHERE account_id = ? AND balance >= ?");
        pst.setDouble(1, amount);
        pst.setInt(2, accId);
        pst.setDouble(3, amount);
        int rows = pst.executeUpdate();
        if (rows > 0) {
            System.out.println("Withdrawal Successful!");
        } else {
            System.out.println("Insufficient Balance!");
        }
    }

    private static void viewBalance() throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Account ID: ");
        int accId = sc.nextInt();
        PreparedStatement pst = conn.prepareStatement("SELECT balance FROM Accounts WHERE account_id = ?");
        pst.setInt(1, accId);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            System.out.println("Account Balance: " + rs.getDouble(1));
        } else {
            System.out.println("Account Not Found!");
        }
    }

    private static void fundTransfer() throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Sender Account ID: ");
        int senderId = sc.nextInt();
        System.out.print("Enter Receiver Account ID: ");
        int receiverId = sc.nextInt();
        System.out.print("Enter Transfer Amount: ");
        double amount = sc.nextDouble();

        conn.setAutoCommit(false);
        try {
            PreparedStatement withdrawPst = conn.prepareStatement("UPDATE Accounts SET balance = balance - ? WHERE account_id = ? AND balance >= ?");
            withdrawPst.setDouble(1, amount);
            withdrawPst.setInt(2, senderId);
            withdrawPst.setDouble(3, amount);
            int rowsWithdraw = withdrawPst.executeUpdate();

            if (rowsWithdraw > 0) {
                PreparedStatement depositPst = conn.prepareStatement("UPDATE Accounts SET balance = balance + ? WHERE account_id = ?");
                depositPst.setDouble(1, amount);
                depositPst.setInt(2, receiverId);
                int rowsDeposit = depositPst.executeUpdate();

                if (rowsDeposit > 0) {
                    conn.commit();
                    System.out.println("Fund Transfer Successful!");
                } else {
                    conn.rollback();
                    System.out.println("Transfer Failed! Receiver Account Not Found.");
                }
            } else {
                conn.rollback();
                System.out.println("Transfer Failed! Insufficient Balance or Invalid Sender Account.");
            }
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
