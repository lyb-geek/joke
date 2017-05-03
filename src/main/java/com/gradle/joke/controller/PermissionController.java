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
import com.gradle.joke.entity.Permission;
import com.gradle.joke.service.PermissionService;
import com.gradle.joke.util.PageUtil;

/**
 * 
 * <p>
 * Title: PermissionController
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月29日
 */
@Controller
@RequestMapping(value = "/permission/")
public class PermissionController {
	private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);
	@Autowired
	private PermissionService permissionSvc;

	@RequestMapping(value = "checkPermissionName")
	@ResponseBody
	public boolean checkPermissionName(String permissionName, Long id) {
		boolean isNotExist = true;

		if (id != null) {
			return true;
		}
		Permission permission = permissionSvc.getPermissionByPermissionName(permissionName);

		if (permission != null) {
			isNotExist = false;
		}

		return isNotExist;

	}

	@RequestMapping(value = "add")
	public String add(Model model) {

		model.addAttribute("persist", "persist");

		List<Permission> parentList = permissionSvc.findParentPermissions();

		model.addAttribute("parentList", parentList);

		return "admin/permission/add";
	}

	@RequestMapping(value = "update/{id}")
	public String update(Model model, @PathVariable Long id) {

		Permission permission = permissionSvc.getPermissionById(id);

		model.addAttribute("permission", permission);

		model.addAttribute("persist", "persist");

		List<Permission> parentList = permissionSvc.findParentPermissions();

		model.addAttribute("parentList", parentList);

		model.addAttribute("update", "update");

		return "admin/permission/add";

	}

	@RequestMapping(value = "view/{id}")
	public String view(Model model, @PathVariable Long id) {

		Permission permission = permissionSvc.getPermissionById(id);

		model.addAttribute("permission", permission);

		List<Permission> parentList = permissionSvc.findParentPermissions();

		model.addAttribute("parentList", parentList);

		model.addAttribute("view", "view");

		return "admin/permission/add";

	}

	@RequestMapping(value = "list")
	public String list(Model model) {
		Pageable pageable = new PageRequest(GlobalParams.PAGE_NO, GlobalParams.PAGE_SIZE, Direction.DESC, "createTime");
		Page<Permission> page = permissionSvc.getPageList(pageable);
		Long totalCount = permissionSvc.getTotalCount();

		int totalPage = PageUtil.getCountpage(Integer.valueOf(totalCount.toString()), GlobalParams.PAGE_SIZE);

		model.addAttribute(PageUtil.PAGE, page.getContent());
		model.addAttribute(PageUtil.TOTAL_PAGE, totalPage);

		return "admin/permission/list";
	}

	@RequestMapping(value = "save")
	@ResponseBody
	public AjaxStatusInfo<String> save(Permission permission) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		try {
			permissionSvc.persist(permission);
		} catch (Exception e) {
			logger.error("save fail :" + e.getMessage(), e);
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;
	}

	@RequestMapping(value = "gotoPage")
	@ResponseBody
	public AjaxStatusInfo<List<Permission>> gotoPage(Integer pageNo, Integer pageSize, SearchForm searchForm) {
		PageHelper<Permission> page = permissionSvc.getPages(pageNo, pageSize, searchForm);

		List<Permission> result = page.getResult();
		AjaxStatusInfo<List<Permission>> ajaxStatusInfo = new AjaxStatusInfo<List<Permission>>(result);

		return ajaxStatusInfo;

	}

	@RequestMapping(value = "search")
	@ResponseBody
	public AjaxStatusInfo<PageHelper<Permission>> search(SearchForm searchForm) {
		PageHelper<Permission> page = permissionSvc.getPages(GlobalParams.PAGE_NO, GlobalParams.PAGE_SIZE, searchForm);

		AjaxStatusInfo<PageHelper<Permission>> ajaxStatusInfo = new AjaxStatusInfo<PageHelper<Permission>>(page);

		return ajaxStatusInfo;

	}

	@RequestMapping(value = "delete")
	@ResponseBody
	public AjaxStatusInfo<String> delete(Long id) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		try {
			permissionSvc.deletePermissionById(id);
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
			permissionSvc.batchDelByIds(ids);
		} catch (Exception e) {
			logger.error("batchDel fail :" + e.getMessage(), e);
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;
	}

}
