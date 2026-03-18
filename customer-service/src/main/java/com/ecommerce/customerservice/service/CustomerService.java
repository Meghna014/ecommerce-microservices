package com.ecommerce.customerservice.service;

import com.ecommerce.customerservice.entity.Customer;
import com.ecommerce.customerservice.exception.CustomerNotFoundException;
import com.ecommerce.customerservice.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer register(Customer customer)
    {
        //check if mail already exist
          if(customerRepository.findByEmail(customer.getEmail()).isPresent())
          {
              throw new IllegalArgumentException("Email already registered!!");
          }

          //Set default role to USER
        if(Objects.isNull(customer.getRole()))
        {
            customer.setRole("USER");
        }

        return  customerRepository.save(customer);
    }

    public Customer login(String username , String password)
    {
       Customer customer =  customerRepository.findByEmail(username).orElseThrow(()
               -> new CustomerNotFoundException("Customer not found with email "+ username));

       if(!customer.getPassword().equals(password))
       {
           throw new IllegalArgumentException("Invalid Password!!");
       }

       return customer;
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }


    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        Customer existing = getCustomerById(id);
        existing.setName(updatedCustomer.getName());
        existing.setEmail(updatedCustomer.getEmail());
        existing.setPhone(updatedCustomer.getPhone());
        existing.setAddress(updatedCustomer.getAddress());
        return customerRepository.save(existing);
    }


    public void deleteCustomer(Long id) {
        Customer existing = getCustomerById(id);
        customerRepository.delete(existing);
    }
}
