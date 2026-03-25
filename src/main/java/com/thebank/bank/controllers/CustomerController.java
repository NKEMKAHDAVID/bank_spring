package com.thebank.bank.controllers;

import com.thebank.bank.dto.request.AddCustomerRequest;
import com.thebank.bank.exceptions.InvalidAccountOperationException;
import com.thebank.bank.services.Bank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.thebank.bank.customer.Customer;
import java.util.List;
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final Bank bank;

    public CustomerController(Bank bank) {
        this.bank = bank;
    }

    @PostMapping("/addCustomer")
    public ResponseEntity<?> addCustomer(@RequestBody AddCustomerRequest request){
            Customer customer = bank.addCustomer(request.getName(), request.getTier());
            return ResponseEntity.ok(customer);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> findCustomer(@PathVariable int customerId){
        try{
            Customer customer = bank.findCustomer(customerId);
            return ResponseEntity.ok(customer);
        }catch(InvalidAccountOperationException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> deleteCustomer(@PathVariable int customerId){
        try{
            bank.deleteCustomer(customerId);
            return ResponseEntity.ok("Customer " + customerId + " deleted successfully ");
        }catch (InvalidAccountOperationException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllCustomers() {
        List<Customer> customers = bank.getAllCustomers();
        if (customers.isEmpty()) {
            return ResponseEntity.status(404).body("No customers found.");
        }
        return ResponseEntity.ok(customers);
    }
}
