package com.thebank.bank.controllers;

import com.thebank.bank.dto.request.OpenAccountRequest;
import com.thebank.bank.exceptions.InvalidAccountOperationException;
import com.thebank.bank.services.BankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.thebank.bank.model.Account;
@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final BankService bank;

    public AccountController(BankService bank) {
        this.bank = bank;
    }


    @PostMapping("/openAccount")
    public ResponseEntity<?> openAccount(@RequestBody OpenAccountRequest request) {
        try {
            Account account = bank.openAccount(request.getCustomerId(), request.getAccountType(), request.getOpeningBalance());
            return ResponseEntity.ok(account);
        } catch (InvalidAccountOperationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<?> findAccount(@PathVariable String accountNumber) {
        try {
            Account account = bank.findAccount(accountNumber);
            return ResponseEntity.ok(account);
        } catch (InvalidAccountOperationException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/{accountNumber}/freeze")
    public ResponseEntity<?> freezeAccount(@PathVariable String accountNumber) {
        try {
            bank.freezeAccount(accountNumber);
            return ResponseEntity.ok("Account " + accountNumber + " frozen successfully.");
        } catch (InvalidAccountOperationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}