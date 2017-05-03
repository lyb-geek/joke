package com.gradle.joke.run;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.gradle.joke.base.GlobalParams;
import com.gradle.joke.entity.Article;
import com.gradle.joke.entity.Image;
import com.gradle.joke.service.ArticleService;
import com.gradle.joke.service.ImageService;
import com.gradle.joke.task.ArticleSyncTask;
import com.gradle.joke.task.ImageSyncTask;
import com.gradle.joke.task.InitReminderMailTask;
import com.gradle.joke.util.MailUtil;
import com.gradle.joke.util.SpringContextHolder;

/**
 * 
 * <p>
 * Title:AppInitOperAfterRun
 * </p>
 * <p>
 * Description:服务启动后，可以用来进行相关数据初始化操作
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月13日
 */
@Component
public class AppInitOperAfterRun implements CommandLineRunner {
	@Autowired
	private MailUtil mailUtil;

	@Autowired
	private ThreadPoolExecutor threadPoolExecutor;

	@Autowired
	private ImageService imageSvc;

	@Autowired
	private ArticleService articleSvc;

	@Value("${task.isRunning}")
	private Integer isRunning;

	@Override
	public void run(String... args) throws Exception {
		mailUtil.initMailSession();
		runInitReminderMailTask();
		if (GlobalParams.YES == isRunning) {
			runArticleTask();
			runImagesTask();
		}

	}

	private void runImagesTask() {
		List<Image> images = imageSvc.getFirstSixImages();
		if (CollectionUtils.isNotEmpty(images)) {
			for (Image image : images) {
				ImageSyncTask imageSyncTask = SpringContextHolder.getBean("imageSyncTask");
				imageSyncTask.setImage(image);
				threadPoolExecutor.execute(imageSyncTask);
			}
		}
	}

	private void runArticleTask() {
		List<Article> articles = articleSvc.getFirstTwoArticle();
		if (CollectionUtils.isNotEmpty(articles)) {
			for (Article article : articles) {
				ArticleSyncTask articleSyncTask = SpringContextHolder.getBean("articleSyncTask");
				articleSyncTask.setArticle(article);
				threadPoolExecutor.execute(articleSyncTask);
			}
		}
	}

	private void runInitReminderMailTask() {
		InitReminderMailTask initReminderMailTask = SpringContextHolder.getBean("initReminderMailTask");
		threadPoolExecutor.execute(initReminderMailTask);
	}

}
