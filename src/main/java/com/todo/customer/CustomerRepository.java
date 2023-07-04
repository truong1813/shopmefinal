package com.todo.customer;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.todo.common.entity.AuthenticationType;
import com.todo.common.entity.Customer;
import com.todo.common.entity.Todo;
@Repository
public interface CustomerRepository extends CrudRepository<Customer, Integer> {
	@Query("SELECT c FROM Customer c WHERE c.email = ?1")
	public Customer findByEmail(String email);
	
	@Query("SELECT c FROM Customer c WHERE c.verificationCode = ?1")
	public Customer findByVerificationCode(String code);
	
	@Query("UPDATE Customer c SET c.enabled = true, c.verificationCode = null WHERE c.id = ?1")
	@Modifying
	public void enable( Integer id); 
	
	@Query("UPDATE Customer c SET c.authenticationType = ?2 WHERE c.id = ?1")
	@Modifying
	public void updateAuthenticationType(Integer customerId, AuthenticationType type);
	
	@Query("SELECT c FROM Customer c WHERE c.resetPasswordToken = ?1")
	public Customer findByToken(String token);
	
	


}
