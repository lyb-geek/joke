package com.gradle.joke.scheduler;

import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gradle.joke.base.GlobalParams;
import com.gradle.joke.task.AudioExistCheckTask;
import com.gradle.joke.task.ImgExistCheckTask;
import com.gradle.joke.util.SpringContextHolder;

/**
 * 
 * <p>
 * Title: JokeScheduler
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
public class JokeScheduler {

	private static final Logger logger = LoggerFactory.getLogger(JokeScheduler.class);
	@Value("${task.isCheck}")
	private Integer isCheck;
	@Autowired
	private ThreadPoolExecutor threadPoolExecutor;

	@Scheduled(cron = "${scheduled.check.cron}") // 每5分钟执行一次
	public void fileExistCheck() {
		if (GlobalParams.YES == isCheck) {
			logger.info("初始化检测文件是否丢失……");
			long startTime = System.currentTimeMillis();
			runAudioExistCheckTask();
			runImgExistCheckTask();
			long endTime = System.currentTimeMillis();
			logger.info("检测文件丢失结束，耗时：{}ms", (endTime - startTime));
		}

	}

	private void runAudioExistCheckTask() {
		AudioExistCheckTask audioExistCheckTask = SpringContextHolder.getBean("audioExistCheckTask");
		threadPoolExecutor.execute(audioExistCheckTask);
	}

	private void runImgExistCheckTask() {
		ImgExistCheckTask imgExistCheckTask = SpringContextHolder.getBean("imgExistCheckTask");
		threadPoolExecutor.execute(imgExistCheckTask);
	}

}
