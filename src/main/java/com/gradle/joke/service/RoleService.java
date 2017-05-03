package com.gradle.joke.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gradle.joke.base.GlobalParams;
import com.gradle.joke.base.PageHelper;
import com.gradle.joke.base.SearchForm;
import com.gradle.joke.base.TreeNode;
import com.gradle.joke.dao.PermissionDao;
import com.gradle.joke.dao.RoleDao;
import com.gradle.joke.dao.UserDao;
import com.gradle.joke.entity.Permission;
import com.gradle.joke.entity.Role;
import com.gradle.joke.entity.User;
import com.gradle.joke.util.DateUtil;
import com.gradle.joke.util.GetUserFromSessionUtil;

/**
 * 
 * <p>
 * Title: RoleService
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
@Service
@Transactional
public class RoleService {

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private PermissionDao permissionDao;

	@Autowired
	private UserDao userDao;

	public void persist(Role role) throws Exception {
		if (role.getId() == null) {
			role.setCreateTime(DateUtil.getNow());
		}

		role.setUpdateTime(DateUtil.getNow());

		roleDao.save(role);
	}

	public Long getTotalCount() {
		return roleDao.count();

	}

	public Page<Role> getPageList(Pageable pageable) {
		return roleDao.findAll(pageable);
	}

	public Role getRoleById(Long id) {
		return roleDao.findOne(id);
	}

	public void deleteRoleById(Long id) {
		roleDao.delete(id);
	}

	public void batchDelByIds(Long[] ids) {
		for (Long id : ids) {
			roleDao.delete(id);
		}
	}

	public PageHelper<Role> getPages(Integer pageNo, Integer pageSize, final SearchForm searchForm) {
		Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "createTime");
		PageHelper<Role> pageHelper = null;

		Page<Role> page = roleDao.findAll(new Specification<Role>() {

			@Override
			public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				return prepareCondition(root, cb, searchForm);

			}
		}, pageable);

		Long totalRecord = roleDao.count(new Specification<Role>() {

			@Override
			public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// TODO Auto-generated method stub
				return prepareCondition(root, cb, searchForm);
			}
		});

		pageHelper = new PageHelper<>(Integer.valueOf(totalRecord.toString()), pageSize);
		pageHelper.setResult(page.getContent());

		return pageHelper;
	}

	private Predicate prepareCondition(Root<Role> root, CriteriaBuilder cb, final SearchForm searchForm) {
		List<Predicate> list = new ArrayList<Predicate>();
		if (StringUtils.isEmpty(searchForm.getEndDate()) && !StringUtils.isEmpty(searchForm.getStartDate())) {
			list.add(cb.greaterThanOrEqualTo(root.get("createTime").as(String.class), searchForm.getStartDate()));
		} else if (!StringUtils.isEmpty(searchForm.getEndDate()) && !StringUtils.isEmpty(searchForm.getStartDate())) {
			list.add(cb.between(root.get("createTime").as(String.class), searchForm.getStartDate(),
					searchForm.getEndDate()));
		} else if (!StringUtils.isEmpty(searchForm.getEndDate()) && StringUtils.isEmpty(searchForm.getStartDate())) {
			list.add(cb.lessThanOrEqualTo(root.get("createTime").as(String.class), searchForm.getEndDate()));
		}

		if (!StringUtils.isEmpty(searchForm.getKeyword())) {
			list.add(cb.like(root.get("roleName").as(String.class), "%" + searchForm.getKeyword() + "%"));
		}
		Predicate[] p = new Predicate[list.size()];
		return cb.and(list.toArray(p));
	}

	public Role findByRoleCode(String roleCode) {
		return roleDao.findByRoleCode(roleCode);
	}

	public Role findByRoleName(String roleName) {
		return roleDao.findByRoleName(roleName);
	}

	public void persist(Role role, Long[] permissionIds) {
		if (role.getId() == null) {
			role.setCreateTime(DateUtil.getNow());
		}

		role.setUpdateTime(DateUtil.getNow());

		if (permissionIds.length > 0) {
			Set<Permission> permissions = new LinkedHashSet<>();
			for (Long id : permissionIds) {
				Permission permission = permissionDao.findOne(id);
				permissions.add(permission);
			}
			role.setPermissionList(permissions);
		} else {
			if (role.getId() != null) {
				Role dbRole = roleDao.findOne(role.getId());
				role.setPermissionList(dbRole.getPermissionList());
			}
		}

		Long operatorId = GetUserFromSessionUtil.getInstance().getCurUserIdFromSession();
		if (operatorId != null) {
			role.setOperatorId(operatorId);
		}
		roleDao.save(role);

	}

	public List<TreeNode> getRoleTreeNodes() {
		List<Role> roles = roleDao.findRoles();
		List<TreeNode> treeNodes = new ArrayList<>();
		TreeNode parentNode = new TreeNode();
		parentNode.setOpen(true);
		parentNode.setChecked(false);
		parentNode.setId(-1L);
		parentNode.setpId(-99L);
		parentNode.setName(GlobalParams.ROLE_PARENT_NAME);
		treeNodes.add(parentNode);
		for (Role role : roles) {
			TreeNode treeNode = new TreeNode();
			treeNode.setOpen(true);
			treeNode.setChecked(false);
			treeNode.setId(role.getId());
			treeNode.setpId(-1L);
			treeNode.setName(role.getRoleName());

			treeNodes.add(treeNode);
		}

		return treeNodes;
	}

	public List<TreeNode> getRoleTreeNodesByUserId(Long userId) {
		List<TreeNode> initTreeNodes = getRoleTreeNodes();
		User user = userDao.findOne(userId);

		if (user != null) {
			Set<Role> checkedRoles = user.getRoleList();
			for (TreeNode treeNode : initTreeNodes) {
				if (GlobalParams.ROLE_PARENT_NAME.equals(treeNode.getName())) {
					if (checkedRoles != null && checkedRoles.size() > 0) {
						treeNode.setChecked(true);
					}

				}
				for (Role role : checkedRoles) {
					if (treeNode.getId().longValue() == role.getId().longValue()) {
						treeNode.setChecked(true);
					}
				}
			}
		}

		return initTreeNodes;
	}

}
