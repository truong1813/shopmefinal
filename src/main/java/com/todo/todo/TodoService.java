package com.todo.todo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Service;

import com.todo.common.entity.Customer;
import com.todo.common.entity.Todo;
import com.todo.customer.CustomerRepository;

@Service
@Transactional

public class TodoService {
	
	public static final Integer TODO_PER_PAGE = 2;
	@Autowired TodoRepository todoRepo;
	@Autowired CustomerRepository customerRepo;
	public List<Todo> listAll(){
		return (List<Todo>) todoRepo.findAll();
	}
	public Todo Save(Todo todo) {
		Todo parent = todo.getParent();
		if(parent!=null ){
			String allParentIds = parent.getAllParentIDs()== null? "-":parent.getAllParentIDs();
			allParentIds +=String.valueOf(parent.getId()) + "-";
			todo.setAllParentIDs(allParentIds);
			
		}

		return todoRepo.save(todo);
	}
	
	private Set<Todo> sortSubTodos(Set<Todo> children){
		return sortSubTodos(children,"asc");
	}


	private Set<Todo> sortSubTodos(Set<Todo> children, String sortDir) {
		Set<Todo> sortedChildren = new TreeSet<>(new Comparator<Todo>() {

			@Override
			public int compare(Todo todo1, Todo todo2) {
				if(sortDir.equals("asc")) {
					return todo1.getName().compareToIgnoreCase(todo2.getName());
				}else {
					return todo2.getName().compareTo(todo1.getName());
				}
				
			}
		});
		sortedChildren.addAll(children);
		return sortedChildren;
	}
	
	public List<Todo> listByPage(TodoPageInfo pageInfo, int pageNum,
			String sortField,String sortDir, String keyword,Integer customerId){
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc")?sort.ascending():sort.descending();
		Pageable pageable = PageRequest.of(pageNum-1, TODO_PER_PAGE,sort);
		Page<Todo> rootPage = null;
		if(keyword !=null && !keyword.isEmpty() ) {
			
			rootPage =todoRepo.findRootTodosByKeyWord(customerId,keyword, pageable);
		}else {
			rootPage=todoRepo.findRootTodos(pageable, customerId);
		}
		List<Todo> rootTodos = rootPage.getContent();
		pageInfo.setTotalElements(rootPage.getTotalElements());
		pageInfo.setTotalPage(rootPage.getTotalPages());	
		
		
		if(keyword!=null && !keyword.isEmpty()) {
			List<Todo> listSearch = rootPage.getContent();
			List<Todo> listInForm  = new ArrayList<>();
			for(Todo todo:listSearch) {
				if(todo.getParent()==null) {
					String nameParent = "Nhóm " + todo.getName();
					listInForm.add(todo.CopyAll(todo,nameParent));
					Set<Todo> childrens = sortSubTodos(todo.getChildren());
					for(Todo children :childrens) {
						String name="-" + children.getName();
						listInForm.add(children.CopyAll(children, name));
					}
				}else {
					Todo parent = todo.getParent();
					String nameParent = "Nhóm " + todo.getName();
					
					listInForm.add(todo.CopyAll(parent,nameParent));
					String name="-" + todo.getName();
					listInForm.add(todo.CopyAll(todo, name));
					
					
				}
				
			}
			return listInForm;
		}else {
			
			return listHierarchicalTodos(rootTodos,sortDir);
		}
	
		
	}


	private List<Todo> listHierarchicalTodos(List<Todo> rootTodos, String sortDir) {
		List<Todo> hierarchial = new ArrayList<>();
		
		for(Todo parent : rootTodos) {
			String nameParent = "Nhóm " + parent.getName();
		
			hierarchial.add(Todo.CopyAll(parent,nameParent));
			Set<Todo> childrens = sortSubTodos(parent.getChildren());
			for(Todo children:childrens) {
				String name="-" + children.getName();
				hierarchial.add(Todo.CopyAll(children,name));
				
			}
			
		}
		return hierarchial;
	}
	
	public Todo get(Integer id) throws TodoNotFoundException {
		try {
			return todoRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new TodoNotFoundException("Không tìm thấy công việc có id:" +id);
		}
	}
	
	public void delete(Integer id) throws TodoNotFoundException, HasChildrenException {
		Long count = todoRepo.countById(id);
		
		
		if(count == 0||count == null) {
			throw new TodoNotFoundException("Không tìm thấy công việc có id:" +id);
		}
		Todo todo = todoRepo.findById(id).get();
		todo.setHasChildren(todo.getChildren().size()>0);
		
		if(todo.isHasChildren())throw new HasChildrenException("Không thể xoá nhóm đang có công việc ");
	
		todoRepo.deleteById(id);
		
		}
	

	public List<Todo> listTodoRoot(Integer customerId) {
		Sort sort = Sort.by("name");
		sort=sort.ascending();
		
		
		return todoRepo.findRootTodos(sort, customerId);
	}

	public String checkGroupUnique(String name,Integer todoId,Integer groupId,Customer customer) {
		Todo todo = null;	
		if(groupId ==0) {
			todo =todoRepo.findGroupByName(customer,name);
		}else {
			todo = todoRepo.findByNameAndCustomerAndParent(name, customer, todoRepo.findById(groupId).get());
		}
		
		if(todo == null)  return "OK";
		
		boolean isCreateNew = (todoId == null || todoId ==0);
		
		if(isCreateNew) {
			if(todo !=null) {
				if(todo.getParent()==null) return  "DuplicatedParent";
				else {
					if(todo.getParent().getChildren().contains(todo)) return "DuplicatedTodo";
				}
			}

		}else {
			if(todo.getParent()==null) {
				if(todo.getId() != todoId) return "DuplicatedParent";
			}else {
				if(todo.getId() != todoId) return "DuplicatedTodo";
			}
		}
		
		return "OK";
		
	}
	
	public List<Todo> listByPageForDate(TodoPageInfo pageInfo,String keyword,String startDate,
			String endDate,Customer customer,String sortField,String sortDir,Integer pageNum) throws ParseException{
		
			Sort sort = Sort.by(sortField);
			sort = sortDir.equals("asc")?sort.ascending():sort.descending();
			
			Pageable pageable = PageRequest.of(pageNum-1, TODO_PER_PAGE,sort);
			Page<Todo> rootPage = todoRepo.findParentsByChildrenStatus("Pending", customer, pageable);
			
			List<Todo> rootTodos = rootPage.getContent();
			List<Todo> listSearch= listForFormUser(keyword,"Pending",rootTodos, sortDir, startDate, endDate);
			pageAndElement( keyword,pageInfo,rootPage,listSearch);
			return listSearch;
	}
	
	
	public List<Todo> listByPageForDateComleted(TodoPageInfo pageInfo,String keyword,String startDate,
			String endDate,Customer customer,String sortField,String sortDir,Integer pageNum) throws ParseException{
			Sort sort = Sort.by(sortField);
			sort = sortDir.equals("asc")?sort.ascending():sort.descending();
			Pageable pageable = PageRequest.of(pageNum-1, TODO_PER_PAGE,sort);
			Page<Todo> rootPage = todoRepo.findParentsByChildrenStatus("Completed", customer, pageable);
			List<Todo> rootTodos = rootPage.getContent();

			List<Todo> listSearch= listForFormUser(keyword,"Completed",rootTodos, sortDir, startDate, endDate);
			pageAndElement( keyword,pageInfo,rootPage,listSearch);
			return listSearch;
	}
	
	private boolean isWithinRange(Date test, String startDate,String endDate) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);

           return !(test.before(start) || test.after(end));
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // Nếu xảy ra lỗi khi phân tích ngày, trả về false
        }
	}
	
private List<Todo> listForFormUser(String keyword,String status,List<Todo>rootTodos,String sortDir,String startDate,String endDate) throws ParseException{
		List<Todo> listForForm = new ArrayList<>();
		if(keyword==null||keyword.isEmpty()) {
			for(Todo todo: rootTodos) {
				int check=0;
				Set<Todo> childrens = sortSubTodos(todo.getChildren());
				List<Todo> listChildrens = new ArrayList<>();
				for(Todo children : childrens) {
					if(children.getStatus().equals(status)) {
						check=1;
						String name = "-" + children.getName();
						listChildrens.add(children.CopyAll(children, name));
					}
				}
				if(check==1) {
					String nameParent = "Nhóm " + todo.getName();
					listForForm.add(todo.CopyAll(todo,nameParent));
					listForForm.addAll(listChildrens);
				}
		
				
			}
			return listForForm;
		}else {
			if(keyword.equals("created")) {
				for(Todo todo: rootTodos) {
					int check=0;
					Set<Todo> childrens = sortSubTodos(todo.getChildren());
					List<Todo> listChildrens = new ArrayList<>();
					for(Todo children : childrens) {
						if(isWithinRange(children.getCreateDate(), startDate, endDate)&&children.getStatus().equals(status)) {
							check=1;
							String name = "-" + children.getName();
							listChildrens.add(children.CopyAll(children, name));
						}
					}
					if(check==1) {
						String nameParent = "Nhóm " + todo.getName();
						listForForm.add(todo.CopyAll(todo,nameParent));
						listForForm.addAll(listChildrens);
					}
			}
			
			}else if(keyword.equals("completed")) {
				for(Todo todo: rootTodos) {
					int check=0;
					Set<Todo> childrens = sortSubTodos(todo.getChildren());
					List<Todo> listChildrens = new ArrayList<>();
					for(Todo children : childrens) {
						if(isWithinRange(children.getCompletedDate(), startDate, endDate)&&children.getStatus().equals(status)) {
							check=1;
							String name = "-" + children.getName();
							listChildrens.add(children.CopyAll(children, name));
						}
					}
					if(check==1) {
						String nameParent = "Nhóm " + todo.getName();
						listForForm.add(todo.CopyAll(todo,nameParent));
						listForForm.addAll(listChildrens);
					}
			}
				
			}
			return listForForm;
		}
	}
private void pageAndElement(String keyword,TodoPageInfo pageInfo,Page<Todo> rootPage,List<Todo> listSearch) {
	if(keyword==null) {
		pageInfo.setTotalElements(rootPage.getTotalElements());
		pageInfo.setTotalPage(rootPage.getTotalPages());
	}else {
		pageInfo.setTotalElements(listSearch.size());
		List<Todo> listParent = new ArrayList<>();
		List<Todo> listChildren = new ArrayList<>();
		for(Todo todo : listSearch) {
			if(todo.getParent()==null) listParent.add(todo);
			else listChildren.add(todo);
		}
		pageInfo.setTotalPage(listParent.size()/TODO_PER_PAGE  + (listParent.size()%TODO_PER_PAGE)==0?0:1);
		pageInfo.setTotalElements(listChildren.size());
	}
}
		
}


/*
private Date formatDate(String inputDate) throws ParseException {
	SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
            Locale.ENGLISH);
	Date parsedDate = sdf.parse(inputDate);
	
	 SimpleDateFormat print = new SimpleDateFormat("yyyy-MM-dd ");
	 
	 
	 return  print.parse(print.format(parsedDate));
}
private Date formatDateTest(String inputDate) throws ParseException {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	Date parsedDate = sdf.parse(inputDate);
	
	 SimpleDateFormat print = new SimpleDateFormat("yyyy-MM-dd");
	 
	 
	 return  print.parse(print.format(parsedDate));
} */

