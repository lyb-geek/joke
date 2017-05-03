package com.gradle.joke.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.gradle.joke.base.GlobalParams;
import com.gradle.joke.entity.Image;

/**
 * 
 * <p>
 * Title: ImageSyncTask
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年2月16日
 */
@Component("imageSyncTask")
@Scope("prototype")
public class ImageSyncTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(ImageSyncTask.class);

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	private Image image;

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	@Override
	public void run() {
		syncImagesToRedis(image);

	}

	public void syncImagesToRedis(Image image) {
		long startTime = System.currentTimeMillis();
		logger.info("当前线程" + Thread.currentThread().getName() + "查找到图片，开始同步到redis中...");
		String json = JSON.toJSONString(image);
		logger.info("image：{}", json);
		redisTemplate.opsForHash().put(GlobalParams.IMAGE_CACHE, image.getId().toString(), json);
		long endTime = System.currentTimeMillis();
		logger.info("当前线程" + Thread.currentThread().getName() + "同步结束，耗时：{}ms", (endTime - startTime));

	}

}
