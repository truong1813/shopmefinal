package com.todo;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import com.todo.common.entity.setting.SettingBag;
import com.todo.security.oauth.CustomerOAuth2User;
import com.todo.setting.EmailSettingBag;

public class Utility {
	
	public static String getSiteUrl(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replaceAll(request.getServletPath(), "");
	}
	
	public static JavaMailSenderImpl prepareMailSender(EmailSettingBag settings) {
		
			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			mailSender.setHost(settings.getHost());
			mailSender.setPort(settings.getPort());
			mailSender.setUsername(settings.getUsername());
			mailSender.setPassword(settings.getPassword());
			
			Properties mailProperties = new Properties();
			mailProperties.setProperty("mail.smtp.auth", settings.getAUTH());
			mailProperties.setProperty("mail.smtp.starttls.enable", settings.getSmtpSecured());
			
			mailSender.setJavaMailProperties(mailProperties);
			
			return mailSender;
		}
		
		public static String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
			String customerEmail = null;
			Object principal  = request.getUserPrincipal();
			if(principal ==null) return null;
			
			if(principal instanceof UsernamePasswordAuthenticationToken ||
					principal instanceof RememberMeAuthenticationToken) {
				customerEmail = request.getUserPrincipal().getName();
			}else if(principal instanceof OAuth2AuthenticationToken) {
				
				OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
				CustomerOAuth2User oauth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
				customerEmail = oauth2User.getEmail();
			}
			
			return customerEmail;
			
		}

	}


