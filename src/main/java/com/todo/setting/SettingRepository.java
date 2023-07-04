package com.todo.setting;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.todo.common.entity.setting.Setting;
import com.todo.common.entity.setting.SettingCategory;

public interface SettingRepository extends CrudRepository<Setting, Integer> {
	public List<Setting> findByCategory(SettingCategory category);
	
	@Query("SELECT s FROM Setting s WHERE s.category = ?1 OR s.category = ?2")
	public List<Setting> findTwoCategory(SettingCategory cateOne, SettingCategory cateTwo);

}
