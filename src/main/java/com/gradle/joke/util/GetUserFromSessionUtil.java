package com.gradle.joke.util;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.gradle.joke.base.GlobalParams;
import com.gradle.joke.entity.User;

/**
 * 
 * <p>
 * Title: GetUserFromSessionUtil
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年2月12日
 */
public class GetUserFromSessionUtil {

	private GetUserFromSessionUtil() {
	}

	private static class SingletonHolder {
		private static final GetUserFromSessionUtil INSTANCE = new GetUserFromSessionUtil();
	}

	public static final GetUserFromSessionUtil getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public User getCurUserFromSession() {
		User user = null;
		Subject loginUser = SecurityUtils.getSubject();
		if (loginUser != null) {
			Session session = loginUser.getSession();
			user = (User) session.getAttribute(GlobalParams.CURRENT_USER);
		}

		return user;
	}

	public Long getCurUserIdFromSession() {
		User user = this.getCurUserFromSession();
		return user == null ? null : user.getId();
	}
}
