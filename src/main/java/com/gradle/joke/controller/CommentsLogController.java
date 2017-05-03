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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gradle.joke.base.AjaxStatusInfo;
import com.gradle.joke.base.GlobalParams;
import com.gradle.joke.base.PageHelper;
import com.gradle.joke.base.SearchForm;
import com.gradle.joke.entity.CommentsLog;
import com.gradle.joke.service.CommentsLogService;
import com.gradle.joke.util.PageUtil;

/**
 * 
 * <p>
 * Title:CommentsLogController
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月14日
 */
@Controller
@RequestMapping(value = "/comments/")
public class CommentsLogController {
	private static final Logger logger = LoggerFactory.getLogger(CommentsLogController.class);

	@Autowired
	private CommentsLogService commentsLogSvc;

	@PostMapping(value = "save")
	@ResponseBody
	public AjaxStatusInfo<String> save(CommentsLog commentsLog) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		try {
			commentsLogSvc.save(commentsLog);
		} catch (Exception e) {
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;

	}

	@PostMapping(value = "list", produces = "application/json; charset=utf-8")
	@ResponseBody
	public AjaxStatusInfo<List<CommentsLog>> getCommentLogs() {
		List<CommentsLog> result = commentsLogSvc.getCommentsLogList();
		AjaxStatusInfo<List<CommentsLog>> ajaxStatusInfo = new AjaxStatusInfo<List<CommentsLog>>(result);
		return ajaxStatusInfo;
	}

	@RequestMapping(value = "view/{id}")
	public String view(Model model, @PathVariable Long id) {

		CommentsLog commentsLog = commentsLogSvc.getCommentsLogById(id);

		model.addAttribute("comments", commentsLog);

		return "admin/commentsLog/view";

	}

	@RequestMapping(value = "logList")
	public String logList(Model model) {
		Pageable pageable = new PageRequest(GlobalParams.PAGE_NO, GlobalParams.PAGE_SIZE, Direction.DESC, "createTime");
		Page<CommentsLog> page = commentsLogSvc.getPageList(pageable);
		Long totalCount = commentsLogSvc.getTotalCount();

		int totalPage = PageUtil.getCountpage(Integer.valueOf(totalCount.toString()), GlobalParams.PAGE_SIZE);

		model.addAttribute(PageUtil.PAGE, page.getContent());
		model.addAttribute(PageUtil.TOTAL_PAGE, totalPage);

		return "admin/commentsLog/list";
	}

	@RequestMapping(value = "gotoPage")
	@ResponseBody
	public AjaxStatusInfo<List<CommentsLog>> gotoPage(Integer pageNo, Integer pageSize, SearchForm searchForm) {
		PageHelper<CommentsLog> page = commentsLogSvc.getPages(pageNo, pageSize, searchForm);

		List<CommentsLog> result = page.getResult();
		AjaxStatusInfo<List<CommentsLog>> ajaxStatusInfo = new AjaxStatusInfo<List<CommentsLog>>(result);

		return ajaxStatusInfo;

	}

	@RequestMapping(value = "search")
	@ResponseBody
	public AjaxStatusInfo<PageHelper<CommentsLog>> search(SearchForm searchForm) {
		PageHelper<CommentsLog> page = commentsLogSvc.getPages(GlobalParams.PAGE_NO, GlobalParams.PAGE_SIZE,
				searchForm);

		AjaxStatusInfo<PageHelper<CommentsLog>> ajaxStatusInfo = new AjaxStatusInfo<PageHelper<CommentsLog>>(page);

		return ajaxStatusInfo;

	}

	@RequestMapping(value = "delete")
	@ResponseBody
	public AjaxStatusInfo<String> delete(Long id) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		try {
			commentsLogSvc.deleteCommentsLogById(id);
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
			commentsLogSvc.batchDelByIds(ids);
		} catch (Exception e) {
			logger.error("batchDel fail :" + e.getMessage(), e);
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;
	}

}
