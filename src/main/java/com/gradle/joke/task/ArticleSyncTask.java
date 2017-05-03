package com.gradle.joke.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.gradle.joke.base.GlobalParams;
import com.gradle.joke.entity.Article;

/**
 * 
 * <p>
 * Title: ImageSyncTask
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年2月16日
 */
@Component("articleSyncTask")
@Scope("prototype")
public class ArticleSyncTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(ArticleSyncTask.class);

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	private Article article;

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	@Override
	public void run() {
		syncArticlesToRedis(article);

	}

	public void syncArticlesToRedis(Article article) {
		long startTime = System.currentTimeMillis();
		logger.info("当前线程" + Thread.currentThread().getName() + "查找到段子，开始同步到redis中...");
		String json = JSON.toJSONString(article);
		logger.info("article：{}", json);
		redisTemplate.opsForHash().put(GlobalParams.ARTICLE_CACHE, article.getId().toString(), json);
		long endTime = System.currentTimeMillis();

		logger.info("当前线程" + Thread.currentThread().getName() + "同步结束，耗时：{}ms", (endTime - startTime));

	}

}
