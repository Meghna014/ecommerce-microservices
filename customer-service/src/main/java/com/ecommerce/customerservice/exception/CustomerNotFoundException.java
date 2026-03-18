package com.ecommerce.customerservice.exception;

public class CustomerNotFoundException extends  RuntimeException{
    public CustomerNotFoundException(String msg)
    {
        super(msg);
    }
}
