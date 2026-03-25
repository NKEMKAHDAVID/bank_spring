package com.thebank.bank.services;

import com.thebank.bank.enums.AccountType;
import com.thebank.bank.enums.CustomerTier;
import com.thebank.bank.enums.TransactionType;
import com.thebank.bank.exceptions.HoldNotFoundException;
import com.thebank.bank.exceptions.InsufficientFundsException;
import com.thebank.bank.exceptions.InvalidAccountOperationException;
import com.thebank.bank.interfaces.Interest;
import com.thebank.bank.model.Account;
import com.thebank.bank.model.CurrentAccount;
import com.thebank.bank.model.SavingsAccount;
import com.thebank.bank.transactions.Transaction;
import com.thebank.bank.transactions.TransferTransaction;
import com.thebank.bank.util.NameFormatter;
import com.thebank.bank.customer.Customer;
import org.springframework.stereotype.Service;
import com.thebank.bank.dto.response.StatementResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BankService {

    private int nextCustomerId = 1001;
    private int nextAccountNumber = 1;


    private final Map<Integer, Customer> customers = new HashMap<>();
    private final Map<String, Account> accounts = new HashMap<>();
    private final Map<Integer, List<Account>> customerAccounts = new HashMap<>();


    public Customer addCustomer(String name, CustomerTier tier){
        String cleanName = NameFormatter.format(name);
        Customer customer = new Customer(nextCustomerId, cleanName, tier);
        customers.put(nextCustomerId , customer);
        customerAccounts.put(nextCustomerId, new ArrayList<>());
        nextCustomerId++;
        return customer;
    }
    public Customer findCustomer(int customerId) throws InvalidAccountOperationException {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            throw new InvalidAccountOperationException("Customer " + customerId + " does not exist.");
        }
        return customer;
    }
    public void deleteCustomer(int customerId) throws InvalidAccountOperationException {
        Customer customer = findCustomer(customerId);
        List<Account> owned = customerAccounts.get(customerId);
        if (!owned.isEmpty()) {
            throw new InvalidAccountOperationException("Cannot delete customer " + customerId + ", they still have open accounts.");
        }
        customers.remove(customerId);
        customerAccounts.remove(customerId);
    }
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }


    public Account openAccount(int customerId, AccountType type, double openingBalance ) throws InvalidAccountOperationException{
        if(openingBalance < 0){
            throw new InvalidAccountOperationException("Opening Balance can Not be negative");
        }
        Customer customer = findCustomer(customerId);
        List<Account> owned = customerAccounts.get(customerId);
        for (Account account : owned) {
            if (account.getAccountType() == type) {
                throw new InvalidAccountOperationException("Customer already has a " + type.getDisplayName() + ".");
            }
        }
        String accountNumber = String.format("ACC-%04d", nextAccountNumber);
        nextAccountNumber++;
        Account account;
        if (type == AccountType.SAVINGS) {
            account = new SavingsAccount(accountNumber, customerId, openingBalance);
        } else {
            account = new CurrentAccount(accountNumber, customerId, openingBalance);
        }

        accounts.put(accountNumber, account);
        owned.add(account);

        return account;
    }
    public Account findAccount(String accountNumber) throws InvalidAccountOperationException{
        Account account = accounts.get(accountNumber);
        if(account == null){
            throw new InvalidAccountOperationException("Account " + accountNumber + " does not exist. ");
        }
        return account;
    }
    public void freezeAccount(String accountNumber) throws InvalidAccountOperationException{
        Account account = findAccount(accountNumber);
        account.freeze();
    }

    public void deposit (String accountNumber, double amount) throws InvalidAccountOperationException {
        Account account = findAccount(accountNumber);

        if(account.isFrozen()){
            throw new InvalidAccountOperationException("Account " + accountNumber + " is frozen");
        }
        if(amount <= 0){
            throw new InvalidAccountOperationException("THe amount can not be zero or below");
        }
        account.credit(amount);
        account.addTransaction(new Transaction(amount, TransactionType.DEPOSIT));
        System.out.println("Deposit of " + amount + " successful");
    }
    public void withdraw(String accountNumber, double amount) throws InvalidAccountOperationException, InsufficientFundsException{
        Account account =  findAccount(accountNumber);
        if(account.isFrozen()){
            throw new InvalidAccountOperationException("Account " + accountNumber + " is frozen");
        }
        if(amount > account.getAvailableBalance()){
            throw new InsufficientFundsException("Error:  Insufficient funds ");
        }
        if (amount <= 0) {
            throw new InvalidAccountOperationException("Withdrawal amount must be greater than zero.");
        }

        Customer customer = findCustomer(account.getCustomerId());
        double balanceAfter = account.getAvailableBalance() - amount ;
        if (customer.getTier() == CustomerTier.GOLD && balanceAfter == 0){

            String warning = "[WARNING] Phantom withdrawal rule triggered. " +
                    "Account: " + accountNumber +
                    " | Customer: " + customer.getName() +
                    " | Amount: " + amount;

            System.err.println(warning);
            return;
        }

        account.debit(amount);
        account.addTransaction(new Transaction(amount, TransactionType.WITHDRAWAL));
    }
    public void transfer(String sourceNumber, String destinationNumber, double amount) throws InvalidAccountOperationException, InsufficientFundsException {

        Account source = findAccount(sourceNumber);
        Account destination = findAccount(destinationNumber);
        Customer sourceCustomer = findCustomer(source.getCustomerId());

        if (source.isFrozen()) {
            throw new InvalidAccountOperationException("Source account " + sourceNumber + " is frozen.");
        }
        if (amount <= 0) {
            throw new InvalidAccountOperationException("Transfer amount must be greater than zero.");
        }

        double available = source.getAvailableBalance();
        if (amount > available) {
            throw new InsufficientFundsException("Insufficient funds in source account. " + "Available: " + available);
        }
        double balanceAfter = available - amount;
        if (sourceCustomer.getTier() == CustomerTier.GOLD && balanceAfter == 0) {
            String warning = "[WARNING] Phantom withdrawal rule triggered " +
                    "on transfer. Account: " + sourceNumber +
                    " | Amount: " + amount;
            System.err.println(warning);
            return;
        }
        source.debit(amount);
        destination.credit(amount);

        TransferTransaction record = new TransferTransaction(amount, sourceNumber, destinationNumber);
        source.addTransaction(record);
        destination.addTransaction(record);
    }


    private int nextHoldId = 1;
    public String placeHold(String accountNumber, double amount) throws InvalidAccountOperationException {

        Account account = findAccount(accountNumber);
        if (amount <= 0) {
            throw new InvalidAccountOperationException("Hold amount must be greater than zero.");
        }

        String holdId = String.format("H-%04d", nextHoldId);
        nextHoldId++;
        account.addHold(holdId, amount);
        return holdId;
    }
    public void releaseHold(String accountNumber, String holdId) throws InvalidAccountOperationException, HoldNotFoundException {

        Account account = findAccount(accountNumber);

        if (!account.holdExists(holdId)) {
            throw new HoldNotFoundException("Hold " + holdId + " does not exist on account " + accountNumber);
        }

        account.releaseHold(holdId);
    }
    public void applyEndOfDay() {
        for (Account account : accounts.values()) {
            if (account instanceof Interest) {
                Interest ib = (Interest) account;
                Customer customer = customers.get(account.getCustomerId());
                double rate = customer.getTier().getMonthlyInterestRate();
                ib.applyInterest(rate);
            }
        }
    }

    public StatementResponse printStatement(String accountNumber)
            throws InvalidAccountOperationException {

        Account account = findAccount(accountNumber);
        Customer customer = findCustomer(account.getCustomerId());

        return new StatementResponse(
                account.getAccountNumber(),
                customer.getName(),
                account.getAccountType().getDisplayName(),
                account.getPrincipalBalance(),
                account.getAvailableBalance(),
                account.isFrozen() ? "FROZEN" : "ACTIVE",
                account.getTransactionHistory()
        );
    }
    public List<Customer> listCustomers() {
        return new ArrayList<>(customers.values());
    }

}
