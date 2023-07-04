package com.todo.customer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.todo.Utility;
import com.todo.common.entity.Customer;
import com.todo.common.entity.Todo;
import com.todo.security.CustomerUserDetails;
import com.todo.security.oauth.CustomerOAuth2User;
import com.todo.setting.EmailSettingBag;
import com.todo.setting.SettingService;
import com.todo.todo.TodoRepository;

@Controller
public class CustomerController {
	@Autowired private CustomerService customerService;
	@Autowired private SettingService settingService;
	@Autowired private TodoRepository todoRepo;
	
	@GetMapping("/register")
	public String viewCustomerRegisterForm( Model model) {
		model.addAttribute("pageTitle", "Customer Registration");
		model.addAttribute("customer", new Customer());
		return "register/register_form";
	}
	
	@PostMapping("/create_customer")
	public String createCustomer(Customer customer, Model model,HttpServletRequest request) 
			throws UnsupportedEncodingException, MessagingException {
		
		customerService.registerCustomer(customer);
		senderVerificationEmail(request,customer);
		
		model.addAttribute("pageTitle", "Registration Success");
		
		return "/register/register_success";
	}

	@GetMapping("/sendNotifications")
    public void sendNotificationsToCustomers() throws UnsupportedEncodingException, MessagingException {
		Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        Date twoDaysLater = calendar.getTime();
        List<Todo> todos = todoRepo.findByCompletedDateBefore(twoDaysLater,"Pending");
        List<Customer> customers = new ArrayList<>();
        
        for (Todo todo : todos) {
           if(!customers.contains(todo.getCustomer())){
        	   customers.add(todo.getCustomer());
           }
            
        }
        for(Customer customer :customers ){
        	senderNotificationEmail(customer);
        	 System.out.println("Sending notification to customer " + customer.getName());
        }
    }

	
	
	
	private void senderNotificationEmail(Customer customer) throws UnsupportedEncodingException, MessagingException {
		
		EmailSettingBag emailSettings = settingService.getEmailSetting();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		mailSender.setDefaultEncoding("utf-8");
		String toAddress = customer.getEmail();
		String subject = emailSettings.getCustomerNotificationSubject();
		String content = emailSettings.getCustomerNotificationContent();
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		content = content.replace("[[name]]",customer.getName());
		
		
		String verifyURL ="http://localhost/Todo/todos/page/pending";
		content = content.replace("[[URL]]", verifyURL);
		helper.setText(content,true);
		mailSender.send(message);
		
	}

	private void senderVerificationEmail(HttpServletRequest request, Customer customer) 
			throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSetting();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		mailSender.setDefaultEncoding("utf-8");
		String toAddress = customer.getEmail();
		String subject = emailSettings.getCustomerVerifySubject();
		String content = emailSettings.getCustomerVerifyContent();
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		content = content.replace("[[name]]",customer.getName());
		String verifyURL = Utility.getSiteUrl(request) + "/create/verify?code=" + customer.getVerificationCode();
		
		content = content.replace("[[URL]]", verifyURL);
		
		helper.setText(content,true);
		
		mailSender.send(message);
		
	}
	
	@GetMapping("/create/verify")
	public String veiry(@Param("code") String code, Model model) {
		boolean verified = customerService.verify(code);
		return "/register/" + (verified?"verify_success":"verify_fail");
	}
	
	@GetMapping("/account_details")
		public String viewAccountDetails(Model model, HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		
		
		
		Customer customer= customerService.getCustomerByEmail(email);
		model.addAttribute("customer", customer);
	
		return "customer/account_form";
	}
	
	
	@PostMapping("/update_account_details")
	public String updateAccountDetails(Customer customer, RedirectAttributes ra,HttpServletRequest request) {
			
		customerService.updateCustomer(customer);
		ra.addFlashAttribute("message","Your account details has been updated.");
		
		updateNameForAuthentiacatedCustomer(customer, request);
		
		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/account_details";
		
		if("address_book".equals(redirectOption)) {
			redirectURL = "redirect:/address_book";
		}else if("cart".equals(redirectOption)) {
			redirectURL = "redirect:/cart";
		}else if("checkout".equals(redirectOption)) {
			redirectURL ="redirect:/address_book?redirect=checkout";
		}
		return redirectURL;
	}

	private void updateNameForAuthentiacatedCustomer(Customer customer, HttpServletRequest request) {
		
		Object principal  = request.getUserPrincipal();
		if(principal instanceof UsernamePasswordAuthenticationToken ||
				principal instanceof RememberMeAuthenticationToken) {
			
			CustomerUserDetails userDetails = getCustomerUserDetailsObject(principal);
			
			Customer authentictedCustomer = userDetails.getCustomer();
			authentictedCustomer.setName(customer.getName());
		}else if(principal instanceof OAuth2AuthenticationToken) {
			
			OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
			CustomerOAuth2User oauth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
			oauth2User.setFullName(customer.getName());
		}
		
	}

	private CustomerUserDetails getCustomerUserDetailsObject(Object principal) {
		CustomerUserDetails userDetails=null;
		if(principal instanceof UsernamePasswordAuthenticationToken){
			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
			userDetails = (CustomerUserDetails) token.getPrincipal();
		}else if(principal instanceof RememberMeAuthenticationToken) {
			RememberMeAuthenticationToken token = (RememberMeAuthenticationToken) principal;
			userDetails = (CustomerUserDetails) token.getPrincipal();
		}
		return userDetails;
	}

}
