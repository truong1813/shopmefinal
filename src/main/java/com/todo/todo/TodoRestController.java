package com.todo.todo;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todo.Utility;
import com.todo.common.entity.Customer;
import com.todo.common.entity.Todo;
import com.todo.customer.CustomerRepository;

@RestController
public class TodoRestController {
	
	@Autowired private CustomerRepository repo;
	@Autowired private TodoService service;
	@Autowired private TodoRepository todoRopo;
	@PostMapping("todos/check_group_name_unique")
	
	public String checkNameUnique(String name,Integer todoId,Integer groupId, HttpServletRequest request) {
		Customer  customer= getEmailOfAuthenticatedCustomer(request) ;
		
		return service.checkGroupUnique(name,todoId, groupId,customer);
	}
	
	private Customer getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		return repo.findByEmail(email);
	}
	
	
	@GetMapping("/todos/list_todos")
	
	public List<TodoDTO> listTodos(HttpServletRequest request){
		Customer customer = getEmailOfAuthenticatedCustomer(request);
		List<Todo> listTodos = todoRopo.findChildrenTodos(customer);
		TodoDTO  todoDTO = new TodoDTO();
		List<TodoDTO> listTodoDTO = new ArrayList<>();
		for(Todo todo: listTodos) {
			todoDTO.setId(todo.getId());
			todoDTO.setName(todo.getName());
			todoDTO.setStatus(todo.getStatus());
			listTodoDTO.add(todoDTO);
		}
		return listTodoDTO;
	}

}
