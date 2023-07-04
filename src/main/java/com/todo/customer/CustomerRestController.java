package com.todo.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class CustomerRestController {
	@Autowired CustomerService customerService;
	
	@PostMapping("/customers/check_unique")
	
	public String checkEmailUnique(Integer id, String email) {
		return customerService.checkEmailUnique(id, email)?"OK":"Duplicated";
	}
	
}
