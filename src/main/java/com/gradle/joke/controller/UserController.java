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
import com.gradle.joke.entity.User;
import com.gradle.joke.service.RoleService;
import com.gradle.joke.service.UserService;
import com.gradle.joke.util.PageUtil;

/**
 * 
 * <p>
 * Title:UserController
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
@RequestMapping(value = "/user/")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

	@Autowired
	private UserService userSvc;

	@Autowired
	private RoleService roleSvc;

	@RequestMapping(value = "add")
	public String add(Model model) {
		model.addAttribute("persist", "persist");
		return "admin/user/add";
	}

	@RequestMapping(value = "checkUsername")
	@ResponseBody
	public boolean checkUsername(String username, Long id) {
		boolean isNotExist = true;

		if (id != null) {
			return true;
		}
		User user = userSvc.getUserByUsername(username);

		if (user != null) {
			isNotExist = false;
		}

		return isNotExist;

	}

	@RequestMapping(value = "checkEmail")
	@ResponseBody
	public boolean checkEmail(String email, Long id, Integer sendEmail) {
		boolean isNotExist = true;

		if (id != null) {
			return true;
		}
		User user = userSvc.findByEmail(email);

		if (GlobalParams.YES == sendEmail) {
			if (user == null) {
				isNotExist = false;
			}
		} else {
			if (user != null) {
				isNotExist = false;
			}
		}

		return isNotExist;

	}

	@RequestMapping(value = "save")
	@ResponseBody
	public AjaxStatusInfo<String> save(User user) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		try {
			userSvc.persist(user);
		} catch (Exception e) {
			logger.error("save fail :" + e.getMessage(), e);
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;
	}

	@RequestMapping(value = "update/{id}")
	public String update(Model model, @PathVariable Long id) {

		User user = userSvc.getUserById(id);

		model.addAttribute("user", user);

		model.addAttribute("persist", "persist");

		return "admin/user/add";

	}

	@RequestMapping(value = "userRoleAdd/{id}")
	public String userRoleAdd(Model model, @PathVariable Long id) {

		model.addAttribute("userId", id);

		return "admin/user/userRoleAdd";

	}

	@RequestMapping(value = "view/{id}")
	public String view(Model model, @PathVariable Long id) {

		User user = userSvc.getUserById(id);

		model.addAttribute("user", user);

		return "admin/user/add";

	}

	@RequestMapping(value = "toChangePwd/{id}")
	public String toChangePwd(Model model, @PathVariable Long id) {

		User user = userSvc.getUserById(id);

		model.addAttribute("user", user);

		return "admin/user/changePwd";

	}

	@RequestMapping(value = "list")
	public String list(Model model) {
		Pageable pageable = new PageRequest(GlobalParams.PAGE_NO, GlobalParams.PAGE_SIZE, Direction.DESC, "createTime");
		Page<User> page = userSvc.getPageList(pageable);
		Long totalCount = userSvc.getTotalCount();

		int totalPage = PageUtil.getCountpage(Integer.valueOf(totalCount.toString()), GlobalParams.PAGE_SIZE);

		model.addAttribute(PageUtil.PAGE, page.getContent());
		model.addAttribute(PageUtil.TOTAL_PAGE, totalPage);

		return "admin/user/list";
	}

	@RequestMapping(value = "gotoPage")
	@ResponseBody
	public AjaxStatusInfo<List<User>> gotoPage(Integer pageNo, Integer pageSize, SearchForm searchForm) {
		PageHelper<User> page = userSvc.getPages(pageNo, pageSize, searchForm);

		List<User> result = page.getResult();
		AjaxStatusInfo<List<User>> ajaxStatusInfo = new AjaxStatusInfo<List<User>>(result);

		return ajaxStatusInfo;

	}

	@RequestMapping(value = "search")
	@ResponseBody
	public AjaxStatusInfo<PageHelper<User>> search(SearchForm searchForm) {
		PageHelper<User> page = userSvc.getPages(GlobalParams.PAGE_NO, GlobalParams.PAGE_SIZE, searchForm);

		AjaxStatusInfo<PageHelper<User>> ajaxStatusInfo = new AjaxStatusInfo<PageHelper<User>>(page);

		return ajaxStatusInfo;

	}

	@RequestMapping(value = "delete")
	@ResponseBody
	public AjaxStatusInfo<String> delete(Long id) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		try {
			userSvc.deleteUserById(id);
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
			userSvc.batchDelByIds(ids);
		} catch (Exception e) {
			logger.error("batchDel fail :" + e.getMessage(), e);
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;
	}

	@RequestMapping(value = "initRoleTreeNodes")
	@ResponseBody
	public AjaxStatusInfo<List<TreeNode>> initRoleTreeNodes(Long userId) {
		List<TreeNode> treeNodes = new ArrayList<>();
		if (userId == null) {
			treeNodes = roleSvc.getRoleTreeNodes();
		} else {
			treeNodes = roleSvc.getRoleTreeNodesByUserId(userId);
		}

		AjaxStatusInfo<List<TreeNode>> ajaxStatusInfo = new AjaxStatusInfo<List<TreeNode>>(treeNodes);

		if (userId != null && treeNodes != null && !treeNodes.isEmpty()) {
			ajaxStatusInfo.setMsg(GlobalParams.YES.toString());
		}

		return ajaxStatusInfo;

	}

	@RequestMapping(value = "userRoleBind")
	@ResponseBody
	public AjaxStatusInfo<String> userRoleBind(Long userId, @RequestParam(value = "roleIds[]") Long[] roleIds) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		try {
			userSvc.userRoleBind(userId, roleIds);
		} catch (Exception e) {
			logger.error("userRoleBind fail :" + e.getMessage(), e);
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;
	}

	@RequestMapping(value = "changePwd")
	@ResponseBody
	public AjaxStatusInfo<String> changePwd(Long userId, String newpassword) {
		AjaxStatusInfo<String> ajaxStatusInfo = new AjaxStatusInfo<>();

		try {
			userSvc.changePwd(userId, newpassword);
		} catch (Exception e) {
			logger.error("changePwd fail :" + e.getMessage(), e);
			ajaxStatusInfo.setStatus(GlobalParams.FAIL_CODE);
			ajaxStatusInfo.setMsg(GlobalParams.FAIL_MSG);
		}

		return ajaxStatusInfo;
	}

}
