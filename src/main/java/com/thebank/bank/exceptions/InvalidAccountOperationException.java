package com.thebank.bank.exceptions;

public class InvalidAccountOperationException extends Exception{
    public InvalidAccountOperationException (String message){
        super(message);
    }
}
