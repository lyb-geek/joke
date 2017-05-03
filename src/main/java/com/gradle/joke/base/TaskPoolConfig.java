package com.gradle.joke.base;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gradle.joke.task.JokeThreadFactory;

@Configuration
public class TaskPoolConfig {

	@Value("${taskPool.corePoolSize}")
	private Integer corePoolSize;

	@Value("${taskPool.maximumPoolSize}")
	private Integer maximumPoolSize;

	@Value("${taskPool.keepAliveTime}")
	private Long keepAliveTime;

	@Value("${taskPoolworkQueueCapacity}")
	private Long taskPoolworkQueueCapacity;

	@Bean
	public ThreadPoolExecutor getThreadPoolExecutor() {
		JokeThreadFactory factory = new JokeThreadFactory("JokeThreadFactory");
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), factory, new ThreadPoolExecutor.AbortPolicy());
		return threadPoolExecutor;
	}

}
