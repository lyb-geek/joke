package com.gradle.joke.controller;

import java.util.ArrayList;
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
import com.gradle.joke.base.TreeNode;
import com.gradle.joke.entity.Role;
import com.gradle.joke.service.PermissionService;
import com.gradle.joke.service.RoleService;
import com.gradle.joke.util.PageUtil;

/**
 * 
 * <p>
 * Title: RoleController
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author linyb
 * @date 2017年1月30日
 */
@Controller
@RequestMapping(value = "/role/")
public class RoleController {

	private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

	@Autowired
	private RoleService roleSvc;

	@Autowired
	private PermissionService permissionSvc;

	@RequestMapping(value = "add")
	public String add(Model model) {

		model.addAttribute("persist", "persist");

		return "admin/role/add";
	}

	@RequestMapping(value = "checkRoleCode")
	@ResponseBody
	public boolean checkRoleCode(String roleCode, Long id) {
		boolean isNotExist = true;

		if (id != null) {
			return true;
		}
		Role role = roleSvc.findByRoleCode(roleCode);

		if (role != null) {
			isNotExist = false;
		}

		return isNotExist;

	}

	@RequestMapping(value = "checkRoleName")
	@ResponseBody
	public boolean checkRoleName(String roleName, Long id) {
		boolean isNotExist = true;

		if (id != null) {
			return true;
		}
		Role role = roleSvc.findByRoleName(roleName);

		if (role != null) {
			isNotExist = false;
		}

		return isNotExist;

	}

	@RequestMapping(value = "update/{id}")
	public String update(Model model, @PathVariable Long id) {

		Role role = roleSvc.getRoleById(id);

		model.addAttribute("role", role);

		model.addAttribute("persist", "persist");

		return "admin/role/add";

	}

	@RequestMapping(value = "view/{id}")
	public String view(Model model, @PathVariable Long id) {

		Role role = roleSvc.getRoleById(id);

		model.addAttribute("role", role);

		return "admin/role/add";

	}

	@RequestMapping(value = "list")
	public String list(Model model) {
		Pageable pageable = new PageRequest(GlobalParams.PAGE_NO, GlobalParams.PAGE_SIZE, Direction.DESC, "createTime");
		Page<Role> page = roleSvc.getPageList(pageable);
		Long totalCount = roleSvc.getTotalCount();

		int totalPage = PageUtil.getCountpage(Integer.valueOf(totalCount.toString()), GlobalParams.PAGE_SIZE);

		model.addAttribute(PageUtil.PAGE, page.getContent());
		model.addAttribute(PageUtil.TOTAL_PAGE, totalPage);

		return "admin/role/list";
	}

	@RequestMapping(value = "save")
	@ResponseBody
	public AjaxStatusInfo<String> save(Role role, @RequestParam(value = "permissionIds[]") Long[] permissionIds) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		try {
			roleSvc.persist(role, permissionIds);
		} catch (Exception e) {
			logger.error("save fail :" + e.getMessage(), e);
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;
	}

	@RequestMapping(value = "gotoPage")
	@ResponseBody
	public AjaxStatusInfo<List<Role>> gotoPage(Integer pageNo, Integer pageSize, SearchForm searchForm) {
		PageHelper<Role> page = roleSvc.getPages(pageNo, pageSize, searchForm);

		List<Role> result = page.getResult();
		AjaxStatusInfo<List<Role>> ajaxStatusInfo = new AjaxStatusInfo<List<Role>>(result);

		return ajaxStatusInfo;

	}

	@RequestMapping(value = "search")
	@ResponseBody
	public AjaxStatusInfo<PageHelper<Role>> search(SearchForm searchForm) {
		PageHelper<Role> page = roleSvc.getPages(GlobalParams.PAGE_NO, GlobalParams.PAGE_SIZE, searchForm);

		AjaxStatusInfo<PageHelper<Role>> ajaxStatusInfo = new AjaxStatusInfo<PageHelper<Role>>(page);

		return ajaxStatusInfo;

	}

	@RequestMapping(value = "delete")
	@ResponseBody
	public AjaxStatusInfo<String> delete(Long id) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		try {
			roleSvc.deleteRoleById(id);
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
			roleSvc.batchDelByIds(ids);
		} catch (Exception e) {
			logger.error("batchDel fail :" + e.getMessage(), e);
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;
	}

	@RequestMapping(value = "initPermissionTreeNodes")
	@ResponseBody
	public AjaxStatusInfo<List<TreeNode>> initPermissionTreeNodes(Long roleId) {
		List<TreeNode> treeNodes = new ArrayList<>();
		if (roleId == null) {
			treeNodes = permissionSvc.getPermissionTreeNodes();
		} else {
			treeNodes = permissionSvc.getPermissionTreeNodesByRoleId(roleId);
		}

		AjaxStatusInfo<List<TreeNode>> ajaxStatusInfo = new AjaxStatusInfo<List<TreeNode>>(treeNodes);

		if (roleId != null && treeNodes != null && !treeNodes.isEmpty()) {
			ajaxStatusInfo.setMsg(GlobalParams.YES.toString());
		}

		return ajaxStatusInfo;

	}

}
