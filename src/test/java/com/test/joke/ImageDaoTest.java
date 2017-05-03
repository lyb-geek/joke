package com.test.joke;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gradle.joke.entity.Article;
import com.gradle.joke.run.ApplicationRun;
import com.gradle.joke.service.ArticleService;

@RunWith(SpringJUnit4ClassRunner.class)
// 指定spring-boot的启动类
@SpringBootTest(classes = ApplicationRun.class)
public class ImageDaoTest {

	@Autowired
	private ArticleService articleService;

	@Test
	public void testAdd() {
		for (int i = 0; i < 66; i++) {
			Article article = new Article();
			article.setContent("这是第" + (i + 1) + "篇测试文章，哈哈哈哈哈xxxxxxxxxxxxxxxxxxxxxxxxxxxx，你是八戒啊啊啊啊啊，哈哈哈哈");
			article.setTitle("第" + (i + 1) + "篇测试");
			try {
				articleService.persist(article);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
