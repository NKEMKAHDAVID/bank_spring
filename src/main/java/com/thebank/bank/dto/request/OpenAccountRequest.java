package com.thebank.bank.dto.request;

import com.thebank.bank.enums.AccountType;

public class OpenAccountRequest {
    private int customerId;
    private AccountType accountType;
    private double openingBalance;

    public int getCustomerId() { return customerId; }
    public AccountType getAccountType() { return accountType; }
    public double getOpeningBalance() { return openingBalance; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }
    public void setOpeningBalance(double openingBalance) { this.openingBalance = openingBalance; }
}