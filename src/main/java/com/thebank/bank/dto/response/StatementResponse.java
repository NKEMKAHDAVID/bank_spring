package com.thebank.bank.dto.response;

import com.thebank.bank.transactions.Transaction;
import java.util.List;

public class StatementResponse {

    private String accountNumber;
    private String ownerName;
    private String accountType;
    private double principalBalance;
    private double availableBalance;
    private String status;
    private List<Transaction> transactions;

    public StatementResponse(String accountNumber, String ownerName,
                             String accountType, double principalBalance,
                             double availableBalance, String status,
                             List<Transaction> transactions) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.accountType = accountType;
        this.principalBalance = principalBalance;
        this.availableBalance = availableBalance;
        this.status = status;
        this.transactions = transactions;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public String getAccountType() { return accountType; }
    public double getPrincipalBalance() { return principalBalance; }
    public double getAvailableBalance() { return availableBalance; }
    public String getStatus() { return status; }
    public List<Transaction> getTransactions() { return transactions; }
}