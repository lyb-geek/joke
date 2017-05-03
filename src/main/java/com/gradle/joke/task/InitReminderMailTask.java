package com.gradle.joke.task;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.gradle.joke.base.GlobalParams;
import com.gradle.joke.entity.Permission;
import com.gradle.joke.entity.Role;
import com.gradle.joke.entity.User;
import com.gradle.joke.service.PermissionService;

/**
 * 
 * <p>
 * Title: InitReminderMailTask
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年2月17日
 */
@Component("initReminderMailTask")
@Scope("prototype")
public class InitReminderMailTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(InitReminderMailTask.class);
	@Autowired
	private PermissionService permissionSvc;

	@Override
	public void run() {
		initReminderEmails();

	}

	private void initReminderEmails() {
		initImageReminderEmails();
		initAudioReminderEmails();
	}

	private void initAudioReminderEmails() {
		logger.info("初始化通知修改音频权限邮箱人员。。。");
		Permission audioPermission = permissionSvc.getPermissionByPermissionName(GlobalParams.AUDIO_EDIT_NAME);
		String[] audioEmailArrs = getEmailArrs(audioPermission);
		GlobalParams.reminderEmailCache.put(GlobalParams.AUDIO_REMINDER_EMAIL_CACHE, audioEmailArrs);
		logger.info("初始化通知修改音频邮箱人员结束。。。");
	}

	private void initImageReminderEmails() {
		logger.info("初始化通知修改趣图权限邮箱人员。。。");
		Permission imagePermission = permissionSvc.getPermissionByPermissionName(GlobalParams.IMAGE_EDIT_NAME);
		String[] imageEmailArrs = getEmailArrs(imagePermission);
		GlobalParams.reminderEmailCache.put(GlobalParams.IMAGE_REMINDER_EMAIL_CACHE, imageEmailArrs);
		logger.info("初始化通知修改趣图权限邮箱人员结束。。。");
	}

	private String[] getEmailArrs(Permission permission) {
		String[] emailArrs = null;
		List<String> reminderEmails = Lists.newArrayList();
		if (permission != null) {
			Set<Role> roles = permission.getRoleList();
			if (CollectionUtils.isNotEmpty(roles)) {
				for (Role role : roles) {
					Set<User> users = role.getUserList();
					if (CollectionUtils.isNotEmpty(users)) {
						for (User user : users) {
							if (GlobalParams.YES == user.getStatus()) {
								reminderEmails.add(user.getEmail());
							}

						}
					}
				}
			}

			if (CollectionUtils.isNotEmpty(reminderEmails)) {
				emailArrs = reminderEmails.toArray(new String[reminderEmails.size()]);
			}

		}

		return emailArrs;

	}

}
