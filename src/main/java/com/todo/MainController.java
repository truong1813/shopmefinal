package com.todo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import com.todo.common.entity.Customer;
import com.todo.common.entity.Todo;
import com.todo.customer.CustomerService;
import com.todo.todo.TodoRepository;

@Controller
public class MainController {
	@Autowired TodoRepository todoRepo;
	@Autowired private CustomerService customerService;
	@GetMapping("")
	public String viewHomePage(HttpServletRequest request, Model model) {
		
		Customer customer = getCustomerByEmail(request);
		List<Todo> listCompletedSearch = todoRepo.findByCustomerAndStatus(customer, "Completed");
		List<Todo> listPendingSearch = todoRepo.findByCustomerAndStatus(customer, "Pending");
		int pending = 0;
		int deadline = 0;
		Date testDay = new Date();
		for(Todo todo: listPendingSearch) {
			
			if(testDay.after(todo.getCompletedDate())){
				deadline ++;
			}
		}
		
		
		model.addAttribute("listPending",listPendingSearch.size());
		model.addAttribute("listDeadLine",deadline);
		model.addAttribute("listCompleted", listCompletedSearch.size());
		
		
		
		
		return "index";
	}
	
	
	@GetMapping("/login")
	public String viewLoginPage() {
		Authentication  authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication==null|| authentication instanceof AnonymousAuthenticationToken) {
			return "login";
		}
		return "redirect:/";
	}
	private Customer getCustomerByEmail(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		return customerService.getCustomerByEmail(email);
	}
	
	

}
