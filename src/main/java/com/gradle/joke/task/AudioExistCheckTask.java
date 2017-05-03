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
import com.gradle.joke.entity.Audio;
import com.gradle.joke.service.AudioService;

/**
 * 
 * <p>
 * Title: FileExistCheckTask
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
@Component("audioExistCheckTask")
@Scope("prototype")
public class AudioExistCheckTask implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(AudioExistCheckTask.class);

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private AudioService audioSvc;

	// 往websokcet推送消息，前端订阅该消息展示
	@Autowired
	private SimpMessagingTemplate brokerMessagingTemplate;

	@Value("${file.upload.path}")
	private String rootFilePath;

	@Override
	public void run() {
		audioExistCheckTask();

	}

	public void audioExistCheckTask() {
		List<Audio> audios = audioSvc.findAll();
		if (CollectionUtils.isNotEmpty(audios)) {
			List<String> audioNames = Lists.newArrayList();
			long startTime = System.currentTimeMillis();
			logger.info("当前线程" + Thread.currentThread().getName() + "开始检测音频是否存在...");
			long endTime = System.currentTimeMillis();
			for (Audio audio : audios) {
				String path = rootFilePath + File.separatorChar + audio.getAudioPath();
				File file = new File(path);
				if (!file.exists()) {
					audioNames.add(audio.getTitle());
				}

			}

			if (CollectionUtils.isNotEmpty(audioNames)) {
				String message = StringUtils.join(audioNames, ",");
				redisTemplate.convertAndSend(GlobalParams.TOPIC_AUDIO, message);
				// WebSocketMessage webSocketMessage = new WebSocketMessage();
				// webSocketMessage.setContent(message);
				// brokerMessagingTemplate.convertAndSend(GlobalParams.WEBSOCKET_MESSAGE_TOPIC_PATH, webSocketMessage);
			}
			logger.info("当前线程" + Thread.currentThread().getName() + "检测结束，耗时：{}ms", (endTime - startTime));
		}

	}

}
