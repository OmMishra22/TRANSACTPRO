-- Database Creation
CREATE DATABASE CoreBankingDB;
USE CoreBankingDB;

-- Customers Table
CREATE TABLE Customers (
    CustomerID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    DOB DATE,
    Email VARCHAR(100) UNIQUE,
    Phone VARCHAR(15) UNIQUE,
    Address TEXT
);

-- Accounts Table
CREATE TABLE Accounts (
    AccountID INT AUTO_INCREMENT PRIMARY KEY,
    CustomerID INT,
    AccountType ENUM('Savings', 'Current', 'Fixed Deposit'),
    Balance DECIMAL(15,2) DEFAULT 0.00,
    Status ENUM('Active', 'Inactive') DEFAULT 'Active',
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) ON DELETE CASCADE
);

-- Transactions Table
CREATE TABLE Transactions (
    TransactionID INT AUTO_INCREMENT PRIMARY KEY,
    AccountID INT,
    TransactionType ENUM('Deposit', 'Withdrawal', 'Transfer'),
    Amount DECIMAL(15,2),
    TransactionDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (AccountID) REFERENCES Accounts(AccountID) ON DELETE CASCADE
);

-- Fund Transfers Table
CREATE TABLE FundTransfers (
    TransferID INT AUTO_INCREMENT PRIMARY KEY,
    FromAccountID INT,
    ToAccountID INT,
    Amount DECIMAL(15,2),
    TransferDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (FromAccountID) REFERENCES Accounts(AccountID) ON DELETE CASCADE,
    FOREIGN KEY (ToAccountID) REFERENCES Accounts(AccountID) ON DELETE CASCADE
);
