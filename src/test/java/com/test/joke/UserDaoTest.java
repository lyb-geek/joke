package com.test.joke;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gradle.joke.entity.EmailLog;
import com.gradle.joke.entity.User;
import com.gradle.joke.run.ApplicationRun;
import com.gradle.joke.service.EmailService;
import com.gradle.joke.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
// 指定spring-boot的启动类
@SpringBootTest(classes = ApplicationRun.class)
public class UserDaoTest {
	@Autowired
	private UserService userService;
	@Autowired
	private EmailService emailService;

	@Test
	public void addUser() {
		for (int i = 1; i <= 77; i++) {
			User u = new User();
			u.setEmail("12" + i + "@qq.com");
			u.setMobile("12345678" + i);
			u.setPassword("12345" + i);

			if (i == 1) {
				u.setUsername("admin");
			} else if (i % 2 == 0) {
				u.setUsername("user" + i);
				u.setStatus(1);
			} else {
				u.setUsername("guest" + i);
				u.setStatus(0);
			}

			userService.persist(u);

		}
	}

	@Test
	public void testSend() {
		EmailLog log = new EmailLog();
		log.setEmail("linyuanbin0227@qq.com");
		log.setMessage("这是来自晴天的邮件");
		log.setUsername("晴天");
		log.setSubject("你好啊");
		emailService.sendEmail(log, 0);
	}

}
