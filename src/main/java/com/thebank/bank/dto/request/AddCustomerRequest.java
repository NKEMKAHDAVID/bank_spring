package com.thebank.bank.dto.request;

import com.thebank.bank.enums.CustomerTier;

public class AddCustomerRequest {
    private String name;
    private CustomerTier tier;

    public String getName() { return name; }
    public CustomerTier getTier() { return tier; }
    public void setName(String name) { this.name = name; }
    public void setTier(CustomerTier tier) { this.tier = tier; }
}