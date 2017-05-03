package com.gradle.joke.base;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 
 * <p>
 * Title:GlobalParams
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月12日
 */
public class GlobalParams {

	public static LoadingCache<String, String[]> reminderEmailCache = CacheBuilder.newBuilder()
			.build(new CacheLoader<String, String[]>() {
				@Override
				public String[] load(String key) throws Exception {
					String[] defaultEmail = new String[] { "410480180@qq.com" };
					return defaultEmail;
				}

			});

	public static final String SUCCESS_MSG = "success";

	public static final Integer SUCCESS_CODE = 1;

	public static final String FAIL_MSG = "fail";

	public static final Integer FAIL_CODE = 0;

	public static final Integer DEBUG_YES = 1;

	public static final Integer DEBUG_NO = 0;

	public static final Integer YES = 1;

	public static final Integer NO = 0;

	public static final Integer PAGE_NO = 0;

	public static final Integer PAGE_SIZE = 10;

	public static final String STR_SPLIT = "_";

	public static final String ROLE_PARENT_NAME = "角色菜单";

	public static final String AUDIO_EDIT_NAME = "修改段子";

	public static final String IMAGE_EDIT_NAME = "修改趣图";

	public static final String CURRENT_USER = "currentUser";

	public static final String AUTH = "lybAuths";
	public static final String AUTH_NAME = "name";
	public static final String AUTH_NAME_SPLIT = ",";

	public static final String IMAGE_CACHE = "imageCache";

	public static final String ARTICLE_CACHE = "articleCache";

	public static final String AUDIO_PLAY_PATH_PREFIX = "file/audio?fileName=";

	public static final String IMAGE_SHOW_PATH_PREFIX = "file/download/1?fileName=";

	public static final String AUDIO_REMINDER_EMAIL_CACHE = "audioReminderEmailCache";

	public static final String IMAGE_REMINDER_EMAIL_CACHE = "imageReminderEmailCache";

	public static final String TOPIC_AUDIO = "audioTopic";

	public static final String TOPIC_IMAGE = "imageTopic";

	public static final String WEBSOCKET_MESSAGE_TOPIC_PATH = "/topic/notify";

}
