package com.thebank.bank.model;

import com.thebank.bank.enums.AccountType;

public class CurrentAccount extends Account {

    public CurrentAccount(String accountNumber, int customerId,
                          double openingBalance) {
        super(accountNumber, AccountType.CURRENT, customerId, openingBalance);
    }
}