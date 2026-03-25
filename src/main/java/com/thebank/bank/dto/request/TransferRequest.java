package com.thebank.bank.dto.request;

public class TransferRequest {
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private double amount;

    public String getSourceAccountNumber() { return sourceAccountNumber; }
    public String getDestinationAccountNumber() { return destinationAccountNumber; }
    public double getAmount() { return amount; }
    public void setSourceAccountNumber(String sourceAccountNumber) { this.sourceAccountNumber = sourceAccountNumber; }
    public void setDestinationAccountNumber(String destinationAccountNumber) { this.destinationAccountNumber = destinationAccountNumber; }
    public void setAmount(double amount) { this.amount = amount; }
}