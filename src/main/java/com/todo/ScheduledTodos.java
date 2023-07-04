package com.todo;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.todo.customer.CustomerController;

@Configuration
@EnableScheduling
public class ScheduledTodos {
	private final CustomerController customerController;
	@Autowired
    public ScheduledTodos(CustomerController customerController) {
        this.customerController = customerController;
    }
	@Scheduled(cron = "0 0 10 * * ?") // Chạy vào 00:00:00 hàng ngày
    public void sendNotificationsToCustomers() throws UnsupportedEncodingException, MessagingException {
        customerController.sendNotificationsToCustomers();
    }

}
