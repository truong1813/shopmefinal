package com.todo.todo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.webjars.NotFoundException;

import com.todo.Utility;
import com.todo.common.entity.Customer;
import com.todo.common.entity.Todo;
import com.todo.customer.CustomerRepository;
import com.todo.customer.CustomerService;

@Controller
public class TodoController {
	@Autowired private TodoService service;
	@Autowired private  CustomerService customerService;
	@Autowired private  TodoRepository todoReepo;
	@Autowired private CustomerRepository customerRepo;
	
	@InitBinder //dung de cau hinh truoc viec ap dung du lieu vao cac doi tuong java
	protected void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	binder.registerCustomEditor(Date.class, new CustomDateEditor(dateformat,false));
	};
	@GetMapping("/todos/page/pending")
	
	public String todoPending(HttpServletRequest request,Model model) throws Exception {
		
		
		return listByPageForDatePending(model, 1, "asc", "name",request);
	}
	@GetMapping("/todos/page/pending/{pageNum}")
	public String listByPageForDatePending(Model model, @PathVariable(name = "pageNum")int pageNum, @Param("sortDir") String sortDir,
			@Param("sortField")String sortField,HttpServletRequest request) throws Exception {
		if(sortDir ==null|| sortDir.isEmpty()) {
			sortDir = "asc";
		}
		

		String keyword = request.getParameter("keyword");
		String fromDate= request.getParameter("fromDate");
		String toDate = request.getParameter("toDate");
		
		Customer customer = getCustomerByEmail(request);
		
		TodoPageInfo pageInfo = new TodoPageInfo();
		
		List<Todo> listTodos = service.listByPageForDate(pageInfo, keyword, fromDate, toDate, customer, sortField, sortDir, pageNum);
		
		
		String message = "Có " + getElementSearch(listTodos) + " kết quả được tìm thấy";
		
		
		long startCount = (pageNum-1)*service.TODO_PER_PAGE +1;
		long endCount = startCount + service.TODO_PER_PAGE -1;
		if(endCount>pageInfo.getTotalElements()) {
			endCount = pageInfo.getTotalElements();
		}
		String reverseSortDir = sortDir.equals("asc")?"desc":"asc";
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageInfo.getTotalElements());
		model.addAttribute("totalPages", pageInfo.getTotalPage());
		
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("message", message);
		model.addAttribute("modelURL", "todos");
		model.addAttribute("keyword", keyword);
		model.addAttribute("fromDate", fromDate);	
		model.addAttribute("toDate", toDate);	
		model.addAttribute("listTodos",listTodos);
		model.addAttribute("reverseSortDir", reverseSortDir);
		return "todos/todo_pending";
		
	}
	
	
	@GetMapping("/todos/page/completed")
	
	public String todoComplated(HttpServletRequest request,Model model) throws Exception {
		
		
		return listByPageForDateCompleted(model, 1, "asc", "name",request);
	}
	
	@GetMapping("/todos/page/completed/{pageNum}")
	public String listByPageForDateCompleted(Model model, @PathVariable(name = "pageNum")int pageNum, @Param("sortDir") String sortDir,
			@Param("sortField")String sortField,HttpServletRequest request) throws Exception {
		if(sortDir ==null|| sortDir.isEmpty()) {
			sortDir = "asc";
		}
		
		String keyword = request.getParameter("keyword");
		String fromDate= request.getParameter("fromDate");
		String toDate = request.getParameter("toDate");
		
		Customer customer = getCustomerByEmail(request);
		
		TodoPageInfo pageInfo = new TodoPageInfo();
		
		List<Todo> listTodos = service.listByPageForDateComleted(pageInfo, keyword, fromDate, toDate, customer, sortField, sortDir, pageNum);
		
		
		String message = "Có " + getElementSearch(listTodos) + " kết quả được tìm thấy";
		
		long startCount = (pageNum-1)*service.TODO_PER_PAGE +1;
		long endCount = startCount + service.TODO_PER_PAGE -1;
		if(endCount>pageInfo.getTotalElements()) {
			endCount = pageInfo.getTotalElements();
		}
		String reverseSortDir = sortDir.equals("asc")?"desc":"asc";
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageInfo.getTotalElements());
		model.addAttribute("totalPages", pageInfo.getTotalPage());
		
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("message", message);
		model.addAttribute("keyword", keyword);
		model.addAttribute("fromDate", fromDate);	
		model.addAttribute("toDate", toDate);	
		model.addAttribute("modelURL", "todos");
		model.addAttribute("listTodos",listTodos);
		model.addAttribute("reverseSortDir", reverseSortDir);
		return "todos/todo_completed";
		
	}
	
	
	@GetMapping("/todos")
	public String listFirstPage(Model model,HttpServletRequest request) {
		return listByPage(model,1,"asc","name",null,request);
	}
	@GetMapping("/todos/page/{pageNum}")
	public String listByPage(Model model, @PathVariable(name = "pageNum")int pageNum, @Param("sortDir") String sortDir,
			@Param("sortField")String sortField,@Param("keyword") String keyword,
			HttpServletRequest request) {
		
		if(sortDir ==null|| sortDir.isEmpty()) {
			sortDir = "asc";
		}
		Customer customer = getCustomerByEmail(request);
		
		TodoPageInfo pageInfo = new TodoPageInfo();
		
		List<Todo> listTodos = service.listByPage(pageInfo, pageNum, sortField, sortDir, keyword, customer.getId());
		
		
		long startCount = (pageNum-1)*service.TODO_PER_PAGE +1;
		long endCount = startCount + service.TODO_PER_PAGE -1;
		if(endCount>pageInfo.getTotalElements()) {
			endCount = pageInfo.getTotalElements();
		}
		String reverseSortDir = sortDir.equals("asc")?"desc":"asc";
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageInfo.getTotalElements());
		model.addAttribute("totalPages", pageInfo.getTotalPage());
		
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		
		model.addAttribute("keyword", keyword);
		model.addAttribute("modelURL", "todos");
				
		model.addAttribute("listTodos",listTodos);
		model.addAttribute("reverseSortDir", reverseSortDir);

		return "todos/todo";
	}

	@GetMapping("/todos/new")
		
	public String newTodos(Model model,HttpServletRequest request) {
		Customer customer = getCustomerByEmail(request);
		List<Todo> listRoot = service.listTodoRoot(customer.getId());
		model.addAttribute("todo", new Todo());
		model.addAttribute("pageTitle", "Create New Todo");
		model.addAttribute("listRoot", listRoot);
		return "todos/todo_form";
	}
	@PostMapping("/todos/save")
	public String save(Todo todo,RedirectAttributes ra,HttpServletRequest request) throws Exception {
		
		if(todo.getParent()==null) {
			todo.setStatus("");
			
		}
		
		Customer customer = getCustomerByEmail(request);
		todo.setCustomer(customer);
		service.Save(todo);
		ra.addFlashAttribute("message", "Công việc đã được lưu !");
		return "redirect:/todos";
		
	}
	
	@GetMapping("/todos/edit/{id}")
	
	public String editTodo(@PathVariable(name="id") Integer id, Model model, RedirectAttributes ra,HttpServletRequest request) throws TodoNotFoundException  {
		
		try {
			Todo todo = service.get(id);
			Customer customer = getCustomerByEmail(request);
			List<Todo> listRoot = service.listTodoRoot(customer.getId());
			model.addAttribute("todo", todo);
			model.addAttribute("listRoot", listRoot);
			return "todos/todo_form";
		} catch (Exception e) {
			ra.addFlashAttribute("message", e.getMessage());
			return "redirect:/todos";
		}
	}
	
	@GetMapping("/todos/delete/{id}")
	
	public String deleteTodo(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes ra) throws HasChildrenException, TodoNotFoundException {
		
		try {
			service.delete(id);
			ra.addFlashAttribute("message", "The Todo with id " + id + " has been deleted");
		} catch (TodoNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		} catch(HasChildrenException e) {
			ra.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/todos";
	}
	private Customer getCustomerByEmail(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		return customerService.getCustomerByEmail(email);
	}
	
	private Date formatDate(String inputDate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
                Locale.ENGLISH);
		Date parsedDate = sdf.parse(inputDate);
		
		 SimpleDateFormat print = new SimpleDateFormat("yyyy-MM-dd");
		 Date result = print.parse(print.format(parsedDate));
		 
		 return result;
	}
	private int getElementSearch(List<Todo> todos) {
		int i = 0;
		for(Todo todo : todos) {
			if(todo.getParent()!=null) i= i+1;
		}
		return i;
	}
	
}
