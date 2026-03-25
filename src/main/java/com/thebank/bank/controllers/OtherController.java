package com.thebank.bank.controllers;

import com.thebank.bank.dto.request.AmountRequest;
import com.thebank.bank.exceptions.HoldNotFoundException;
import com.thebank.bank.exceptions.InvalidAccountOperationException;
import com.thebank.bank.services.Bank;
import com.thebank.bank.customer.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.thebank.bank.dto.response.StatementResponse;
import java.util.List;

@RestController
@RequestMapping("/api/other")
public class OtherController {

    private final Bank bank;

    public OtherController(Bank bank) {
        this.bank = bank;
    }

    @PostMapping("/{accountNumber}/hold")
    public ResponseEntity<?> placeHold(
            @PathVariable String accountNumber,
            @RequestBody AmountRequest request) {
        try {
            String holdId = bank.placeHold(accountNumber, request.getAmount());
            return ResponseEntity.ok("Hold placed successfully. Hold ID: " + holdId);
        } catch (InvalidAccountOperationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{accountNumber}/hold/{holdId}")
    public ResponseEntity<?> releaseHold(
            @PathVariable String accountNumber,
            @PathVariable String holdId) {
        try {
            bank.releaseHold(accountNumber, holdId);
            return ResponseEntity.ok("Hold " + holdId + " released successfully.");
        } catch (InvalidAccountOperationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (HoldNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/end-of-day")
    public ResponseEntity<?> applyEndOfDay() {
        bank.applyEndOfDay();
        return ResponseEntity.ok("End of day interest applied to all savings accounts.");
    }

    @GetMapping("/{accountNumber}/statement")
    public ResponseEntity<?> printStatement(
            @PathVariable String accountNumber) {
        try {
            StatementResponse statement = bank.printStatement(accountNumber);
            return ResponseEntity.ok(statement);
        } catch (InvalidAccountOperationException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/customers")
    public ResponseEntity<?> listCustomers() {
        List<Customer> customers = bank.listCustomers();
        if (customers.isEmpty()) {
            return ResponseEntity.status(404).body("No customers found.");
        }
        return ResponseEntity.ok(customers);
    }
}