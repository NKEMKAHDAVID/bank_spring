package com.thebank.bank.model;

import com.thebank.bank.enums.AccountType;
import com.thebank.bank.interfaces.Interest;

public class SavingsAccount extends Account implements Interest {

    public SavingsAccount(String accountNumber, int customerId, double openingBalance) {

        super(accountNumber, AccountType.SAVINGS, customerId, openingBalance);
    }



    @Override
    public void applyInterest(double monthlyInterestRate) {
        double interest = getPrincipalBalance() * monthlyInterestRate;
        setPrincipalBalance(getPrincipalBalance() + interest);
    }
}