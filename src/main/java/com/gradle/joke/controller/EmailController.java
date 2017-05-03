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
import com.gradle.joke.entity.EmailLog;
import com.gradle.joke.service.EmailService;
import com.gradle.joke.util.PageUtil;

/**
 * 
 * <p>
 * Title:EmailController
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月12日
 */
@Controller
@RequestMapping(value = "/email/")
public class EmailController {
	private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

	@Autowired
	private EmailService emailSvc;

	@PostMapping(value = "send")
	@ResponseBody
	public AjaxStatusInfo<String> send(EmailLog emailLog) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		boolean isSendOk = emailSvc.sendEmail(emailLog, 0);

		if (!isSendOk) {
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;

	}

	@RequestMapping(value = "view/{id}")
	public String view(Model model, @PathVariable Long id) {

		EmailLog emailLog = emailSvc.getEmailLogById(id);

		model.addAttribute("emailLog", emailLog);

		return "admin/emailLog/view";

	}

	@RequestMapping(value = "logList")
	public String logList(Model model) {
		Pageable pageable = new PageRequest(GlobalParams.PAGE_NO, GlobalParams.PAGE_SIZE, Direction.DESC, "createTime");
		Page<EmailLog> page = emailSvc.getPageList(pageable);
		Long totalCount = emailSvc.getTotalCount();

		int totalPage = PageUtil.getCountpage(Integer.valueOf(totalCount.toString()), GlobalParams.PAGE_SIZE);

		model.addAttribute(PageUtil.PAGE, page.getContent());
		model.addAttribute(PageUtil.TOTAL_PAGE, totalPage);

		return "admin/emailLog/list";
	}

	@RequestMapping(value = "gotoPage")
	@ResponseBody
	public AjaxStatusInfo<List<EmailLog>> gotoPage(Integer pageNo, Integer pageSize, SearchForm searchForm) {
		PageHelper<EmailLog> page = emailSvc.getPages(pageNo, pageSize, searchForm);

		List<EmailLog> result = page.getResult();
		AjaxStatusInfo<List<EmailLog>> ajaxStatusInfo = new AjaxStatusInfo<List<EmailLog>>(result);

		return ajaxStatusInfo;

	}

	@RequestMapping(value = "search")
	@ResponseBody
	public AjaxStatusInfo<PageHelper<EmailLog>> search(SearchForm searchForm) {
		PageHelper<EmailLog> page = emailSvc.getPages(GlobalParams.PAGE_NO, GlobalParams.PAGE_SIZE, searchForm);

		AjaxStatusInfo<PageHelper<EmailLog>> ajaxStatusInfo = new AjaxStatusInfo<PageHelper<EmailLog>>(page);

		return ajaxStatusInfo;

	}

	@RequestMapping(value = "delete")
	@ResponseBody
	public AjaxStatusInfo<String> delete(Long id) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		try {
			emailSvc.deleteEmailLogById(id);
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
			emailSvc.batchDelByIds(ids);
		} catch (Exception e) {
			logger.error("batchDel fail :" + e.getMessage(), e);
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;
	}

}
