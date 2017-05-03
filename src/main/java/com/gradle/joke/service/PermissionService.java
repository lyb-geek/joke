package com.gradle.joke.service;

import java.util.ArrayList;
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
import com.gradle.joke.entity.Permission;
import com.gradle.joke.entity.Role;
import com.gradle.joke.util.DateUtil;
import com.gradle.joke.util.GetUserFromSessionUtil;

/**
 * 
 * <p>
 * Title: PermissionService
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
@Service
@Transactional
public class PermissionService {
	@Autowired
	private PermissionDao permissionDao;

	@Autowired
	private RoleDao roleDao;

	public void persist(Permission permission) throws Exception {
		if (permission.getId() == null) {
			permission.setCreateTime(DateUtil.getNow());
		} else {
			if (permission.getParentId() == null) {
				Permission dbPermission = permissionDao.findOne(permission.getId());

				if (dbPermission != null) {
					permission.setParentId(dbPermission.getParentId());
				}
			}

			Permission dbPermission = permissionDao.findOne(permission.getId());
			Set<Role> roles = dbPermission.getRoleList();
			permission.setRoleList(roles);
		}

		if (GlobalParams.YES == permission.getIsParentId()) {
			permission.setParentId(-1L);
		}

		permission.setUpdateTime(DateUtil.getNow());

		Long operatorId = GetUserFromSessionUtil.getInstance().getCurUserIdFromSession();
		if (operatorId != null) {
			permission.setOperatorId(operatorId);
		}

		permissionDao.save(permission);

	}

	public Long getTotalCount() {
		return permissionDao.count();

	}

	public Page<Permission> getPageList(Pageable pageable) {
		return permissionDao.findAll(pageable);
	}

	public Permission getPermissionById(Long id) {
		return permissionDao.findOne(id);
	}

	public Permission getPermissionByPermissionName(String permissionName) {
		return permissionDao.findByPermissionName(permissionName);
	}

	public List<Permission> findParentPermissions() {
		return permissionDao.findParentPermissions();
	}

	public void deletePermissionById(Long id) {
		permissionDao.delete(id);
	}

	public void batchDelByIds(Long[] ids) {
		for (Long id : ids) {
			permissionDao.delete(id);
		}
	}

	public List<TreeNode> getPermissionTreeNodes() {
		List<Permission> permissions = permissionDao.findPermissions();
		List<TreeNode> treeNodes = new ArrayList<>();
		for (Permission permission : permissions) {
			TreeNode treeNode = new TreeNode();
			if (GlobalParams.YES == permission.getIsParentId()) {
				treeNode.setOpen(true);
				// treeNode.setParent(true);
			}

			treeNode.setChecked(false);
			treeNode.setId(permission.getId());
			treeNode.setpId(permission.getParentId());
			treeNode.setName(permission.getPermissionName());

			treeNodes.add(treeNode);
		}

		return treeNodes;
	}

	public PageHelper<Permission> getPages(Integer pageNo, Integer pageSize, final SearchForm searchForm) {
		Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "createTime");
		PageHelper<Permission> pageHelper = null;

		Page<Permission> page = permissionDao.findAll(new Specification<Permission>() {

			@Override
			public Predicate toPredicate(Root<Permission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				return prepareCondition(root, cb, searchForm);

			}
		}, pageable);

		Long totalRecord = permissionDao.count(new Specification<Permission>() {

			@Override
			public Predicate toPredicate(Root<Permission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// TODO Auto-generated method stub
				return prepareCondition(root, cb, searchForm);
			}
		});

		pageHelper = new PageHelper<>(Integer.valueOf(totalRecord.toString()), pageSize);
		pageHelper.setResult(page.getContent());

		return pageHelper;
	}

	private Predicate prepareCondition(Root<Permission> root, CriteriaBuilder cb, final SearchForm searchForm) {
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
			list.add(cb.like(root.get("permissionName").as(String.class), "%" + searchForm.getKeyword() + "%"));
		}
		Predicate[] p = new Predicate[list.size()];
		return cb.and(list.toArray(p));
	}

	public List<TreeNode> getPermissionTreeNodesByRoleId(Long roleId) {
		List<TreeNode> initTreeNodes = getPermissionTreeNodes();
		Role role = roleDao.findOne(roleId);

		if (role != null) {
			Set<Permission> checkedPermissions = role.getPermissionList();
			for (TreeNode treeNode : initTreeNodes) {
				for (Permission permission : checkedPermissions) {
					if (treeNode.getId().longValue() == permission.getId().longValue()) {
						treeNode.setChecked(true);
					}
				}
			}
		}

		return initTreeNodes;
	}

}
