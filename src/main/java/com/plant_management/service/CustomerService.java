package com.plant_management.service;

import com.plant_management.model.Customer;
import com.plant_management.dao.CustomerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerDao customerDao;

    public Customer addCustomer(Customer customer) {
        return customerDao.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerDao.findAll();
    }

    public Customer getCustomerById(int id) {
        return customerDao.findById(id).orElse(null);
    }

    public Customer updateCustomer(int id, Customer updatedCustomer) {
        updatedCustomer.setCustomer_id(id);
        return customerDao.save(updatedCustomer);
    }

    public void deleteCustomer(int id) {
        customerDao.deleteById(id);
    }
}