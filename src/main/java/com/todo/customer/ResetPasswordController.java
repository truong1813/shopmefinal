package com.todo.customer;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.todo.Utility;
import com.todo.common.entity.Customer;
import com.todo.setting.EmailSettingBag;
import com.todo.setting.SettingService;
@Controller
public class ResetPasswordController {
	@Autowired private CustomerService customerService;
	@Autowired private SettingService settingService;
	
	
	@GetMapping("/forgot_password")
	public String showRequestForm() {
		
		return "customer/forgot_password_form";
	}
	
	@PostMapping("/forgot_password")
	public String processRequestForm(HttpServletRequest request, Model model) {
		String email = request.getParameter("email");
		
		try {
		String token=customerService.updateResetPasswordToken(email);
		String link  = Utility.getSiteUrl(request) + "/reset_password?token=" + token;
		sendMail(link, email);
		model.addAttribute("message", "We have send a reset password link to your email.");
		} catch (CustomerNotFoundException e) {
			model.addAttribute("error", e.getMessage());
		} catch(UnsupportedEncodingException | MessagingException e) {
			model.addAttribute("error", "Could not send email");
		}
		
		return "customer/forgot_password_form";
	}
	
	private  void sendMail(String link, String email) throws UnsupportedEncodingException, MessagingException{
		EmailSettingBag emailSettings = settingService.getEmailSetting();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		
		String toAddress = email;
		String subject = "Here's the link to reset your password";
		String content ="<p>Hello,</>"
				+ "<p>You have requested to reset your password.</p>"
				+ "CLick the link below to change your password:</p>"
				+ "<p><a href=\"" + link + "\">Change my password</a></p>"
				+ "<br>"
				+ "<p>Ignore this email if you do remember your password, "
				+ "or you have not made the request.</p>";
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);

		helper.setText(content,true);
		
		mailSender.send(message);
	}
	
	@GetMapping("/reset_password")
	
	public String showResetForm(@Param("token") String token, Model model) {
		Customer customer= customerService.getByResetPasswordToken(token);
		if(customer != null) {
			model.addAttribute("token", token);
			
		}else {
			model.addAttribute("message", "invalid token");
			return "message";
		}
		
		
		return "customer/reset_password_form";
	}
	
	@PostMapping("/reset_password")
	public String resetPassword(HttpServletRequest request,Model model) {
		String password = request.getParameter("password");
		String token = request.getParameter("token");
		try {
			customerService.updateNewPasswordForCustomer(token, password);
			model.addAttribute("title", "Reset Your Password:");
			model.addAttribute("message", "Your password have changed successfully.");
		} catch (CustomerNotFoundException e) {
			model.addAttribute("title", "Invalid token");
			model.addAttribute("message", e.getMessage());
		}
		
		return "message";
	}

}
