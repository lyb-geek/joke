package com.gradle.joke.task;

import java.io.File;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.gradle.joke.base.GlobalParams;
import com.gradle.joke.base.WebSocketMessage;
import com.gradle.joke.entity.Image;
import com.gradle.joke.service.ImageService;

/**
 * 
 * <p>
 * Title: ImgExistCheckTask
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
@Component("imgExistCheckTask")
@Scope("prototype")
public class ImgExistCheckTask implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ImgExistCheckTask.class);

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private ImageService imageSvc;

	// 往websokcet推送消息，前端订阅该消息展示
	@Autowired
	private SimpMessagingTemplate brokerMessagingTemplate;

	@Value("${img.upload.path}")
	private String rootImgPath;

	@Override
	public void run() {
		imageExistCheckTask();

	}

	public void imageExistCheckTask() {
		List<Image> images = imageSvc.findAll();
		if (CollectionUtils.isNotEmpty(images)) {
			List<String> imageNames = Lists.newArrayList();
			long startTime = System.currentTimeMillis();
			logger.info("当前线程" + Thread.currentThread().getName() + "开始检查趣图是否存在...");
			long endTime = System.currentTimeMillis();
			for (Image image : images) {
				String path = rootImgPath + File.separatorChar + image.getImgPath();
				File file = new File(path);
				if (!file.exists()) {
					imageNames.add(image.getTitle());
				}

			}

			if (CollectionUtils.isNotEmpty(imageNames)) {
				String message = StringUtils.join(imageNames, ",");
				redisTemplate.convertAndSend(GlobalParams.TOPIC_IMAGE, message);
				WebSocketMessage webSocketMessage = new WebSocketMessage();
				webSocketMessage.setContent(message);
				brokerMessagingTemplate.convertAndSend(GlobalParams.WEBSOCKET_MESSAGE_TOPIC_PATH, webSocketMessage);
			}
			logger.info("当前线程" + Thread.currentThread().getName() + "检测结束，耗时：{}ms", (endTime - startTime));
		}

	}

}
