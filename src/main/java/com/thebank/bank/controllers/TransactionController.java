package com.thebank.bank.controllers;

import com.thebank.bank.dto.request.AmountRequest;
import com.thebank.bank.dto.request.TransferRequest;
import com.thebank.bank.exceptions.InsufficientFundsException;
import com.thebank.bank.exceptions.InvalidAccountOperationException;
import com.thebank.bank.services.BankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final BankService bank;

    public TransactionController(BankService bank) {
        this.bank = bank;
    }



    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<?> deposit(
            @PathVariable String accountNumber,
            @RequestBody AmountRequest request) {
        try {
            bank.deposit(accountNumber, request.getAmount());
            return ResponseEntity.ok("Deposit of " + request.getAmount() +
                    " into " + accountNumber + " successful.");
        } catch (InvalidAccountOperationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<?> withdraw(
            @PathVariable String accountNumber,
            @RequestBody AmountRequest request) {
        try {
            bank.withdraw(accountNumber, request.getAmount());
            return ResponseEntity.ok("Withdrawal of " + request.getAmount() +
                    " from " + accountNumber + " successful.");
        } catch (InvalidAccountOperationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (InsufficientFundsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        try {
            bank.transfer(
                    request.getSourceAccountNumber(),
                    request.getDestinationAccountNumber(),
                    request.getAmount()
            );
            return ResponseEntity.ok("Transfer of " + request.getAmount() +
                    " from " + request.getSourceAccountNumber() +
                    " to " + request.getDestinationAccountNumber() +
                    " successful.");
        } catch (InvalidAccountOperationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (InsufficientFundsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}