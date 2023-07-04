package com.todo.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.todo.common.entity.AuthenticationType;
import com.todo.common.entity.Customer;
import com.todo.customer.CustomerService;
@Component
public class DatabaseLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	@Autowired CustomerService customerService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		CustomerUserDetails  userDetail = (CustomerUserDetails) authentication.getPrincipal();
		Customer customer = userDetail.getCustomer();
		
		customerService.updateAuthentication(customer, AuthenticationType.DATABASE);
		super.onAuthenticationSuccess(request, response, authentication);
	}
	
	
}
