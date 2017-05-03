package com.gradle.joke.pubsub;

import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gradle.joke.base.GlobalParams;
import com.gradle.joke.task.SendAlarmMailTask;
import com.gradle.joke.util.SpringContextHolder;

/**
 * 
 * <p>
 * Title: AudioSubscribe
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
@Component
public class AudioSubscribe {
	private static final Logger logger = LoggerFactory.getLogger(AudioSubscribe.class);
	@Autowired
	private ThreadPoolExecutor threadPoolExecutor;

	public void receiveMessage(String message) {
		logger.info("AudioReceived <" + message + ">");
		SendAlarmMailTask sendAlarmMailTask = SpringContextHolder.getBean("sendAlarmMailTask");
		sendAlarmMailTask.setMessage(message);
		sendAlarmMailTask.setIsImgAlarm(GlobalParams.NO);
		threadPoolExecutor.execute(sendAlarmMailTask);
	}

}
