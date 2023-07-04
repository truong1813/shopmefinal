package com.todo.setting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.todo.common.entity.setting.Setting;
import com.todo.common.entity.setting.SettingCategory;

@Service
public class SettingService {
	@Autowired SettingRepository repo;
	
	public EmailSettingBag getEmailSetting() {
		List<Setting> settings =repo.findByCategory(SettingCategory.MAIL_SERVER);
		settings.addAll(repo.findByCategory(SettingCategory.MAIL_TEMPLATE));
		return new EmailSettingBag(settings);
	}

}
