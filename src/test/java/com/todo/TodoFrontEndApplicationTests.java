package com.todo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.todo.common.entity.Customer;
import com.todo.common.entity.Todo;
import com.todo.customer.CustomerRepository;
import com.todo.todo.TodoRepository;

@SpringBootTest
class TodoFrontEndApplicationTests {
@Autowired TodoRepository repo;
@Autowired CustomerRepository cusRepo;
	@Test
	
		public void formatDate() throws ParseException {
			SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
	                Locale.ENGLISH);
			Date parsedDate = sdf.parse("Tue Jun 27 10:05:31 ICT 2023");
			 SimpleDateFormat print = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
			 System.out.println(print.format(parsedDate));
	}
	@Test
	
	public void indByCompletedDateBefore() {
		Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        Date twoDaysLater = calendar.getTime();
        List<Todo> todos =repo.findByCompletedDateBefore(twoDaysLater,"Pending");
        List<Customer> customers = new ArrayList<>();
        

        for (Todo todo : todos) {
           if(!customers.contains(todo.getCustomer())){
        	   customers.add(todo.getCustomer());
           }
            
	}
	for(Customer customer :customers ){
		System.out.println(customer.getEmail());
	}

	}
}
