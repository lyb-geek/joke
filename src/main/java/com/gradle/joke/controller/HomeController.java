package com.gradle.joke.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.gradle.joke.anotation.NeedBrowseCount;
import com.gradle.joke.base.GlobalParams;
import com.gradle.joke.entity.Article;
import com.gradle.joke.entity.Image;
import com.gradle.joke.service.ArticleService;
import com.gradle.joke.service.AudioService;
import com.gradle.joke.service.ImageService;

import io.swagger.annotations.ApiOperation;

/**
 * 
 * <p>
 * Title:HomeController
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月10日
 */
@Controller
@RequestMapping(value = "/home/")
public class HomeController {
	@Autowired
	private ArticleService articleSvc;

	@Autowired
	private ImageService imageSvc;

	@Autowired
	private AudioService audioSvc;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Value("${img.upload.path}")
	private String rootImgPath;

	@Value("${file.upload.path}")
	private String rootFilePath;

	@ApiOperation(value = "joke首页展示", notes = "")
	// @ApiImplicitParam(name = "model", value = "通过model把数据传到view层", required = false, dataType = "Model")
	@NeedBrowseCount
	@RequestMapping(value = "index")
	public String index(Model model) {
		setArticles(model);
		setImgPaths(model);
		model.addAttribute("audioPath", audioSvc.getAudioPath());
		return "front/index";
	}

	private void setDefaultArticles(Model model) {
		List<Article> articles = Lists.newArrayList();
		Article article = new Article();
		article.setTitle("从前");
		article.setContent("从前车马很慢，书信很远，一生只够爱一个，但能纳很多妾");
		articles.add(article);

		Article article1 = new Article();
		article1.setTitle("喜欢");
		article1.setContent("你要是喜欢一个女生，从现在就开始好好学习，将来找好工作挣好多钱，等她结婚的时候多出点份子钱");
		articles.add(article1);
		model.addAttribute("articles", articles);
	}

	private void setArticles(Model model) {
		List<Article> articles = articleSvc.getFirstTwoArticle();
		if (CollectionUtils.isNotEmpty(articles)) {
			model.addAttribute("articles", articles);
		} else {
			setDefaultArticles(model);
		}
	}

	private void setDefaultImgPaths(Model model) {
		List<String> imagesPath = Lists.newArrayList();
		String defaultImgPath = "front/img/default.jpg";
		for (int i = 0; i < 6; i++) {
			switch (i) {
			case 0:
				defaultImgPath = "front/img/default-0.jpg";
				imagesPath.add(defaultImgPath);
				break;
			case 1:
				defaultImgPath = "front/img/default-1.jpg";
				imagesPath.add(defaultImgPath);
				break;
			case 2:
				defaultImgPath = "front/img/default-2.jpg";
				imagesPath.add(defaultImgPath);
				break;

			case 3:
				defaultImgPath = "front/img/default-3.jpg";
				imagesPath.add(defaultImgPath);
				break;
			case 4:
				defaultImgPath = "front/img/default-4.jpg";
				imagesPath.add(defaultImgPath);
				break;
			case 5:
				defaultImgPath = "front/img/default-5.jpg";
				imagesPath.add(defaultImgPath);
				break;

			default:
				imagesPath.add(defaultImgPath);
				break;
			}

		}
		model.addAttribute("imagePaths", imagesPath);
	}

	private void setImgPaths(Model model) {
		List<String> imagePaths = imageSvc.getImgPathsForShow();
		if (CollectionUtils.isNotEmpty(imagePaths)) {

			model.addAttribute("imagePaths", imagePaths);
		} else {
			setDefaultImgPaths(model);
		}
	}

	@Deprecated
	private void setArtcles(Model model) {
		List<Article> articles = Lists.newArrayList();
		Map<Object, Object> artcleMap = redisTemplate.opsForHash().getOperations()
				.boundHashOps(GlobalParams.ARTICLE_CACHE).entries();
		if (artcleMap != null && !artcleMap.isEmpty()) {
			for (Object key : artcleMap.keySet()) {
				Object value = artcleMap.get(key);
				Article article = JSON.parseObject(value.toString(), Article.class);
				articles.add(article);
			}
		} else {
			articles = articleSvc.getFirstTwoArticle();
		}

		model.addAttribute("articles", articles);

	}

	@Deprecated
	private void setImages(Model model) {
		List<Image> images = Lists.newArrayList();
		Map<Object, Object> imageMap = redisTemplate.opsForHash().getOperations().boundHashOps(GlobalParams.IMAGE_CACHE)
				.entries();
		if (imageMap != null && !imageMap.isEmpty()) {
			for (Object key : imageMap.keySet()) {
				Object value = imageMap.get(key);
				Image image = JSON.parseObject(value.toString(), Image.class);
				images.add(image);
			}
		} else {
			images = imageSvc.getFirstSixImages();
		}

		model.addAttribute("images", images);

	}

}
