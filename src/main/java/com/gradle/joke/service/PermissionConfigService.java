package com.gradle.joke.service;

import java.util.ArrayList;
import java.util.List;

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

import com.gradle.joke.base.PageHelper;
import com.gradle.joke.base.SearchForm;
import com.gradle.joke.dao.PermissionConfigDao;
import com.gradle.joke.entity.PermissionConfig;
import com.gradle.joke.util.DateUtil;
import com.gradle.joke.util.GetUserFromSessionUtil;

@Service
@Transactional
public class PermissionConfigService {

	@Autowired
	private PermissionConfigDao permissionConfigDao;

	public List<PermissionConfig> getPermissConfigs() {
		return permissionConfigDao.findAll();
	}

	public void persist(PermissionConfig permissionConfig) throws Exception {
		if (permissionConfig.getId() == null) {
			permissionConfig.setCreateTime(DateUtil.getNow());
		}

		permissionConfig.setUpdateTime(DateUtil.getNow());

		Long operatorId = GetUserFromSessionUtil.getInstance().getCurUserIdFromSession();
		if (operatorId != null) {
			permissionConfig.setOperatorId(operatorId);
		}

		permissionConfigDao.save(permissionConfig);
	}

	public Long getTotalCount() {
		return permissionConfigDao.count();

	}

	public Page<PermissionConfig> getPageList(Pageable pageable) {
		return permissionConfigDao.findAll(pageable);
	}

	public PermissionConfig getPermissionConfigById(Long id) {
		return permissionConfigDao.findOne(id);
	}

	public void deletePermissionConfigById(Long id) {
		permissionConfigDao.delete(id);
	}

	public void batchDelByIds(Long[] ids) {
		for (Long id : ids) {
			permissionConfigDao.delete(id);
		}
	}

	public PageHelper<PermissionConfig> getPages(Integer pageNo, Integer pageSize, final SearchForm searchForm) {
		Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "createTime");
		PageHelper<PermissionConfig> pageHelper = null;

		Page<PermissionConfig> page = permissionConfigDao.findAll(new Specification<PermissionConfig>() {

			@Override
			public Predicate toPredicate(Root<PermissionConfig> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				return prepareCondition(root, cb, searchForm);

			}
		}, pageable);

		Long totalRecord = permissionConfigDao.count(new Specification<PermissionConfig>() {

			@Override
			public Predicate toPredicate(Root<PermissionConfig> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// TODO Auto-generated method stub
				return prepareCondition(root, cb, searchForm);
			}
		});

		pageHelper = new PageHelper<>(Integer.valueOf(totalRecord.toString()), pageSize);
		pageHelper.setResult(page.getContent());

		return pageHelper;
	}

	private Predicate prepareCondition(Root<PermissionConfig> root, CriteriaBuilder cb, final SearchForm searchForm) {
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
			list.add(cb.like(root.get("url").as(String.class), "%" + searchForm.getKeyword() + "%"));
		}
		Predicate[] p = new Predicate[list.size()];
		return cb.and(list.toArray(p));
	}

}
