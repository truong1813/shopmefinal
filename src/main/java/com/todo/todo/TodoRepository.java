package com.todo.todo;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.todo.common.entity.Customer;
import com.todo.common.entity.Todo;
@Repository
public interface TodoRepository extends PagingAndSortingRepository<Todo, Integer> {

	@Query("SELECT t FROM Todo t WHERE t.parent is NUll AND t.customer.id= :id ")
	public List<Todo> findRootTodos(Sort sort, Integer id);
	@Query("SELECT t FROM Todo t WHERE t.parent != Null  AND t.customer= :customer ")
	public List<Todo> findChildrenTodos(Customer customer);
	@Query("SELECT t FROM Todo t WHERE t.parent is NUll AND t.customer.id= :id AND t.name LIKE :keyword ")
	public Page<Todo> findRootTodosByKeyWord(Integer id,String keyword,Pageable pageable);
	
	@Query("SELECT t FROM Todo t WHERE t.parent is NUll AND t.customer.id= :customerId")
	public Page<Todo> findRootTodos(Pageable pageable, Integer customerId);
	
	@Query("SELECT t FROM Todo t WHERE t.parent is null AND t.customer= :customer AND t.name = :name")
	public Todo findGroupByName(Customer customer, String name);
	@Query("SELECT t FROM Todo t WHERE t.customer= :customer AND t.name =:name AND t.parent=:parent")
	public Todo findByNameAndCustomerAndParent(String name,Customer customer,Todo parent);
	
	@Query("SELECT t FROM Todo t WHERE t.customer.id= :customerId AND t.name =:keyword ")
	public Page<Todo> findAllByKeyword(String keyword,Integer customerId,Pageable pageable);
	
	@Query("SELECT t FROM Todo t WHERE t.customer.id= :customerId")
	public Page<Todo> FindByCustomer(Integer customerId,Pageable pageable);
	
	//@Query("SELECT t FROM Todo t WHERE t.customer =:customer AND t.name =:name")
	//public List<Todo> findByNameAndCustomer(Customer customer,String name);
	
	
	@Query("SELECT t FROM Todo t WHERE t.customer  = :customer AND t.status = :status")
	public List<Todo> findByCustomerAndStatus(Customer customer, String status);
	
	 @Query("SELECT t FROM Todo t WHERE t.customer =:customer AND EXISTS (SELECT c FROM Todo c WHERE c.parent = t AND c.status = :status)")
	   Page<Todo> findParentsByChildrenStatus(@Param("status") String status,Customer customer,Pageable pageable );
	 
	 @Query("SELECT t FROM Todo t WHERE t.parent != NULL AND t.status= :status AND t.completedDate > :date")
	 List<Todo> findByCompletedDateBefore(Date date,String status);
	
	public Long countById(Integer id);
	
	
	
}
