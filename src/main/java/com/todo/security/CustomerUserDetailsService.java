package com.todo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.todo.common.entity.Customer;
import com.todo.customer.CustomerRepository;

public class CustomerUserDetailsService implements UserDetailsService {
	@Autowired CustomerRepository repo;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Customer customer = repo.findByEmail(email);
		if(customer !=null) return new CustomerUserDetails(customer);
		throw new UsernameNotFoundException("Could not find user with email:" + email);
	}

}
