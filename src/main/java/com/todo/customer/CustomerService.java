package com.todo.customer;


import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.todo.common.entity.AuthenticationType;
import com.todo.common.entity.Customer;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class CustomerService {
	@Autowired private CustomerRepository customerRepo;
	
	@Autowired PasswordEncoder passwordEncoder;
	
	
	public boolean checkEmailUnique(Integer id,String email) {
		Customer customer = customerRepo.findByEmail(email);
		if(customer == null) return true;
		
		boolean isCreateNew = (id==null);
		if(isCreateNew) {
			if(customer !=null) return false;
		}else {
			if(customer.getId() !=id) return false;
		}
		return true;
	}
	
	public boolean verify(String verificationCode) {
		Customer customer = customerRepo.findByVerificationCode(verificationCode);
		if(customer == null || customer.isEnabled()) {
			return false;
		}else {
			customerRepo.enable(customer.getId());
			return true;
		}
	}
	
	public void registerCustomer(Customer customer) {
		encodePassword(customer);
		customer.setEnabled(false);
	
		customer.setAuthenticationType(AuthenticationType.DATABASE);
		String randomCode = RandomString.make(64);
		customer.setVerificationCode(randomCode);
		customerRepo.save(customer);  
	}

	private void encodePassword(Customer customer) {
		String encodePassword= passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodePassword);
		
	}
	
	public void updateAuthentication(Customer customer, AuthenticationType type) {
		if(!customer.getAuthenticationType().equals(type)) {
			customerRepo.updateAuthenticationType(customer.getId(), type);
		}
	}
	public Customer getCustomerByEmail(String email) {
		return customerRepo.findByEmail(email);
	}

	public void addNewCustomerUponOAuthLogin(String name, String email, String countryCode,
			AuthenticationType authenticationType) {
		Customer  customer = new Customer();
		
		setName(name, customer);
		
		customer.setEmail(email);
		
		customer.setEnabled(true);
		customer.setAuthenticationType(authenticationType);
		customer.setPassword("");
				
		
		customerRepo.save(customer);
	}
	
	public void setName(String name, Customer customer) {
		customer.setName(name);
		
	}
	
	public void updateCustomer(Customer customerInForm) {
		Customer customerDB = customerRepo.findById(customerInForm.getId()).get();
		
		if(customerInForm.getAuthenticationType().equals(AuthenticationType.DATABASE)){
			if(!customerInForm.getPassword().isEmpty()) {
				encodePassword(customerInForm);
				
			}else {
				
				customerInForm.setPassword(customerDB.getPassword());
			}
		}else {
			customerInForm.setPassword(customerDB.getPassword());	
		}
		
		customerInForm.setEnabled(customerDB.isEnabled());
		
		customerInForm.setVerificationCode(customerDB.getVerificationCode());
		customerInForm.setAuthenticationType(customerDB.getAuthenticationType());
		customerInForm.setResetPasswordToken(customerDB.getResetPasswordToken());
		
		customerRepo.save(customerInForm);
		}
	
		public Customer getByResetPasswordToken(String token) {
			return customerRepo.findByToken(token);
	
		}
		
		public String updateResetPasswordToken(String email) throws CustomerNotFoundException {
			Customer  customer = customerRepo.findByEmail(email);
			if(customer !=null) {
				String token = RandomString.make(30);
				customer.setResetPasswordToken(token);
				customerRepo.save(customer);
				return token;
			}else {
				throw new CustomerNotFoundException("Could not find any customer with email: " + email);
			}
		}
		 public void updateNewPasswordForCustomer(String token, String password) throws CustomerNotFoundException {
			Customer customer = customerRepo.findByToken(token);
			if(customer ==null) {
				throw new CustomerNotFoundException("No customer found: Invalid token");
			}
			
			customer.setPassword(password);
			customer.setResetPasswordToken(null);
			encodePassword(customer);
			customerRepo.save(customer);
		 }

}
