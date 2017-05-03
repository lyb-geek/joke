package com.test.joke;

import java.util.concurrent.ThreadPoolExecutor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gradle.joke.run.ApplicationRun;
import com.gradle.joke.task.ImageSyncTask;

@RunWith(SpringJUnit4ClassRunner.class)
// 指定spring-boot的启动类
@SpringBootTest(classes = ApplicationRun.class)
public class ThreadTest {
	@Autowired
	private ThreadPoolExecutor threadPoolExecutor;
	@Autowired
	private ImageSyncTask imageSyncTask;

	@Test
	public void addRun() {
		threadPoolExecutor.execute(imageSyncTask);
	}

}
