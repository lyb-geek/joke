package com.test.joke;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gradle.joke.entity.Image;
import com.gradle.joke.run.ApplicationRun;
import com.gradle.joke.service.ImageService;

@RunWith(SpringJUnit4ClassRunner.class)
// 指定spring-boot的启动类
@SpringBootTest(classes = ApplicationRun.class)
public class ArticleDaoTest {

	@Autowired
	private ImageService imageService;

	@Test
	public void testAdd() {
		for (int i = 0; i < 66; i++) {
			Image image = new Image();
			image.setImgDesc("第" + (i + 1) + "幅图片的经典描述，哈哈哈哈哈哈哈哈哈你妹啊");
			image.setImgPath("1485225098292_小清新.jpg");
			if (i % 2 == 0) {
				image.setIsShow(0);
			} else {
				image.setIsShow(1);
			}

			image.setTitle("第" + (i + 1) + "幅测试图片");
			try {
				imageService.persist(image);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
