package com.thebank.bank.customer;

import com.thebank.bank.enums.CustomerTier;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {

    private final int id;
    private String name;
    private CustomerTier tier;

    public Customer(int id, String name, CustomerTier tier) {
        this.id = id;
        this.name = name;
        this.tier = tier;
    }
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CustomerTier getTier() {
        return tier;
    }
    @Override
    public String toString() {
        return String.format("Customer [ID: %d | Name: %s | Tier: %s]", id, name, tier);
    }
}