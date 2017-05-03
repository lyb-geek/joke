package com.gradle.joke.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gradle.joke.base.AjaxStatusInfo;
import com.gradle.joke.base.GlobalParams;
import com.gradle.joke.base.PageHelper;
import com.gradle.joke.base.SearchForm;
import com.gradle.joke.entity.Article;
import com.gradle.joke.service.ArticleService;
import com.gradle.joke.util.PageUtil;

/**
 * 
 * <p>
 * Title:ArticleController
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月15日
 */
@Controller
@RequestMapping(value = "/article/")
public class ArticleController {
	private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

	@Autowired
	private ArticleService articleSvc;

	@RequestMapping(value = "add")
	public String add(Model model) {

		model.addAttribute("persist", "persist");

		return "admin/article/add";
	}

	@RequestMapping(value = "update/{id}")
	public String update(Model model, @PathVariable Long id) {

		Article article = articleSvc.getArtcleById(id);

		model.addAttribute("article", article);

		model.addAttribute("persist", "persist");

		return "admin/article/add";

	}

	@RequestMapping(value = "view/{id}")
	public String view(Model model, @PathVariable Long id) {

		Article article = articleSvc.getArtcleById(id);

		model.addAttribute("article", article);

		return "admin/article/add";

	}

	@RequestMapping(value = "list")
	public String list(Model model) {
		Pageable pageable = new PageRequest(GlobalParams.PAGE_NO, GlobalParams.PAGE_SIZE, Direction.DESC, "updateTime");
		Page<Article> page = articleSvc.getPageList(pageable);
		Long totalCount = articleSvc.getTotalCount();

		int totalPage = PageUtil.getCountpage(Integer.valueOf(totalCount.toString()), GlobalParams.PAGE_SIZE);

		model.addAttribute(PageUtil.PAGE, page.getContent());
		model.addAttribute(PageUtil.TOTAL_PAGE, totalPage);

		return "admin/article/list";
	}

	@RequestMapping(value = "save")
	@ResponseBody
	public AjaxStatusInfo<String> save(Article article) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		try {
			articleSvc.persist(article);
		} catch (Exception e) {
			logger.error("save fail :" + e.getMessage(), e);
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;
	}

	@RequestMapping(value = "gotoPage")
	@ResponseBody
	public AjaxStatusInfo<List<Article>> gotoPage(Integer pageNo, Integer pageSize, SearchForm searchForm) {
		PageHelper<Article> page = articleSvc.getPages(pageNo, pageSize, searchForm);

		List<Article> result = page.getResult();
		AjaxStatusInfo<List<Article>> ajaxStatusInfo = new AjaxStatusInfo<List<Article>>(result);

		return ajaxStatusInfo;

	}

	@RequestMapping(value = "search")
	@ResponseBody
	public AjaxStatusInfo<PageHelper<Article>> search(SearchForm searchForm) {
		PageHelper<Article> page = articleSvc.getPages(GlobalParams.PAGE_NO, GlobalParams.PAGE_SIZE, searchForm);

		AjaxStatusInfo<PageHelper<Article>> ajaxStatusInfo = new AjaxStatusInfo<PageHelper<Article>>(page);

		return ajaxStatusInfo;

	}

	@RequestMapping(value = "delete")
	@ResponseBody
	public AjaxStatusInfo<String> delete(Long id) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		try {
			articleSvc.deleteArtcleById(id);
		} catch (Exception e) {
			logger.error("delete fail :" + e.getMessage(), e);
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;
	}

	@RequestMapping(value = "batchDel")
	@ResponseBody
	public AjaxStatusInfo<String> batchDel(@RequestParam(value = "ids[]") Long[] ids) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		try {
			articleSvc.batchDelByIds(ids);
		} catch (Exception e) {
			logger.error("batchDel fail :" + e.getMessage(), e);
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;
	}

}
