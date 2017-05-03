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
import com.gradle.joke.entity.PermissionConfig;
import com.gradle.joke.service.PermissionConfigService;
import com.gradle.joke.util.PageUtil;

/**
 * 
 * <p>
 * Title:PermissionConfigController
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
@RequestMapping(value = "/permissionConfig/")
public class PermissionConfigController {
	private static final Logger logger = LoggerFactory.getLogger(PermissionConfigController.class);

	@Autowired
	private PermissionConfigService permissionConfigSvc;

	@RequestMapping(value = "add")
	public String add(Model model) {

		model.addAttribute("persist", "persist");

		return "admin/permissionConfig/add";
	}

	@RequestMapping(value = "update/{id}")
	public String update(Model model, @PathVariable Long id) {

		PermissionConfig permissionConfig = permissionConfigSvc.getPermissionConfigById(id);

		model.addAttribute("permissionConfig", permissionConfig);

		model.addAttribute("persist", "persist");

		return "admin/permissionConfig/add";

	}

	@RequestMapping(value = "view/{id}")
	public String view(Model model, @PathVariable Long id) {

		PermissionConfig permissionConfig = permissionConfigSvc.getPermissionConfigById(id);

		model.addAttribute("permissionConfig", permissionConfig);

		return "admin/permissionConfig/add";

	}

	@RequestMapping(value = "list")
	public String list(Model model) {
		Pageable pageable = new PageRequest(GlobalParams.PAGE_NO, GlobalParams.PAGE_SIZE, Direction.DESC, "createTime");
		Page<PermissionConfig> page = permissionConfigSvc.getPageList(pageable);
		Long totalCount = permissionConfigSvc.getTotalCount();

		int totalPage = PageUtil.getCountpage(Integer.valueOf(totalCount.toString()), GlobalParams.PAGE_SIZE);

		model.addAttribute(PageUtil.PAGE, page.getContent());
		model.addAttribute(PageUtil.TOTAL_PAGE, totalPage);

		return "admin/permissionConfig/list";
	}

	@RequestMapping(value = "save")
	@ResponseBody
	public AjaxStatusInfo<String> save(PermissionConfig permissionConfig) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		try {
			permissionConfigSvc.persist(permissionConfig);
		} catch (Exception e) {
			logger.error("save fail :" + e.getMessage(), e);
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;
	}

	@RequestMapping(value = "gotoPage")
	@ResponseBody
	public AjaxStatusInfo<List<PermissionConfig>> gotoPage(Integer pageNo, Integer pageSize, SearchForm searchForm) {
		PageHelper<PermissionConfig> page = permissionConfigSvc.getPages(pageNo, pageSize, searchForm);

		List<PermissionConfig> result = page.getResult();
		AjaxStatusInfo<List<PermissionConfig>> ajaxStatusInfo = new AjaxStatusInfo<List<PermissionConfig>>(result);

		return ajaxStatusInfo;

	}

	@RequestMapping(value = "search")
	@ResponseBody
	public AjaxStatusInfo<PageHelper<PermissionConfig>> search(SearchForm searchForm) {
		PageHelper<PermissionConfig> page = permissionConfigSvc.getPages(GlobalParams.PAGE_NO, GlobalParams.PAGE_SIZE,
				searchForm);

		AjaxStatusInfo<PageHelper<PermissionConfig>> ajaxStatusInfo = new AjaxStatusInfo<PageHelper<PermissionConfig>>(
				page);

		return ajaxStatusInfo;

	}

	@RequestMapping(value = "delete")
	@ResponseBody
	public AjaxStatusInfo<String> delete(Long id) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		try {
			permissionConfigSvc.deletePermissionConfigById(id);
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
			permissionConfigSvc.batchDelByIds(ids);
		} catch (Exception e) {
			logger.error("batchDel fail :" + e.getMessage(), e);
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;
	}

}
